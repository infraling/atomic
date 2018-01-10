/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
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

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class MergeCellsToSpanHandler extends AbstractHandler {
	
	private static final Logger log = LogManager.getLogger(MergeCellsToSpanHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		Collection<ILayerCell> selectedCells = (Collection<ILayerCell>) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		// FIXME Use try catch for ClassCastExceptions for above code
		SDocumentGraph graph = grid.getGraph();
		final List<SToken> sortedTokens = graph.getSortedTokenByText();
		ArrayList<ILayerCell> sortedSelectedCells = new ArrayList<ILayerCell>(selectedCells);
		sortedSelectedCells.sort(Comparator.comparing(ILayerCell::getRowIndex));
		String header = grid.getColumnHeaderMap().get(sortedSelectedCells.get(0).getColumnIndex());
		int columnIndex = sortedSelectedCells.get(0).getColumnIndex();
		/*
		 * Collect covered tokens and first annotation for selected cells, 
		 * then remove all spans and create a new span with the first annotation.
		 * TODO Document this behaviour (only the first annotation survives)!
		 */
		Set<SSpan> spansToDelete = new HashSet<>();
		List<Integer> tokenIndices = new ArrayList<>();
		SAnnotation firstAnnotation = null;
		for (ILayerCell cell : sortedSelectedCells) {
			Object dataValue = cell.getDataValue();
			if (dataValue == null) {
				tokenIndices.add(cell.getRowIndex());
			}
			else if (dataValue instanceof SSpan) {
				SSpan span = (SSpan) dataValue;
				List<SToken> spanTokens = graph.getOverlappedTokens(span);
				for (SToken token : spanTokens) {
					tokenIndices.add(sortedTokens.indexOf(token));
				}
				spansToDelete.add(span);
			}
			else if (dataValue instanceof SAnnotation) {
				SAnnotation annotation = (SAnnotation) dataValue;
				firstAnnotation = firstAnnotation == null ? annotation : firstAnnotation;
				LabelableElement container = annotation.getContainer();
				// TODO In future versions which support token annotations, catch STokens as well
				if (container instanceof SSpan) {
					SSpan span = (SSpan) container;
					List<SToken> spanTokens = graph.getOverlappedTokens(span);
					for (SToken token : spanTokens) {
						tokenIndices.add(sortedTokens.indexOf(token));
					}
					spansToDelete.add(span);
				}
				else {
					log.warn("Container of annotation {} in cell in row {} is not an instance of {}, but of {}: {}.", annotation.toString(), cell.getRowIndex(), SSpan.class.getSimpleName(), container.getClass().getSimpleName());
				}
			}
			else {
				log.warn("Encountered an invalid cell value. Cell values should be an instance of {}, {} or null. Instead, I've found an object of type {}: {}.", SSpan.class.getSimpleName(), SAnnotation.class.getSimpleName(), dataValue.getClass().getSimpleName(), dataValue.toString());
			}
		}
		final SAnnotation finalValue = firstAnnotation;
		List<SToken> tokenList = new ArrayList<>();
		tokenIndices.stream().forEach(i -> {
			tokenList.add(sortedTokens.get(i));
			grid.record(i, columnIndex, header, finalValue);
		});
		for (SSpan span : spansToDelete) {
			grid.getGraph().removeNode(span);
		}
		SSpan newSpan = graph.createSpan(tokenList);
		newSpan.addAnnotation(finalValue);
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
