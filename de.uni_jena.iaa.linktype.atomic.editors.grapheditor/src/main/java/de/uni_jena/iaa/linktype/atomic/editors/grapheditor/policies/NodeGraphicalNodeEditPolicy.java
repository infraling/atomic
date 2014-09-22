/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationCreateCommand;

/**
 * @author Stephan Druskat
 *
 */
public class NodeGraphicalNodeEditPolicy extends GraphicalNodeEditPolicy {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCompleteCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		RelationCreateCommand relationResult = (RelationCreateCommand) request.getStartCommand();
		Object target = getHost().getModel();
		if (relationResult.getRelation() instanceof SSpanningRelation) {
			if (!(target instanceof SToken)) {
				return null;
			}
		}
	    relationResult.setTarget((Node) target);
	    return relationResult;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getConnectionCreateCommand(org.eclipse.gef.requests.CreateConnectionRequest)
	 */
	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Object relation = request.getNewObject();
		Object source = getHost().getModel();
		if (relation instanceof SDominanceRelation) {
			if (!(source instanceof SStructure)) {
				return null;
			}
		}
		else if (relation instanceof SSpanningRelation) {
			if (!(source instanceof SSpan)) {
				return null;
			}
		}
		RelationCreateCommand relationResult = new RelationCreateCommand();
	    relationResult.setSource((Node) source);
	    relationResult.setRelation((SRelation) relation);
	    relationResult.setGraph(((Node) getHost().getModel()).getGraph());
	    request.setStartCommand(relationResult);
	    return relationResult;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectTargetCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy#getReconnectSourceCommand(org.eclipse.gef.requests.ReconnectRequest)
	 */
	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

}
