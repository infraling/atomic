/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.commands.DocumentGraphAwareHandler;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.gui.AnnotationColInputDialog;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class NewAnnotationColumnHandler extends DocumentGraphAwareHandler {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAnnotationColumnHandler".
	 */
	private static final Logger log = LogManager.getLogger(NewAnnotationColumnHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AnnotationGrid grid = (AnnotationGrid) ((Event) event.getTrigger()).data;
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		
		// Query annotation key
		String namespace = null;
		String name = null;
		AnnotationColInputDialog inputDial = new AnnotationColInputDialog(Display.getDefault().getActiveShell());
		if (inputDial.open() == Window.OK) {
			namespace = inputDial.getNamespace();
			name = inputDial.getName();
		}
		if (name == null || name.isEmpty()) {
			log.trace("User input (new annotation name) is null/empty."); 
			return null;
		}
		for (Row row : grid.getRowMap().values()) {
			row.put(row.getCells().size(), (namespace == null || namespace.isEmpty()) ? name : namespace + "::" + name, null);
		}
		table.refresh();
		return null;
	}

}
