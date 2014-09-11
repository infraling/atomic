/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		AnnotationDeleteCommand command = new AnnotationDeleteCommand();
		command.setModel((LabelableElement) getHost().getModel());
		return command;
	}

}
