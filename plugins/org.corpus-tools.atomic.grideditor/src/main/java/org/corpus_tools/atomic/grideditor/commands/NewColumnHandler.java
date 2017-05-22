/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import org.corpus_tools.atomic.api.commands.DocumentGraphAwareHandler;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.widgets.Event;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class NewColumnHandler extends DocumentGraphAwareHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AnnotationGrid grid = (AnnotationGrid) ((Event) event.getTrigger()).data;
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		for (Row row : grid.getRowMap().values()) {
			row.put(row.getCells().size(), "NEW" + Math.random(), null);
		}
		table.refresh();
		return null;
	}

}
