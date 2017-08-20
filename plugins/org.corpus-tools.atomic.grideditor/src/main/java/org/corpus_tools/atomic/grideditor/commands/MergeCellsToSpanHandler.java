/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.utils.CellUtils;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.graph.LabelableElement;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

import com.google.common.collect.Range;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class MergeCellsToSpanHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		Collection<ILayerCell> selectedCells = (Collection<ILayerCell>) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		// FIXME Use try catch for ClassCastExceptions for above code
		ArrayList<ILayerCell> sortedSelectedCells = new ArrayList<ILayerCell>(selectedCells);
		sortedSelectedCells.sort(Comparator.comparing(ILayerCell::getRowIndex));
		SAnnotation firstAnnotation = null;
		SSpan firstSpan = null;
		Object firstValue = sortedSelectedCells.get(0).getDataValue();
		if (firstValue instanceof SAnnotation) {
			firstAnnotation = (SAnnotation) firstValue;
			if (firstAnnotation.getContainer() instanceof SSpan) {
				firstSpan = firstAnnotation.getContainer();
			}
		}

		List<Integer> tokenIndices = new ArrayList<>();
		sortedSelectedCells.stream().forEach(c -> {
			Row row = grid.getRowMap().get(c.getRowIndex());
			row.put(c.getColumnIndex(), grid.getColumnHeaderMap().get(c.getColumnIndex()), firstValue);
			grid.getRowMap().put(c.getRowIndex(), row);
			if (c.getDataValue() instanceof SAnnotation) {
				SAnnotation annotation = (SAnnotation) c.getDataValue();
				SSpan parent = annotation.getContainer();
				grid.getGraph().removeNode(parent);
				tokenIndices.add(c.getRowIndex());
			}
		});
		table.refresh();
		List<SToken> tokenList = new ArrayList<>();
		tokenIndices.stream().forEach(i -> {
			tokenList.add(grid.getGraph().getSortedTokenByText().get(i));
		});
		grid.getGraph().removeNode(firstSpan);
		grid.getGraph().createSpan(tokenList).addAnnotation(firstAnnotation);
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
