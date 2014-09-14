/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationComponentEditPolicy extends ComponentEditPolicy {
	
	@Override 
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		AnnotationDeleteCommand command = new AnnotationDeleteCommand();
		SAnnotation model = (SAnnotation) getHost().getModel();
		SAnnotatableElement modelParent = model.getSAnnotatableElement();
		command.setModel(model);
		command.setModelParent(modelParent);
		return command;
	}

}
