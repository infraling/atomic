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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.STokenDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.STokenGraphicalNodeEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicMultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.TooltipAndTextCellEditor;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.STokenFigure;

/**
 * @author Stephan Druskat
 *
 */
public class STokenEditPart extends AbstractGraphicalEditPart implements NodeEditPart {
	
	private STokenAdapter adapter;

	public STokenEditPart() {
		super();
		adapter = new STokenAdapter();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new STokenFigure(extractDisplayID());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new STokenDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new STokenGraphicalNodeEditPolicy());
	}
	
	@Override public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO Parametrize for preferences sheet
			performDirectEditing();
		}
		if(req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize for preferences sheet
			System.out.println("requested double-click."); 
	    }
	}
	
	private void performDirectEditing() {
		AtomicMultiLineDirectEditManager manager = new AtomicMultiLineDirectEditManager(this, TooltipAndTextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}

	@Override
	protected void refreshVisuals() {
		STokenFigure figure = (STokenFigure) getFigure();
		SToken model = (SToken) getModel();
		SDocumentGraphEditPart parent = (SDocumentGraphEditPart) getParent();
		SDocumentGraph graph = (SDocumentGraph) parent.getModel();
		
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added, which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);
		
		Rectangle layout = calculateLayout(figure, parent, model, graph);
		
		parent.setLayoutConstraint(this, figure, layout); // FIXME: Let this be calculated dynamically
		parent.refresh();
	}
	
	private Rectangle calculateLayout(STokenFigure figure, SDocumentGraphEditPart parent, SToken model, SDocumentGraph graph) {
		Rectangle calculatedLayout = null;
		
		List<SToken> tokenList = graph.getSTokens();
		int indexOfThisTokenInTokenList = parent.getTokenMap().get(model);
		
		if (indexOfThisTokenInTokenList != 0) {
			SToken lastSToken = tokenList.get(indexOfThisTokenInTokenList - 1);
			Rectangle lastTokenFigureConstraints = null;
			IFigure parentFigure = parent.getFigure();
			for (Object ep : parent.getChildren()) {
				if (ep instanceof STokenEditPart && ((STokenEditPart) ep).getModel() == lastSToken) 
					lastTokenFigureConstraints = (Rectangle) parentFigure.getLayoutManager().getConstraint(((STokenEditPart) ep).getFigure()); 
			}
			if (!(model.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__X") != null))
					model.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__X", (lastTokenFigureConstraints.x  + lastTokenFigureConstraints.width + 5), SDATATYPE.SNUMERIC);
			calculatedLayout = new Rectangle(lastTokenFigureConstraints.x + lastTokenFigureConstraints.width + 5, lastTokenFigureConstraints.y, figure.getPreferredSize().width, figure.getPreferredSize().height); // TODO Set display correctly
		}
		else { // TODO: Calculate correct position for token line! Viewport?
			if (!(model.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__X") != null))
				model.createSProcessingAnnotation(null, "ATOMIC_GEF_COORDINATES__X", 10, SDATATYPE.SNUMERIC);
			calculatedLayout = new Rectangle(10, 550, figure.getPreferredSize().width, figure.getPreferredSize().height);
		}
		return calculatedLayout;
	}
	
	@Override 
	protected List<EObject> getModelChildren() {
		List<EObject> childrenList = new ArrayList<EObject>();
		SToken model = (SToken) getModel();
		childrenList.add(model.getSProcessingAnnotation("ATOMIC::TOKEN_TEXT"));
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}
	
	@Override 
	protected List<Edge> getModelSourceConnections() {
		SToken model = (SToken) getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> sourceList = new ArrayList<Edge>();
		if (graph != null) {
			for (Edge e : graph.getOutEdges(sId)) {
				if (!(e instanceof STextualRelation))
					sourceList.add(e);
			}
		}
		return sourceList;
	}
	 
	@Override 
	protected List<Edge> getModelTargetConnections() {
		SToken model = (SToken) getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> targetList = new ArrayList<Edge>();
		if (graph != null)
			for (Edge e : graph.getInEdges(sId)) {
				if (!(e instanceof STextualRelation))
					targetList.add(e);
			}
	    return targetList;
	}
	
	private String extractDisplayID() {
		String sName = ((SToken) getModel()).getSName();
		LinkedList<String> displayID = new LinkedList<String>();

		//--- Finds all ints in String and adds them to LinkedList
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sName); 
		while (m.find()) {
		   displayID.add(m.group());
		}
		
		// Example: SName = "token123" -> displayID = "T123"
		return "T" + displayID.getFirst(); 
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((STokenFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((STokenFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((STokenFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((STokenFigure) getFigure()).getConnectionAnchor();
	}
	
	@Override
	public void activate() {
		if (!isActive()) {
			((SToken) getModel()).eAdapters().add(adapter);
		}
		super.activate();
	}
	
	@Override
	public void deactivate() {
		if (isActive()) {
			((SToken) getModel()).eAdapters().remove(adapter);
		}
		super.deactivate();
	}
	
	/**
	 * Inner class STokenAdapter 
	 */
	public class STokenAdapter implements Adapter {
		
		@Override
		public void notifyChanged(Notification notification) {
	    	switch (notification.getEventType()) {
				case Notification.ADD:
					refresh();
					// Refresh all TokenEditParts if an SAnnotation has been added to this.getModel()
					if (notification.getNotifier() == getModel() && notification.getOldValue() == null || notification.getNewValue() instanceof SAnnotation) {
						for (Object child : getParent().getChildren()) {
							if (child instanceof STokenEditPart) {
								((STokenEditPart) child).refresh();
							}
						}
					}
					break;
					
				case Notification.SET:
					String notificationString = null;
					if (notification.getOldValue() instanceof String)
						notificationString = (String) notification.getOldValue();
					refresh();
					if (notificationString.equals("SANNOTATION_HAS_CHANGED")) {
						for (Object child : getParent().getChildren()) {
							if (child instanceof STokenEditPart) {
								((STokenEditPart) child).refresh();
							}
						}
					}
					break;
					
				case Notification.REMOVE:
					getParent().refresh(); // TEST refresh() (not parent.refresh()): This threw an error when removing a SPointRel from token to token
					break;
	
				default:
					break;
				}
		}
		
		@Override
		public Notifier getTarget() {
			return (SToken) getModel();
		}

		@Override
		public void setTarget(Notifier newTarget) {
			// Do nothing.
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type.equals(SToken.class);
		}
	}

}
