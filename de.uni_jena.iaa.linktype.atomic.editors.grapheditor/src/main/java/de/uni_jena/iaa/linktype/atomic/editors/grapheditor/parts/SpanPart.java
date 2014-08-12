/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.StructureFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.AtomicComponentEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.StructuredNodeDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.MultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 *
 */
public class SpanPart extends AbstractGraphicalEditPart {

	private SpanAdapter adapter;
	
	public SpanPart() {
		super();
		setAdapter(new SpanAdapter());
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new StructureFigure(PartUtils.getVisualID((SNode) getModel()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new StructuredNodeDirectEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new NonResizableEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new AtomicComponentEditPolicy());
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
	
	@Override
	protected void refreshVisuals() {
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added, which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);
		
		Rectangle layout = PartUtils.calculateStructuredNodeLayout(this, getModel(), (Figure) getFigure());
		((GraphPart) getParent()).setLayoutConstraint(this, getFigure(), layout); // FIXME: Fixed y coord (10). Make settable in Prefs?
		super.refreshVisuals();
	}
	
	private void performDirectEditing() {
		MultiLineDirectEditManager manager = new MultiLineDirectEditManager(this, TextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}
	
	@Override 
	protected List<Object> getModelChildren() {
		List<Object> childrenList = new ArrayList<Object>();
		childrenList.addAll(getModel().getSAnnotations());
		return childrenList;
	}

	/**
	 * @return the adapter
	 */
	public SpanAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter the adapter to set
	 */
	public void setAdapter(SpanAdapter adapter) {
		this.adapter = adapter;
	}
	
	public SSpan getModel() {
		return (SSpan) super.getModel();
	}

	/**
	 * @author Stephan Druskat
	 *
	 */
	public class SpanAdapter extends EContentAdapter {
		
		@Override 
		public void notifyChanged(Notification n) {
			
		}
		
		@Override public Notifier getTarget() {
	    	return getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SSpan.class);
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
