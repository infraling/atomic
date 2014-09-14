/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraph;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class NodeComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		NodeDeleteCommand command = new NodeDeleteCommand();
		SStructuredNode model = (SStructuredNode) getHost().getModel();
		SGraph graph = model.getSGraph();
		command.setModel(model);
		command.setGraph(graph);
		command.setCoordinates(((AbstractGraphicalEditPart) getHost()).getFigure().getBounds());
		return command;
	}
	
}
