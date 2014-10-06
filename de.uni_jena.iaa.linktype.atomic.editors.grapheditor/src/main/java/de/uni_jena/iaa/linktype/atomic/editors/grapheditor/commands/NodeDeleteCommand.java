/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraph;

/**
 * @author Stephan Druskat
 *
 */
public class NodeDeleteCommand extends Command {

	private SStructuredNode model, oldModel;
	private SGraph graph;
	private Rectangle bounds;
	private HashSet<Edge> relations;
	private HashMap<Edge, Node> sources;
	private HashMap<Edge, Node> targets;
	
	// FIXME: Remove this once the following bug is fixed:
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.gef.commands.Command#canUndo()
	 * When deleting an SStructure S, undoing the delete,
	 * then deleting an SSpan P, undoing the delete,
	 * then deleting the SStructure S again, the figure for
	 * S remains visible, and the connections for P are
	 * severed (they reconnect once the SSpanFigure is moved).
	 * Undoing of the last delete of S results in an IndexException
	 * (i.e., index: 306, size: 305, or similar) 
	 */
	@Override
	public boolean canUndo() {
		return false;
	}

	@Override
	public void execute() {
		setOldModel(getModel());
		if (getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS") == null) {
			getModel().createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[]{getBounds().x, getBounds().y, 1}, SDATATYPE.SOBJECT);
		}
		removeRelations();
		getModel().setGraph(null);
	}
	
	private void removeRelations() {
		setRelations(new HashSet<Edge>());
		getRelations().addAll(getGraph().getInEdges(getModel().getSId()));
		getRelations().addAll(getGraph().getOutEdges(getModel().getSId()));
		setSources(new HashMap<Edge, Node>());
		setTargets(new HashMap<Edge, Node>());
		for (Edge edge : getRelations()) {
			getSources().put(edge, edge.getSource());
			getTargets().put(edge, edge.getTarget());
			edge.setGraph(null);
			edge.eNotify(new NotificationImpl(Notification.REMOVE, edge, null));
			edge.getSource().eNotify(new NotificationImpl(Notification.REMOVE, getModel(), null));
			edge.getTarget().eNotify(new NotificationImpl(Notification.REMOVE, getModel(), null));
		}
	}

	@Override
	public void undo() {
		getOldModel().setGraph(getGraph());
		reattachRelations();
	}
	
	private void reattachRelations() {
		for (Edge edge : getRelations()) {
			edge.setSource(getSources().get(edge));
			edge.setTarget(getTargets().get(edge));
			edge.setGraph(getGraph());
			edge.getSource().eNotify(new NotificationImpl(Notification.ADD, null, getModel()));
			edge.getTarget().eNotify(new NotificationImpl(Notification.ADD, null, getModel()));
		}
	}

	/**
	 * @return the model
	 */
	private SStructuredNode getModel() {
		return model;
	}

	public void setModel(SStructuredNode model) {
		this.model = model;		
	}

	/**
	 * @return the oldModel
	 */
	public SStructuredNode getOldModel() {
		return oldModel;
	}

	/**
	 * @param oldModel the oldModel to set
	 */
	public void setOldModel(SStructuredNode oldModel) {
		this.oldModel = oldModel;
	}

	public void setGraph(SGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * @return the graph
	 */
	private SGraph getGraph() {
		return graph;
	}

	public void setCoordinates(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * @return the relations
	 */
	public HashSet<Edge> getRelations() {
		return relations;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(HashSet<Edge> relations) {
		this.relations = relations;
	}

	/**
	 * @return the sources
	 */
	public HashMap<Edge, Node> getSources() {
		return sources;
	}

	/**
	 * @param sources the sources to set
	 */
	public void setSources(HashMap<Edge, Node> sources) {
		this.sources = sources;
	}

	/**
	 * @return the targets
	 */
	public HashMap<Edge, Node> getTargets() {
		return targets;
	}

	/**
	 * @param targets the targets to set
	 */
	public void setTargets(HashMap<Edge, Node> targets) {
		this.targets = targets;
	}

}
