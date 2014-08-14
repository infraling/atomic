/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.StructuredNodeChangeConstraintsCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.SpanPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.StructurePart;

/**
 * @author Stephan Druskat
 *
 */
public class GraphXYLayoutEditPolicy extends XYLayoutEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
	 */
	@Override
	protected Command getCreateCommand(CreateRequest request) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override 
	protected Command createChangeConstraintCommand(EditPart editPart, Object constraint) {
		if (editPart instanceof StructurePart || editPart instanceof SpanPart) {
			StructuredNodeChangeConstraintsCommand command = new StructuredNodeChangeConstraintsCommand();
			command.setNewConstraint((Rectangle) constraint);
			command.setEditPart((AbstractGraphicalEditPart) editPart);
			command.setModel((SStructuredNode) editPart.getModel());
			return command;
		}
		else return null;
	}

}
