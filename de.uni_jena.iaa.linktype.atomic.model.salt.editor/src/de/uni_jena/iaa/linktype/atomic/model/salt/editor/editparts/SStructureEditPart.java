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
import org.eclipse.draw2d.LabeledBorder;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureComponentEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureGraphicalNodeEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicMultiLineDirectEditManager;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.TooltipAndTextCellEditor;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SStructureFigure;

/**
 * @author Stephan Druskat
 *
 */
public class SStructureEditPart extends AbstractGraphicalEditPart implements NodeEditPart {
	
	private SStructureAdapter adapter;
	
	public SStructureEditPart() {
		super();
		adapter = new SStructureAdapter();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		return new SStructureFigure(extractDisplayID());
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new SStructureDirectEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new SStructureGraphicalNodeEditPolicy());
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new SStructureComponentEditPolicy());
	}
	 
	@Override public void performRequest(Request req) {
		if(req.getType() == RequestConstants.REQ_DIRECT_EDIT) { // TODO Parametrize for preferences sheet
			performDirectEditing();
		}
		if(req.getType() == RequestConstants.REQ_OPEN) { // TODO Parametrize for preferences sheet
			System.out.println("requested double-click."); 
	    }
	}
	
	@Override
	protected void refreshVisuals() {
		SStructureFigure figure = (SStructureFigure) getFigure();
		SStructure model = (SStructure) getModel();
		SDocumentGraphEditPart parent = (SDocumentGraphEditPart) getParent();
		
		// FIXME: Bug fix
		// Sometimes, for n = getModelChildren().size(), n+1 children get added, which leads to a blank line
		if (getFigure().getChildren().size() > getModelChildren().size())
			getFigure().getChildren().remove(getFigure().getChildren().size() - 1);
		
		Rectangle layout = calculateLayout(model, figure);
		
		parent.setLayoutConstraint(this, figure, layout); // FIXME: Let this be calculated dynamically
		parent.refresh();

	}
	
	private String extractDisplayID() {
		String sName = ((SStructure) getModel()).getSName();
		LinkedList<String> displayID = new LinkedList<String>();

		//--- Finds all ints in String and adds them to LinkedList
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(sName); 
		while (m.find()) {
		   displayID.add(m.group());
		}
		
		// Example: SName = "structure123" -> displayID = "N123"
		return "N" + displayID.getFirst(); 
	}

	private Rectangle calculateLayout(SStructure model, SStructureFigure figure) {
		int x = (Integer) model.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__X").getSValue();
		int y = (Integer) model.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__Y").getSValue();
		int width = figure.getPreferredSize().width;
		int height = figure.getPreferredSize().height;
		
		// Do hit testing - with findFigureAt
		if (getFigure().getParent().findFigureAt((x), (y + height)) != null || getFigure().getParent().findFigureAt((x + width), (y + height)) != null) { // I.e., when there's a figure at the bottom center coordinate of the this figure
			IFigure hit = null;
			if (getFigure().getParent().findFigureAt((x + width), (y + height)) != null)
				hit = getFigure().getParent().findFigureAt((x + width), (y + height));
			else hit = getFigure().getParent().findFigureAt((x), (y + height));
//			int oldY = y;
			if (!(hit == getFigure()))
				y = hit.getBounds().y - height - 35; // FIXME: Hardcoded
			// Calculate difference between old y and new y and move all other structure figures up by this difference
//			int yDifference = oldY - y;
//			for (Object child : getParent().getChildren()) {
//				if (child instanceof SStructureEditPart) {
//					SProcessingAnnotation anno = ((SStructure)((SStructureEditPart) child).getModel()).getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__Y");
//					int oldVal = (Integer) anno.getSValue();
//					anno.setSValue(oldVal - yDifference);
//					((SStructureEditPart) child).refresh();
//				}
//			}
			model.getSProcessingAnnotation("ATOMIC_GEF_COORDINATES__Y").setSValue(y);
		}
		
		return new Rectangle(x, y, width, height);
	}

	
	@Override 
	protected List<EObject> getModelChildren() {
		List<EObject> childrenList = new ArrayList<EObject>();
		SStructure model = (SStructure) getModel();
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}
	
	private void performDirectEditing() {
		AtomicMultiLineDirectEditManager manager = new AtomicMultiLineDirectEditManager(this, TooltipAndTextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
		manager.show();
	}
	
	@Override 
	protected List<Edge> getModelSourceConnections() {
		SStructure model = (SStructure) getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> sourceList = new ArrayList<Edge>();
		if (graph != null) 
			sourceList.addAll(graph.getOutEdges(sId));
		return sourceList;
	}
	 
	@Override 
	protected List<Edge> getModelTargetConnections() {
		SStructure model = (SStructure) getModel();
		SDocumentGraph graph = model.getSDocumentGraph();
		String sId = model.getSId();
		List<Edge> targetList = new ArrayList<Edge>();
		if (graph != null)
			targetList.addAll(graph.getInEdges(sId));
	    return targetList;
	}
	
	@Override
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return ((SStructureFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return ((SStructureFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return ((SStructureFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return ((SStructureFigure) getFigure()).getConnectionAnchor();
	}

	@Override
	public void activate() {
		if (!isActive()) {
			((SStructure) getModel()).eAdapters().add(adapter);
		}
		super.activate();
	}
	
	@Override
	public void deactivate() {
		if (isActive()) {
			((SStructure) getModel()).eAdapters().remove(adapter);
		}
		super.deactivate();
	}
	
	/**
	 * Inner class SStructureAdapter 
	 */
	public class SStructureAdapter extends EContentAdapter {
		
		@Override
		public void notifyChanged(Notification notification) {
			switch (notification.getEventType()) {
				case Notification.ADD:
					refresh();
					break;
	
				case Notification.SET:
					if (notification.getNotifier() == getModel() && 
						notification.getOldValue() instanceof SDocumentGraph && 
						notification.getNewValue() == null) {
						if (isActive()) {
							getFigure().setVisible(false); // Hack, in case the figure doesn't get removed.
							deactivate();
						}
					}
					else {
						refresh();
					}
					break;
					
				case Notification.REMOVE:
					break;
						
				default:
					break;
			}
		}
		
		@Override
		public Notifier getTarget() {
			return (SStructure) getModel();
		}

		@Override
		public void setTarget(Notifier newTarget) {
			// Do nothing.
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type.equals(SStructure.class);
		}
	}

}
