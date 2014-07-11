/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.AnnotationDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationPart extends AbstractGraphicalEditPart {
	
		private AnnotationAdapter adapter;

		public AnnotationPart() {
		super();
		setAdapter(new AnnotationAdapter());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new AnnotationFigure();
	}
	
	@Override
	protected void refreshVisuals() {
		((Label) getFigure()).setText(getModel().getSName() + ":" + getModel().getValueString());
		if (getParent() != null) {
			getParent().refresh();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new AnnotationDirectEditPolicy());
		NonResizableEditPolicy selectionPolicy = new NonResizableEditPolicy();
		selectionPolicy.setDragAllowed(false);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionPolicy);
	}

	@Override 
	public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO Parametrize for preferences sheet
			PartUtils.performDirectEditing(this);
		}
		if(req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize for preferences sheet
			PartUtils.performDirectEditing(this);
	    }
	}
	
	public SAnnotation getModel() {
		return (SAnnotation) super.getModel();
	}

	/**
	 * @return the adapter
	 */
	public AnnotationAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(AnnotationAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @author Stephan Druskat
	 *
	 */
	public class AnnotationFigure extends Label {
		public AnnotationFigure() {
			setBorder(new LineBorder(PartUtils.getColor(PartUtils.VERYLIGHTGREY), 1, Graphics.LINE_DOT));
			setOpaque(true);
		}
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class AnnotationAdapter extends EContentAdapter {
		@Override 
		public void notifyChanged(Notification n) {
			if (n.getEventType() == Notification.SET) {
				if (n.getOldValue() instanceof String && n.getNewValue() instanceof String) { // i.e., when the (key or?) value has changed
					refreshVisuals();
				}
				// FIXME: implement below for SRelations or check against higher supertype
				if (!(n.getOldValue() instanceof SNode) && !(n.getNewValue() == null)) { // i.e., if the annotation's parent has not been set to null
					EditPart grandparent = getParent().getParent();
					if (grandparent instanceof GraphPart) {
						for (Object part : grandparent.getChildren()) {
							if (part instanceof TokenPart) {
								((TokenPart) part).refresh();
							}
						}
					}
				}
			}
	    }
	 
		@Override 
		public Notifier getTarget() {
	    	return getModel();
	    }
	 
	    @Override 
	    public boolean isAdapterForType(Object type) {
	    	return type.equals(SAnnotation.class);
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
