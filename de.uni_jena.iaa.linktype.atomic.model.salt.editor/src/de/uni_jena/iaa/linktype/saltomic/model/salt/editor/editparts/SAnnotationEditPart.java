/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SAnnotationComponentEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SAnnotationDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.SAnnotationCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicSingleLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SAnnotationFigure;


/**
 * @author Stephan Druskat
 *
 */
public class SAnnotationEditPart extends AbstractGraphicalEditPart {
	
	SAnnotationAdapter adapter;
	
	public SAnnotationEditPart() {
		super();
		adapter = new SAnnotationAdapter();
	}
	
	@Override public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO Parametrize for preferences sheet
			performDirectEditing();
		}
		if(req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize for preferences sheet
			performDirectEditing();
	    }
	}
	
	private void performDirectEditing() {
		AtomicSingleLineDirectEditManager manager = new AtomicSingleLineDirectEditManager(this, TextCellEditor.class, new SAnnotationCellEditorLocator(getFigure()), getFigure());
		manager.show();
	}

	@Override
	protected IFigure createFigure() {
		return new SAnnotationFigure();
	}
	
	@Override
	protected void refreshVisuals() {
		SAnnotation model = (SAnnotation) getModel();
		Label figure = (SAnnotationFigure) getFigure();
		EditPart parent = getParent();
		
		figure.setText(model.getSName() + ":" + model.getValueString());
		parent.refresh();
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new SAnnotationDirectEditPolicy());
		NonResizableEditPolicy selectionPolicy = new NonResizableEditPolicy();
		selectionPolicy.setDragAllowed(false);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, selectionPolicy);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new SAnnotationComponentEditPolicy());
	}

	@Override 
	public void activate() {
		if(!isActive()) {
			((SAnnotation) getModel()).eAdapters().add(adapter);
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
			((SAnnotation) getModel()).eAdapters().remove(adapter);
		}
		super.deactivate();
	}
	
	public class SAnnotationAdapter implements Adapter {
		 
	    @Override public void notifyChanged(Notification notification) {
	    	switch (notification.getEventType()) {
				case Notification.SET: // i.e., when the SAnnotation's key or value have been set
					// FIXME: Change to refresh() as soon as label-per-annotation has been implemented!
//					getParent().refresh();
					refresh();
					// Necessary for STokenFigures to refresh properly & realign
					if (getParent() instanceof STokenEditPart) {
						STokenEditPart tokenEditPart = (STokenEditPart) getParent();
						((SToken) tokenEditPart.getModel()).eNotify(new NotificationImpl(Notification.SET, "SANNOTATION_HAS_CHANGED", getFigure().getPreferredSize()));
					}
					break;
	
				case Notification.ADD: // i.e., when the SAnnotation has been added
					// FIXME: Change to refresh() as soon as label-per-annotation has been implemented!
//					getParent().refresh();
					refresh();
					break;
					
				case Notification.REMOVE: // i.e., when the SAnnotation has been added
					// FIXME: Change to refresh() as soon as label-per-annotation has been implemented!
//					getParent().refresh();
					refresh();
					break;
	
				default:
					break;
			}
	    }
	 
	    @Override public Notifier getTarget() {
	    	return (SAnnotation) getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SAnnotation.class);
	    }

		@Override
		public void setTarget(Notifier newTarget) {
			// TODO Auto-generated method stub
		}
	}

}
