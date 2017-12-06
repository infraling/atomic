/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
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
 * Note that deleting a token
 * 
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class PurgeTokenHandler extends AbstractHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		new TokenHandlerUtil(clickedCell, grid).deleteToken(true);
		// Refresh table widget and set editor dirty
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
