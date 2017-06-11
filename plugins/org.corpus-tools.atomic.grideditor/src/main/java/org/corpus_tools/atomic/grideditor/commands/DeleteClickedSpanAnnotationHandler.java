/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.utils.CellUtils;
import org.corpus_tools.salt.common.SSpan;
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
public class DeleteClickedSpanAnnotationHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		Range<Integer> indexRange = CellUtils.getRowIndicesForCell(clickedCell);
		Object value = clickedCell.getDataValue();
		int rowIndex = clickedCell.getRowIndex();
		int colIndex = clickedCell.getColumnIndex();
		if (value instanceof SAnnotation && ((SAnnotation) value).getContainer() instanceof SSpan) {
			SSpan parent = null;
			// Remove this annotation from the parent span
			(parent = (SSpan) ((SAnnotation) value).getContainer()).removeLabel(((SAnnotation) value).getQName());
			// If, now, the parent span doesn't contain any annotations, delete
			// it
			if (parent.getAnnotations().size() == 0) {
				parent.getGraph().removeNode(parent);
			}
			Row row = grid.getRowMap().get(rowIndex);
			if (!clickedCell.isSpannedCell()) {
				// If the cell doesn't span more than one row, just set the
				// value to `null`.
				row.put(clickedCell.getColumnIndex(), grid.getHeaderMap().get(clickedCell.getColumnIndex()), null);
			}
			else {
				for (int i = indexRange.lowerEndpoint(); i < indexRange.upperEndpoint() + 1; i++) {
					row = grid.getRowMap().get(i);
					if (row != null) {
						if (row.get(colIndex).getValue() == value) {
							row.put(colIndex, grid.getHeaderMap().get(colIndex), null);
							grid.getRowMap().put(i, row);
						}
					}
				}
			}
			table.refresh();
		}
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}
}
