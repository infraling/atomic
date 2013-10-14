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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.elements.STokenFigureBorder;

/**
 * @author Stephan Druskat
 *
 */
public class STokenFigure extends Figure {
	
	private ConnectionAnchor connectionAnchor;
	   
	public STokenFigure(String sTokenID) {
		setOpaque(false);
		final ToolbarLayout annotationLayout = new ToolbarLayout();
		annotationLayout.setSpacing(1);
		setLayoutManager(annotationLayout);
		setBorder(new STokenFigureBorder(sTokenID));
	}
	 
	public ConnectionAnchor getConnectionAnchor() {
		if (connectionAnchor == null) {
			connectionAnchor = new ChopboxAnchor(this);
		}
		return connectionAnchor;
	}

}
