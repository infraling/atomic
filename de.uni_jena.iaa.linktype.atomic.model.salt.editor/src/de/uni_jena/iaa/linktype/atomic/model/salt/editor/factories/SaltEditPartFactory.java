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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SAnnotationEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SDocumentGraphEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SDominanceRelationEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SPointingRelationEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SProcessingAnnotationEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SStructureEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.STokenEditPart;

/**
 * @author Stephan Druskat
 *
 */
public class SaltEditPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof SDocumentGraph) {
			part = new SDocumentGraphEditPart((SDocumentGraph) model);
		}
		else if (model instanceof SToken) {
			part = new STokenEditPart();
		}
		else if (model instanceof SStructure) {
			part = new SStructureEditPart();
		}
		else if (model instanceof SPointingRelation) {
			part = new SPointingRelationEditPart();
		}
		else if (model instanceof SDominanceRelation) {
			part = new SDominanceRelationEditPart();
		}
		else if (model instanceof SAnnotation) {
			part = new SAnnotationEditPart();
		}
		else if (model instanceof SProcessingAnnotation) {
			part = new SProcessingAnnotationEditPart();
		}
		
		if (part != null) {
			part.setModel(model);
		}
		    
		return part;
	}

}
