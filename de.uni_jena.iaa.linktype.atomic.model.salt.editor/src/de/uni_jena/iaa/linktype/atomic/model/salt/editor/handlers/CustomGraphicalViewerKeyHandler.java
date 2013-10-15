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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.handlers;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * @author Stephan Druskat
 *
 */
public class CustomGraphicalViewerKeyHandler extends GraphicalViewerKeyHandler {

	public CustomGraphicalViewerKeyHandler(GraphicalViewer viewer) {
		super(viewer);
	}
	
	@Override
	public boolean keyPressed(KeyEvent event) {
		super.keyPressed(event);
		if (event.keyCode == SWT.CR) {
			GraphicalEditPart focus = getFocusEditPart();
			Request request = new DirectEditRequest();
	        focus.performRequest(request);
			return true;
		}
		return super.keyPressed(event);	
	}

}
