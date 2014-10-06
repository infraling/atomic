/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler {

	/**
	 * @param viewer
	 */
	public AtomicGraphicalViewerKeyHandler(GraphicalViewer viewer) {
		super(viewer);
	}
	
	/**
	 * @return <code>true</code> if key pressed indicates a direct edit
	 * Carriage return key = Enter/Return
	 */
	boolean acceptDirectEdit(KeyEvent event) {
		return event.character == SWT.CR;
	}
	
	public boolean keyPressed(KeyEvent event) {
		if (acceptDirectEdit(event)) {
			directEditContainer(event);
			return true;
		}
		return super.keyPressed(event);
	}

	private void directEditContainer(KeyEvent event) {
		GraphicalEditPart focus = getFocusEditPart();
			focus.performRequest(new Request(RequestConstants.REQ_DIRECT_EDIT));
	}

}
