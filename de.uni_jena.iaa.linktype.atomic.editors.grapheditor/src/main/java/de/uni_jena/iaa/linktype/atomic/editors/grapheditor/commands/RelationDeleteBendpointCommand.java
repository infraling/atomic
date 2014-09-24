/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 * 
 */
public class RelationDeleteBendpointCommand extends Command {

	private SRelation relation;
	private int index;
	private Point location;

	@SuppressWarnings("unchecked")
	@Override
	public boolean canExecute() {
		return (relation != null) && (((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).size() > index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		location = ((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).get(index);
		((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).remove(index);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void undo() {
		((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).add(index, location);
	}

	public void setIndex(final int index) {
		this.index = index;
	}

	public void setRelation(final SRelation relation) {
		this.relation = relation;
	}

}
