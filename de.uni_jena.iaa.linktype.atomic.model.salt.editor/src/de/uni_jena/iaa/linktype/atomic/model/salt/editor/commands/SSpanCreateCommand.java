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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;

/**
 * @author Stephan Druskat
 *
 */
public class SSpanCreateCommand extends Command {
	
	private SSpan newSSpan;
	private SDocumentGraph graph;
	private Point constraints;
	
	@Override 
	public void execute() {
		if (constraints != null) annotateSSpanWithConstraintsData();
		newSSpan.setGraph(graph);
	}
	
	@Override 
	public void undo() {
		newSSpan.setGraph(null);
	}
	
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}
	 
	public void setSSpan(SSpan newSSpan) {
		this.newSSpan = newSSpan;
	}

	public void setLocation(Point constraint) {
		constraints = constraint;
	}
	
	/**
	 * Saves the location coordinate information in SProcessingAnnotations, attached to the new SSpan object. FIXME: Add link to API.
	 */
	private void annotateSSpanWithConstraintsData() {
		if (!(newSSpan.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__X") != null) &&
				!(newSSpan.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__Y") != null)) {
			newSSpan.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__X", constraints.x, SDATATYPE.SNUMERIC);
			newSSpan.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__Y", constraints.y, SDATATYPE.SNUMERIC);
		}
		else {
			newSSpan.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__X").setSValue(constraints.x);
			newSSpan.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__Y").setSValue(constraints.y);
		}
//		newSSpan.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__WIDTH", constraints.width, SDATATYPE.SNUMERIC);
//		newSSpan.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__HEIGHT", constraints.height, SDATATYPE.SNUMERIC);
	}

}
