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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SSpanChangeConstraintsCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SSpanCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SStructureChangeConstraintsCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SStructureCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SSpanEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SStructureEditPart;

/**
 * @author Stephan Druskat
 *
 */
public class SDocumentGraphXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		Command returnCommand = null;
	    
		if (request.getNewObject() instanceof SStructure) {
	    	SStructureCreateCommand command = new SStructureCreateCommand();
	    	Rectangle rect = (Rectangle) getConstraintFor(request);
	    	Point constraint = new Point(rect.x, rect.y);
			command.setLocation(constraint);
	    	command.setGraph((SDocumentGraph) (getHost().getModel()));
	    	command.setSStructure((SStructure) (request.getNewObject()));
	    	returnCommand = command;
	    }
		else if (request.getNewObject() instanceof SSpan) {
	    	SSpanCreateCommand command = new SSpanCreateCommand();
	    	Rectangle rect = (Rectangle) getConstraintFor(request);
	    	Point constraint = new Point(rect.x, rect.y);
			command.setLocation(constraint);
	    	command.setGraph((SDocumentGraph) (getHost().getModel()));
	    	command.setSSpan((SSpan) (request.getNewObject()));
	    	returnCommand = command;
	    }
		return returnCommand;
	}
	
	@Override 
	protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
		if (child instanceof SStructureEditPart) {
			SStructureChangeConstraintsCommand command = new SStructureChangeConstraintsCommand();
			command.setModel((SStructure) child.getModel());
			command.setNewConstraints((Rectangle) constraint);
			return command;
		}
		if (child instanceof SSpanEditPart) {
			SSpanChangeConstraintsCommand command = new SSpanChangeConstraintsCommand();
			command.setModel((SSpan) child.getModel());
			command.setNewConstraints((Rectangle) constraint);
			return command;
		}
		else return null;
	}

}
