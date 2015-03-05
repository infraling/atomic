/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphElementRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.ElementDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.NodeGraphicalNodeEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.MultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 * 
 */
public class TokenPart extends AbstractGraphicalEditPart implements NodeEditPart {

	private TokenAdapter adapter;

	public TokenPart() {
		super();
		setAdapter(new TokenAdapter());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new NodeFigure(GraphElementRegistry.getIDForElement(getModel(), getModel().getSDocumentGraph()), NodeFigure.TOKEN_MODEL);
	}

	@Override
	protected void refreshVisuals() {
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added,
		// which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);

		int x = PartUtils.getTokenX((GraphPart) getParent(), getModel(), getFigure());
		// FIXME TODO y value hardcoded! Make switchable via prefs
		((GraphPart) getParent()).setLayoutConstraint(this, getFigure(), new Rectangle(x, 500, getFigure().getPreferredSize().width, getFigure().getPreferredSize().height)); // FIXME:
																																												// Fixed
																																												// y
																																												// coord
																																												// (10).
																																												// Make
																																												// settable
																																												// in
																																												// Prefs?
		super.refreshVisuals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ElementDirectEditPolicy());
		NonResizableEditPolicy selectionPolicy = new NonResizableEditPolicy();
		selectionPolicy.setDragAllowed(false);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionPolicy);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeGraphicalNodeEditPolicy());
	}

	@Override
	protected List<Object> getModelChildren() {
		List<Object> childrenList = new ArrayList<Object>();
		childrenList.add(((GraphPart) getParent()).getTokenTextRegistry().get(getModel()));
		childrenList.addAll(getModel().getSAnnotations());
		return childrenList;
	}

	@Override
	public void performRequest(Request req) {
		if (req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO
																	// Parametrize
																	// for
																	// preferences
																	// sheet
			performDirectEditing();
			getParent().setFocus(true); // So that graph can be saved directly
										// with CTRL + S
		}
		if (req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize
															// for preferences
															// sheet
			System.out.println("requested double-click.");
		}
	}

	private void performDirectEditing() {
		MultiLineDirectEditManager manager = new MultiLineDirectEditManager(this, TextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}

	public SToken getModel() {
		return (SToken) super.getModel();
	}

	/**
	 * @return the adapter
	 */
	public TokenAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public void setAdapter(TokenAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @author Stephan Druskat
	 * 
	 */
	public class TokenAdapter extends EContentAdapter {

		@Override
		public void notifyChanged(Notification n) {
			refresh();
			switch (n.getEventType()) {
			case Notification.REMOVE:
				refreshChildren();
				break;
			case Notification.ADD:
				refreshChildren();
				break;
			case Notification.SET:
				refresh();
				refreshChildren();
				break;
			default:
				break;
			}
		}

		@Override
		public Notifier getTarget() {
			return getModel();
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type.equals(SToken.class);
		}

	}

	@Override
	public void activate() {
		if (!isActive()) {
			getModel().eAdapters().add(getAdapter());
		}
		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			getModel().eAdapters().remove(getAdapter());
		}
		super.deactivate();
	}

	@Override
	protected List<Edge> getModelSourceConnections() {
		SToken model = getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> sourceList = new ArrayList<Edge>();
		if (graph != null) {
			for (Edge edge : graph.getOutEdges(sId)) {
				if (edge instanceof SDominanceRelation || edge instanceof SSpanningRelation || edge instanceof SPointingRelation || edge instanceof SOrderRelation) {
					if (((GraphPart) getParent()).getDynamicModelChildrenList().contains(edge.getTarget()) ) {
						sourceList.add(edge);
					}
				}
			}
		}
		return sourceList;
	}

	@Override
	protected List<Edge> getModelTargetConnections() {
		SToken model = getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> targetList = new ArrayList<Edge>();
		if (graph != null) {
			for (Edge edge : graph.getInEdges(sId)) {
				if (edge instanceof SDominanceRelation || edge instanceof SSpanningRelation || edge instanceof SPointingRelation || edge instanceof SOrderRelation) {
					if (((GraphPart) getParent()).getDynamicModelChildrenList().contains(edge.getSource())) {
						targetList.add(edge);
					}
				}
			}
		}
		return targetList;
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((NodeFigure) getFigure()).getConnectionAnchor();
	}

}
