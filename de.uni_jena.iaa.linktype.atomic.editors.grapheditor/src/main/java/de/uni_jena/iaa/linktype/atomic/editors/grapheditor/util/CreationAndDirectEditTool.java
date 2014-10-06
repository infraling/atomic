/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.CreationTool;
import org.eclipse.swt.widgets.Display;

/**
 * @author Stephan Druskat
 *
 */
public class CreationAndDirectEditTool extends CreationTool {
	
	@Override 
	protected void performCreation(int button) {
		super.performCreation(button);
		EditPartViewer viewer = getCurrentViewer();
		final Object model = getCreateRequest().getNewObject();
		if (model == null || viewer == null) {
			return;
		}
			     
		final Object o = viewer.getEditPartRegistry().get(model);
		if (o instanceof EditPart) {
			Display.getCurrent().asyncExec(new Runnable() {
			         
				@Override 
				public void run() {
					EditPart part = (EditPart) o;
			        Request request = new DirectEditRequest();
			        part.performRequest(request);
				}
			});
		}
	}
}
