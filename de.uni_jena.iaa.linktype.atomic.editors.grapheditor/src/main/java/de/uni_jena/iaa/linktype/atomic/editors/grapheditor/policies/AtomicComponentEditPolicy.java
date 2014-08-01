/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.GraphEditorDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		GraphEditorDeleteCommand command = new GraphEditorDeleteCommand();
		command.setModel((LabelableElement) getHost().getModel());
		return command;
	}

}
