/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.tools;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.swt.widgets.Display;

import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SDominanceRelationEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SPointingRelationEditPart;

/**
 * @author Stephan Druskat
 *
 */
public class ConnectionCreationAndDirectEditTool extends ConnectionCreationTool {
	
	/**
	 * Method that is called when the gesture to create the connection has been
	 * received. Subclasses may extend or override this method to do additional
	 * creation setup, such as prompting the user to choose an option about the
	 * connection being created. Returns <code>true</code> to indicate that the
	 * connection creation succeeded.
	 * 
	 * @return <code>true</code> if the connection creation was performed
	 */
	@Override
	protected boolean handleCreateConnection() {
		super.handleCreateConnection();
		EditPartViewer viewer = getCurrentViewer();
		final Object model = ((CreateConnectionRequest) getTargetRequest()).getNewObject();
		if (model == null || viewer == null) {
			return false;
		}
			     
		final Object editPartObject = getCurrentViewer().getEditPartRegistry().get(model);
		if(editPartObject instanceof EditPart) {
			Display.getCurrent().asyncExec(new Runnable() {
			         
				@Override public void run() {
					EditPart part = (EditPart) editPartObject;
					if (editPartObject instanceof SDominanceRelationEditPart)
						((SDominanceRelationEditPart) editPartObject).setLabelled(true);
					else if (editPartObject instanceof SPointingRelationEditPart)
						((SPointingRelationEditPart) editPartObject).setLabelled(true);
			        Request request = new DirectEditRequest();
			        part.performRequest(request);
			        part.refresh();
				}
			});
		}
		return true;
	}

}
