/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.TokenFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class TokenPart extends AbstractGraphicalEditPart {
	
	private TokenAdapter adapter;

	public TokenPart() {
		super();
		setAdapter(new TokenAdapter());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new TokenFigure(PartUtils.getVisualID(getModel()));
	}
	
	@Override
	protected void refreshVisuals() {
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added, which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);
		
		SToken model = getModel();
		int x = PartUtils.getTokenX(getViewer(), model, getFigure());
		((GraphPart) getParent()).setLayoutConstraint(this, getFigure(), new Rectangle(x, 10, getFigure().getPreferredSize().width, getFigure().getPreferredSize().height)); // FIXME: Fixed y coord (10). Make settable in Prefs?
		super.refreshVisuals();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		NonResizableEditPolicy selectionPolicy = new NonResizableEditPolicy();
		selectionPolicy.setDragAllowed(false);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionPolicy);
	}
	
	@Override 
	protected List<Object> getModelChildren() {
		List<Object> childrenList = new ArrayList<Object>();
		childrenList.add(((GraphPart) getParent()).getTokenTextRegistry().get(getModel()));
		childrenList.addAll(getModel().getSAnnotations());
		return childrenList;
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
	 * @param adapter the adapter to set
	 */
	public void setAdapter(TokenAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class TokenAdapter extends EContentAdapter {

		@Override public void notifyChanged(Notification n) {
			refreshChildren();
	    }
	 
		@Override public Notifier getTarget() {
	    	return getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SToken.class);
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
