/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 *
 */
public class RelationCreateBendpointCommand extends Command {

	private int index;
    private Point location;
    private SRelation relation;
 
    @SuppressWarnings("unchecked")
	@Override 
    public void execute() {
    	if (relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS") == null) {
    		List<Point> bendpoints = new ArrayList<Point>();
        	relation.createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_BENDPOINTS", bendpoints, SDATATYPE.SOBJECT);
    	}
    	((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).add(index, location);
    	relation.eNotify(new NotificationImpl(Notification.ADD, null, location));
	}
 
    @SuppressWarnings("unchecked")
	@Override 
    public void undo() {
    	((List<Point>) relation.getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_BENDPOINTS").getValue()).remove(index);
    	relation.eNotify(new NotificationImpl(Notification.REMOVE, location, null));
    }
 
    public void setIndex(final int index) {
    	this.index = index;
    }
 
    public void setLocation(final Point location) {
    	this.location = location;
    }

	public void setRelation(SRelation model) {
		this.relation = model;
		
	}
 
}
