/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationCreateBendpointCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationDeleteBendpointCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationMoveBendpointCommand;

/**
 * @author Stephan Druskat
 * 
 */
public class RelationBendpointEditPolicy extends BendpointEditPolicy {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getCreateBendpointCommand(final BendpointRequest request) {
		RelationCreateBendpointCommand command = new RelationCreateBendpointCommand();

		Point p = request.getLocation();

		command.setRelation((SRelation) request.getSource().getModel());
		RootEditPart root = getHost().getRoot();
		ConnectionLayer layer = (ConnectionLayer) ((ScalableFreeformRootEditPart) root).getLayer(LayerConstants.CONNECTION_LAYER);
		layer.translateToRelative(p);
		command.setLocation(p);
		command.setIndex(request.getIndex());

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getMoveBendpointCommand(final BendpointRequest request) {
		RelationMoveBendpointCommand command = new RelationMoveBendpointCommand();

		Point p = request.getLocation();
		command.setRelation((SRelation) request.getSource().getModel());
		RootEditPart root = getHost().getRoot();
		ConnectionLayer layer = (ConnectionLayer) ((ScalableFreeformRootEditPart) root).getLayer(LayerConstants.CONNECTION_LAYER);
		layer.translateToRelative(p);
		command.setLocation(p);
		command.setIndex(request.getIndex());

		return command;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Command getDeleteBendpointCommand(final BendpointRequest request) {
		RelationDeleteBendpointCommand command = new RelationDeleteBendpointCommand();

		command.setRelation((SRelation) request.getSource().getModel());
		command.setIndex(request.getIndex());
		return command;
	}

}
