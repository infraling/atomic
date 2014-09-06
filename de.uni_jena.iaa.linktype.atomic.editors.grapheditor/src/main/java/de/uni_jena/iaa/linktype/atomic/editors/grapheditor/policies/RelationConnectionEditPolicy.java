/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationDeleteCommand;

/**
 * @author Stephan Druskat
 *
 */
public class RelationConnectionEditPolicy extends ConnectionEditPolicy {

	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		RelationDeleteCommand command = new RelationDeleteCommand();
		command.setRelation((SRelation) getHost().getModel());
		return command;
	}


}
