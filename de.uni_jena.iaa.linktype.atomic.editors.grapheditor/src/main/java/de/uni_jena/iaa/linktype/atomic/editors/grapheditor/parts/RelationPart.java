/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeLocator;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SIdentifiableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphElementRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.IDLabel;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.RelationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart.AnnotationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.ElementDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.RelationConnectionEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.MultiLineDirectEditManager;

/**
 * @author Stephan Druskat
 *
 */
public class RelationPart extends AbstractConnectionEditPart {
	
	public enum RelationType {
		DOMINANCE, SPANNING, POINTING, ORDER
	}

	private RelationType type;
	private RelationAdapter adapter;

	public RelationPart(RelationType type) {
		super();
		this.type = type;
		setAdapter(new RelationAdapter());
	}

	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		String visualID = GraphElementRegistry.getIDForElement((SIdentifiableElement) getModel(), getSDocumentGraph());
		switch (type) {
		case DOMINANCE:
			return new RelationFigure(visualID, RelationFigure.DOMINANCERELATION_MODEL);
		case SPANNING:
			return new RelationFigure(visualID, RelationFigure.SPANNINGRELATION_MODEL);
		case POINTING:
			return new RelationFigure(visualID, RelationFigure.POINTINGRELATION_MODEL);
		case ORDER:
			return new RelationFigure(visualID, RelationFigure.ORDERRELATION_MODEL);

		default:
			break;
		}
		return null;
		
	}
	
	@Override
	protected void refreshVisuals() { // FIXME TODO Refactor
		// Show layers in tooltip
		if (!((SRelation) getModel()).getLayers().isEmpty()) {
			String tooltipText = "Levels:\n";
			for (SLayer layer : ((SRelation)getModel()).getSLayers()) {
				tooltipText = tooltipText + layer.getSName() + "\n";
			}
			((RelationFigure) getFigure()).setTooltipText(tooltipText);
		}
		else {
			((RelationFigure) getFigure()).setTooltipText("\u269BNo assigned level\u269B");
		}

		RelationFigure figure = (RelationFigure) getFigure();
		
		if (((SRelation) getModel()).getLabel("saltCore", "STYPE") != null) {
			figure.getLabel().setText(GraphElementRegistry.getIDForElement((SIdentifiableElement) getModel(), getSDocumentGraph()) + ":" + ((SRelation) getModel()).getLabel("saltCore", "STYPE").getValueString());
		}

		// Reorder figure.getChildren() to avoid IndexOutOfBoundsException
		List<Object> customFigureChildren = new ArrayList<Object>();
		// Make sure the IDLabel is the first element in the list
		for (Object child : figure.getChildren()) {
			if (child instanceof IDLabel)
				customFigureChildren.add(0, child);
		}
		// Get SAnnotations in correct order
		List<EObject> modelChildren = getModelChildren();
		for (Iterator<EObject> iterator = modelChildren.iterator(); iterator.hasNext();) {
			EObject eObject = (EObject) iterator.next();
			if (!(eObject instanceof SAnnotation))
				modelChildren.remove(eObject);
		}
		// Compare List of SANnotations and List of SAnnotationFigures by SName
		for (Iterator<EObject> iterator = modelChildren.iterator(); iterator.hasNext();) {
			String comparableSName = ((SAnnotation) iterator.next()).getSName();
			for (Object figureChild : figure.getChildren()) {
				if (figureChild instanceof AnnotationFigure) {
					if (((AnnotationFigure) figureChild).getText().split(" : ")[0].equalsIgnoreCase(comparableSName)) {
						customFigureChildren.add((AnnotationFigure) figureChild);
					}
				}
			}
		}
		for (Object child : customFigureChildren) {
			if (child instanceof AnnotationFigure) {
				int figureIndex = customFigureChildren.indexOf(child);
				figure.add((AnnotationFigure) child, new RelativeLocator((IFigure) customFigureChildren.get(figureIndex - 1), 0.5, 1.7));
			}
		}
	}


	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ElementDirectEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new RelationConnectionEditPolicy());
	}	
	@Override public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEditing();
		}
	}
	
	private void performDirectEditing() {
		MultiLineDirectEditManager manager = new MultiLineDirectEditManager(this, TextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}
	
	@Override 
	protected List<EObject> getModelChildren() {
		List<EObject> childrenList = new ArrayList<EObject>();
		SAnnotatableElement model = (SAnnotatableElement) getModel();
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}
	
	private SDocumentGraph getSDocumentGraph() {
		return (SDocumentGraph) ((SRelation) getModel()).getSGraph();
	}
	
	/**
	 * @return the adapter
	 */
	public RelationAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(RelationAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class RelationAdapter extends EContentAdapter {
		
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
			case Notification.REMOVING_ADAPTER:
				getParent().refresh();
				if (getFigure().isVisible()) {
					getFigure().setVisible(false);
					deactivate();
				}
				break;
			case Notification.SET:
				if (n.getNotifier() == getModel()) {
					if (n.getOldValue() instanceof SDocumentGraph && n.getNewValue() == null) {
						if (getFigure().isVisible()) {
							getFigure().setVisible(false);
							deactivate();
						}
					}
				}
				break;
			default:
				break;
			}
	    }
	 
		@Override 
		public Notifier getTarget() {
	    	return (Notifier) getModel();
	    }
	 
	    @Override 
	    public boolean isAdapterForType(Object type) {
	    	switch (RelationPart.this.type) {
			case DOMINANCE:
				return type.equals(SDominanceRelation.class);
			case SPANNING:
				return type.equals(SSpanningRelation.class);
			case POINTING:
				return type.equals(SPointingRelation.class);
			case ORDER:
				return type.equals(SOrderRelation.class);
			default:
				break;
			}
			return false;
	    }

	}
	
	@Override 
	public void activate() {
		if(!isActive()) {
			((Notifier) getModel()).eAdapters().add(getAdapter());
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
			((Notifier) getModel()).eAdapters().remove(getAdapter());
		}
		super.deactivate();
	}

}
