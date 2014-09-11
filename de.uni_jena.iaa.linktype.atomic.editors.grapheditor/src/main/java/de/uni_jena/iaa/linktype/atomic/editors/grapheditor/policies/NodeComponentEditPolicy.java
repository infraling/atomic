/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class NodeComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		System.err.println("REQ " + deleteRequest.getEditParts());
		Command command = new NodeDeleteCommand();
		return command;
	}
	
}
