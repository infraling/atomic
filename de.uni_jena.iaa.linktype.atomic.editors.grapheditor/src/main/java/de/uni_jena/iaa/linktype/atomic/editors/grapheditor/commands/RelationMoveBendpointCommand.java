/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import java.util.List;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 * 
 */
public class RelationMoveBendpointCommand extends Command {

	private Point oldLocation, newLocation;
	private int index;
	private SRelation relation;

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		if (oldLocation == null) {
			oldLocation = ((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).get(index);
		}
		((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).set(index, newLocation);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void undo() {
		((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).set(index, oldLocation);
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public void setRelation(final SRelation relation) {
		this.relation = relation;
	}

	public void setLocation(final Point newLocation) {
		this.newLocation = newLocation;
	}
	
}
