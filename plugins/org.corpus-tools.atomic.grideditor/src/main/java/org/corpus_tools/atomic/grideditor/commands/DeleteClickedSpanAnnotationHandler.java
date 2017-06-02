/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.HashMap;
import java.util.Map;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Event;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class DeleteClickedSpanAnnotationHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		Object value = clickedCell.getDataValue();
		int rowIndex = clickedCell.getRowIndex();
		int colIndex = clickedCell.getColumnIndex();
		if (value instanceof SAnnotation && ((SAnnotation) value).getContainer() instanceof SSpan) {
			SSpan parent = null;
			// Remove this annotation from the parent span
			(parent = (SSpan) ((SAnnotation) value).getContainer()).removeLabel(((SAnnotation) value).getQName());
			// If, now, the parent span doesn't contain any annotations, delete it
			if (parent.getAnnotations().size() == 0) {
				parent.getGraph().removeNode(parent);
			}
			Row row = grid.getRowMap().get(rowIndex);
			if (!clickedCell.isSpannedCell()) {
				// If the cell doesn't span more than one row, just set the value to `null`.
				row.put(clickedCell.getColumnIndex(), grid.getHeaderMap().get(clickedCell.getColumnIndex()), null);
			}
			else {
				// FIXME: Optimize (find the outer bounds of the span
				int spanSize = clickedCell.getRowSpan();
				/* 
				 * Record rows in both directions fromm cell's row index
				 * within a range of 0..spanSize.
				 */
				Map<Integer, Row> candidateRows = new HashMap<>();
				for (int i = (rowIndex - spanSize); i < rowIndex; i++) {
					candidateRows.put(i, grid.getRowMap().get(i));
				}
				for (int j = rowIndex; j < (rowIndex + spanSize); j++) {
					candidateRows.put(j, grid.getRowMap().get(j));
				}
				/* 
				 * Iterate candidate rows, and if value at respective colIndex
				 * == value of clicked cell, remove former value in candidate row
				 * and replace original row with updated candidate row.
				 */
				candidateRows.forEach((i, r) -> {
					if (r != null) {
						if (r.get(colIndex).getValue() == value) {
							r.put(colIndex, grid.getHeaderMap().get(colIndex), null);
							grid.getRowMap().put(i, r);
						}
					}
				});
			}
			table.refresh();
		}
		return null;
	}

}
