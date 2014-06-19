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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
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
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureComponentEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureDirectEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SStructureGraphicalNodeEditPolicy;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicCellEditorLocator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing.AtomicMultiLineDirectEditManager;
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
		int x = -1;
		int y = -1;
		int width = figure.getPreferredSize().width;
		int height = figure.getPreferredSize().height;
		SProcessingAnnotation xAnno = model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_X");
		SProcessingAnnotation yAnno = model.getSProcessingAnnotation("ATOMIC::GEF_COORDS_Y");
		if (xAnno == null && yAnno == null) {
			if (xAnno == null) {
				x = calculateX(model, figure, width);
			}
			if (yAnno == null) {
				y = calculateY(model, figure, height);
			}
			model.createSProcessingAnnotation("ATOMIC", "GEF_COORDS_X", Integer.toString(x));
			model.createSProcessingAnnotation("ATOMIC", "GEF_COORDS_Y", Integer.toString(y));
		}
		else {
			x = Integer.parseInt((String) xAnno.getValue());
			y = Integer.parseInt((String) yAnno.getValue());
		}
		return new Rectangle(x, y, width, height);
	}

	
	private int calculateY(SStructure model, SStructureFigure figure, int height) {
		TreeSet<Integer> ySet = new TreeSet<Integer>();
		for (Edge connection : getModelSourceConnections()) {
			// TODO check if target is a token and has a span above it, then get span figure and go - spanfigure.height!
			IFigure targetFigure = ((GraphicalEditPart) getViewer().getEditPartRegistry().get(connection.getTarget())).getFigure();
			int targetY = ((Rectangle)((GraphicalEditPart) getParent()).getFigure().getLayoutManager().getConstraint(targetFigure)).y;
			ySet.add(targetY);
		}
		return ySet.first() - 50 - height; // FIXME Hardcoded 50, 
	}

	private int calculateX(SStructure model, SStructureFigure figure, int width) {
		TreeMap<Integer, Rectangle> constraintsMap = new TreeMap<Integer, Rectangle>();
		TreeSet<Integer> xSet = new TreeSet<Integer>();
		List<Edge> connections = getModelSourceConnections();
		for (int i = 0; i < connections.size(); i++) {
			IFigure targetFigure = ((GraphicalEditPart) getViewer().getEditPartRegistry().get(connections.get(i).getTarget())).getFigure();
			Rectangle constraints = (Rectangle) ((GraphicalEditPart) getParent()).getFigure().getLayoutManager().getConstraint(targetFigure);
			if (i == (connections.size() - 1))
				xSet.add(constraints.x + constraints.width);
			else
				xSet.add(constraints.x);
			
//			constraintsMap.put(constraints.x, constraints);
		}
		int finalX = 0;
		for (Integer x : xSet) { 
			finalX = finalX + x;
		}
//		return (((constraintsMap.lastEntry().getKey() + constraintsMap.lastEntry().getValue().width) -  constraintsMap.firstEntry().getKey()) /*/ constraintsMap.size()*/) - (width / 2);
		if (connections.size() != 0)
			return (finalX / connections.size()) - (width / 2);
		else
			return 100;
	}

	@Override 
	protected List<EObject> getModelChildren() {
		List<EObject> childrenList = new ArrayList<EObject>();
		SStructure model = (SStructure) getModel();
		childrenList.addAll(model.getSAnnotations());
		return childrenList;
	}
	
	private void performDirectEditing() {
		AtomicMultiLineDirectEditManager manager = new AtomicMultiLineDirectEditManager(this, TextCellEditor.class, new AtomicCellEditorLocator(getFigure()));
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
