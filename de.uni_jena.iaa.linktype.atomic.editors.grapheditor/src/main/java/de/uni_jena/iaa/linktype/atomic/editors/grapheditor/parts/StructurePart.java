/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
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
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.AtomicComponentEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.StructuredNodeDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.MultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class StructurePart extends AbstractGraphicalEditPart implements NodeEditPart {
	
	private StructureAdapter adapter;

	public StructurePart() {
		super();
		setAdapter(new StructureAdapter());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new NodeFigure(PartUtils.getVisualID(getModel()), NodeFigure.STRUCTURE_MODEL);
	}

	@Override
	protected void refreshVisuals() {
		// Check if the graph has been auto-layouted
		boolean isGraphLayouted = (getModel().getSDocumentGraph().getSProcessingAnnotation("ATOMIC::IS_LAYOUTED") != null);
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added, which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);
		Rectangle layout;
		if (getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS") != null) {
			Dimension prefSize = getFigure().getPreferredSize();
			int[] xy = (int[]) getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS").getValue();
			layout = new Rectangle(xy[0], xy[1], prefSize.width, prefSize.height);
		}
		else {
			layout = PartUtils.calculateStructuredNodeLayout(this, getModel(), (Figure) getFigure());
		}
		((GraphPart) getParent()).setLayoutConstraint(this, getFigure(), layout); // FIXME: Fixed y coord (10). Make settable in Prefs?super.refreshVisuals();
		getFigure().setBounds(layout);
		if (!isGraphLayouted) {
			hitTest(getFigure());
		}
		super.refreshVisuals();
	}

	private void hitTest(IFigure figure) {
		int nodes = 0;
		SDocumentGraph graph = getModel().getSDocumentGraph();
		nodes = nodes + graph.getSSpans().size();
		nodes = nodes + graph.getSStructures().size();
		int partCounter = 0;
		for (Object value : getViewer().getEditPartRegistry().values()) {
			if (value instanceof StructurePart || value instanceof SpanPart) {
				partCounter++;
			}
		}
		if (nodes == partCounter) {
			PartUtils.doOneTimeReLayout(getViewer().getEditPartRegistry(), graph, getModel());
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new StructuredNodeDirectEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AtomicComponentEditPolicy());
	}
	
	@Override 
	protected List<Object> getModelChildren() {
		List<Object> childrenList = new ArrayList<Object>();
		childrenList.addAll(getModel().getSAnnotations());
		return childrenList;
	}

	@Override 
	public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO Parametrize for preferences sheet
			performDirectEditing();
			getParent().setFocus(true); // So that graph can be saved directly with CTRL + S
		}
		if(req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize for preferences sheet
			System.out.println("requested double-click."); 
	    }
	}
	
	private void performDirectEditing() {
		MultiLineDirectEditManager manager = new MultiLineDirectEditManager(this, TextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}

	public SStructure getModel() {
		return (SStructure) super.getModel();
	}

	/**
	 * @return the adapter
	 */
	public StructureAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(StructureAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class StructureAdapter extends EContentAdapter {
		
		@Override public void notifyChanged(Notification n) {
			refresh();
			switch (n.getEventType()) {
			case Notification.REMOVE:
				refreshChildren();
				break;
			default:
				break;
			}
	    }
	 
		@Override public Notifier getTarget() {
	    	return getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SStructure.class);
	    }

	}
	
	@Override 
	public void activate() {
		if(!isActive()) {
			getModel().eAdapters().add(getAdapter());
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
			getModel().eAdapters().remove(getAdapter());
		}
		super.deactivate();
	}

	@Override 
	protected List<Edge> getModelSourceConnections() {
		SStructure model = getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> sourceList = new ArrayList<Edge>();
		if (graph != null) 
			sourceList.addAll(graph.getOutEdges(sId));
		ArrayList<Edge> removalList = new ArrayList<Edge>();
		for (Iterator iterator = sourceList.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			if (!(edge instanceof SDominanceRelation)) {
				removalList.add(edge);
			}
		}
		sourceList.removeAll(removalList);
		return sourceList;
	}
	 
	@Override 
	protected List<Edge> getModelTargetConnections() {
		SStructure model = getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> targetList = new ArrayList<Edge>();
		if (graph != null)
			targetList.addAll(graph.getInEdges(sId));
		ArrayList<Edge> removalList = new ArrayList<Edge>();
		for (Iterator iterator = targetList.iterator(); iterator.hasNext();) {
			Edge edge = (Edge) iterator.next();
			if (!(edge instanceof SDominanceRelation)) {
				removalList.add(edge);
			}
		}
		targetList.removeAll(removalList);
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
