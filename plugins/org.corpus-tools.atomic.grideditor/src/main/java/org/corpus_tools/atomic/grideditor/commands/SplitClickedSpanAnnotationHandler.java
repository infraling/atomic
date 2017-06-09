/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.utils.CellUtils;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
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
public class SplitClickedSpanAnnotationHandler extends AbstractHandler {
	
	private static final Logger log = LogManager.getLogger(SplitClickedSpanAnnotationHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		Range<Integer> indexRange = CellUtils.getRowIndicesForCell(clickedCell);
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		Object cellObject = clickedCell.getDataValue();
		String ns = null;
		String name = null;
		Object value = null;
		SAnnotation originalAnnotation = null;
		if (cellObject instanceof SAnnotation) {
			originalAnnotation = (SAnnotation) cellObject;
			ns = originalAnnotation.getNamespace();
			name = originalAnnotation.getName();
			value = originalAnnotation.getValue();
		}
		else {
			log.trace("Object value of clicked cell is not an instance of SAnnotation when it should be. Cancelling command execution!");
			return null;
		}
		// Get spanned cells and write new annotation for each
		if (originalAnnotation.getContainer() instanceof SSpan) {
			SSpan parent = null;
			// Remove this annotation from the parent span
			(parent = (SSpan) ((SAnnotation) cellObject).getContainer()).removeLabel(((SAnnotation) cellObject).getQName());
			// If, now, the parent span doesn't contain any annotations, delete it
			if (parent.getAnnotations().size() == 0) {
				parent.getGraph().removeNode(parent);
			}
			SDocumentGraph graph = grid.getGraph();
			List<SToken> sortedTokens = graph.getSortedTokenByText();
			int columnIndex = clickedCell.getColumnIndex();
			String header = grid.getHeaderMap().get(columnIndex);
			/* 
			 * Create a new span with annotation for all tokens in span index range,
			 * and update the grid accordingly.
			 */
			for (int i = indexRange.lowerEndpoint(); i < indexRange.upperEndpoint() + 1; i++) {
				SSpan span = grid.getGraph().createSpan(sortedTokens.get(i));
				SAnnotation annotation = span.createAnnotation(ns, name, value);
				Row row = grid.getRowMap().get(i);
				row.put(columnIndex, header, annotation);
			}
			table.refresh();
		}
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
