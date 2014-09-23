/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Graph;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 *
 */
public class RelationCreateCommand extends Command {
	
	// FIXME TODO Do check on whether executable in Policy and return null for command when check fails 

	private Node target;
	private Node source;
	private SRelation relation;
	private Graph graph;
	
	@Override 
	public void execute() {
		getRelation().setSource(getSource());
		getRelation().setTarget(getTarget());
		getRelation().setGraph(getGraph());
		source.eNotify(new NotificationImpl(Notification.ADD, null, getRelation()));
		target.eNotify(new NotificationImpl(Notification.ADD, null, getRelation()));
	}
			 
	@Override 
	public void undo() {
		getRelation().setSGraph(null);
		source.eNotify(new NotificationImpl(Notification.REMOVE, null, getRelation()));
		target.eNotify(new NotificationImpl(Notification.REMOVE, null, getRelation()));
	}


	public void setTarget(Node node) {
		this.target = node;
	}

	/**
	 * @return the target
	 */
	private Node getTarget() {
		return target;
	}

	public void setSource(Node model) {
		this.source = model;
		
	}

	/**
	 * @return the source
	 */
	private Node getSource() {
		return source;
	}

	public void setRelation(SRelation newObject) {
		this.relation = newObject;
	}

	/**
	 * @return the relation
	 */
	public SRelation getRelation() {
		return relation;
	}

	public void setGraph(Graph graph) {
		this.graph = graph;
	}

	/**
	 * @return the graph
	 */
	private Graph getGraph() {
		return graph;
	}

}
