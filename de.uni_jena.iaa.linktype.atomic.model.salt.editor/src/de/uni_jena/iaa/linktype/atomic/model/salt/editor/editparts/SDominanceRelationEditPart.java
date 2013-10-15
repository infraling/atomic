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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RelativeLocator;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SDominanceRelationConnectionEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SDominanceRelationDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicMultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.TooltipAndTextCellEditor;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SAnnotationFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SDominanceRelationFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.elements.IDLabel;

/**
 * @author Stephan Druskat
 *
 */
public class SDominanceRelationEditPart extends AbstractConnectionEditPart {
	
	private SDominanceRelationAdapter adapter;
	private boolean labelled = false;
	
	public SDominanceRelationEditPart() {
		super();
		adapter = new SDominanceRelationAdapter();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new SDominanceRelationDirectEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ROLE, new SDominanceRelationConnectionEditPolicy());
	}
	
	@Override public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) {
			performDirectEditing();
		}
	}
	
	private void performDirectEditing() {
		AtomicMultiLineDirectEditManager manager = new AtomicMultiLineDirectEditManager(this, TooltipAndTextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}
	
	@Override 
	protected List<EObject> getModelChildren() {
		List<EObject> childrenList = new ArrayList<EObject>();
		SDominanceRelation model = (SDominanceRelation) getModel();
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}

	
	@Override 
	protected IFigure createFigure() {
		return new SDominanceRelationFigure(extractDisplayID());
	}
	
	@Override
	protected void refreshVisuals() { // TODO Refactor
		SDominanceRelationFigure figure = (SDominanceRelationFigure) getFigure();

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
				if (figureChild instanceof SAnnotationFigure) {
					if (((SAnnotationFigure) figureChild).getText().split(":")[0].equalsIgnoreCase(comparableSName)) {
						customFigureChildren.add((SAnnotationFigure) figureChild);
					}
				}
			}
		}
		for (Object child : customFigureChildren) {
			if (child instanceof SAnnotationFigure) {
				int figureIndex = customFigureChildren.indexOf(child);
				figure.add((SAnnotationFigure) child, new RelativeLocator((IFigure) customFigureChildren.get(figureIndex - 1), 0.5, 1.7));
			}
		}
	}
	
	private String extractDisplayID() {
		String sName = ((SDominanceRelation) getModel()).getSName();
		LinkedList<String> displayID = new LinkedList<String>();

		//--- Finds all ints in String and adds them to LinkedList
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sName); 
		while (m.find()) {
		   displayID.add(m.group());
		}
		
		// Example: SName = "dominancerelation123" -> displayID = "DE123"
		return "D" + displayID.getFirst(); // TODO: for "undirected edge"
	}

	/**
	 * @return the labelled
	 */
	public boolean isLabelled() {
		return labelled;
	}

	/**
	 * @param labelled the labelled to set
	 */
	public void setLabelled(boolean labelled) {
		this.labelled = labelled;
	}

	@Override 
	public void activate() {
		if(!isActive()) {
			((SDominanceRelation) getModel()).eAdapters().add(adapter);
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
			((SDominanceRelation) getModel()).eAdapters().remove(adapter);
		}
		super.deactivate();
	}
	
	public class SDominanceRelationAdapter implements Adapter {
		 
	    @Override public void notifyChanged(Notification notification) {
			switch (notification.getEventType()) {
				case Notification.ADD:
					refresh();
					break;
	
				case Notification.SET:
					if (notification.getOldValue() instanceof SDocumentGraph && notification.getNewValue() == null) {
						if (isActive()) {
							removeNotify();
						}
					}
					else
						refresh();
					break;
					
				case Notification.REMOVE:
					refresh();
					break;
	
				default:
					break;
			}
	    }
	 
	    @Override public Notifier getTarget() {
	    	return (SDominanceRelation) getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SDominanceRelation.class);
	    }

		@Override
		public void setTarget(Notifier newTarget) {
			// TODO Auto-generated method stub
		}
	}

}
