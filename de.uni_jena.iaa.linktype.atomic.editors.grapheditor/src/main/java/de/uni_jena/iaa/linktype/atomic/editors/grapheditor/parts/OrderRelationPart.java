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
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.IDLabel;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.RelationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart.AnnotationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.MultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class OrderRelationPart extends AbstractConnectionEditPart {

	private OrderRelationAdapter adapter;

	public OrderRelationPart() {
		super();
		setAdapter(new OrderRelationAdapter());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new RelationFigure(PartUtils.getVisualID(getModel()), RelationFigure.ORDERRELATION_MODEL);
	}
	
	@Override
	protected void refreshVisuals() { // TODO Refactor
		RelationFigure figure = (RelationFigure) getFigure();

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
					if (((AnnotationFigure) figureChild).getText().split(":")[0].equalsIgnoreCase(comparableSName)) {
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
		// TODO Auto-generated method stub

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
		SOrderRelation model = (SOrderRelation) getModel();
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}
	
	public SOrderRelation getModel() {
		return (SOrderRelation) super.getModel();
	}

	/**
	 * @return the adapter
	 */
	public OrderRelationAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(OrderRelationAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class OrderRelationAdapter extends EContentAdapter {
		
		@Override 
		public void notifyChanged(Notification n) {
	    }
	 
		@Override 
		public Notifier getTarget() {
	    	return getModel();
	    }
	 
	    @Override 
	    public boolean isAdapterForType(Object type) {
	    	return type.equals(SOrderRelation.class);
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

}
