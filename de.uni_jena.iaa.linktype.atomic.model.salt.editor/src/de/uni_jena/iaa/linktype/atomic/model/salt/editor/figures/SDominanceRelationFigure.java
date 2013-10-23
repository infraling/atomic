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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.elements.IDLabel;

/**
 * @author Stephan Druskat
 *
 */
public class SDominanceRelationFigure extends PolylineConnection {
	
	private String iDLabelString;
	private IDLabel iDLabel;
	   
	public SDominanceRelationFigure(String labelString) {
		this.iDLabelString = labelString;
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setTemplate(PolygonDecoration.TRIANGLE_TIP);
		setTargetDecoration(decoration);
//		setForegroundColor(ColorConstants.blue);
		iDLabel = new IDLabel(iDLabelString, "SDOMINANCERELATION");
		add(iDLabel, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
	}

	/**
	 * @return the iDLabel
	 */
	public IDLabel getiDLabel() {
		return iDLabel;
	}
	
}
