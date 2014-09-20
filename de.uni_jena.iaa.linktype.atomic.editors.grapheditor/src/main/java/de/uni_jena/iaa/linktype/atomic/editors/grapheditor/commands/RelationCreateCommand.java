/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Graph;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 *
 */
public class RelationCreateCommand extends Command {

	private Node target;
	private Node source;
	private SRelation relation;
	private Graph graph;
	
	@Override
	public boolean canExecute() {
		if (getRelation() instanceof SDominanceRelation) {
			return source != null && source instanceof SStructure && target != null && getRelation() != null;
		}
		else if (getRelation() instanceof SSpanningRelation) {
			return source != null && source instanceof SSpan && (target instanceof SToken || target instanceof SSpan) && target != null && getRelation() != null;
		}
		return false;
	}
			   
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
		graph.getOutEdges(getRelation().getSTarget().getSId()).remove(getRelation());
		graph.getInEdges(getRelation().getSTarget().getSId()).remove(getRelation());
		getRelation().setSGraph(null);
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
	private SRelation getRelation() {
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
