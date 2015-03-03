/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.PartUtils;

/**
 * @author Stephan Druskat
 * 
 */
public class NodeCreateCommand extends Command {

	private SDocumentGraph graph;
	private SStructuredNode model;
	private Point location;
	private List<EditPart> selectedEditParts;

	@Override
	public void execute() {
		getModel().setGraph(getGraph());
		if (getSelectedEditParts() != null && !getSelectedEditParts().isEmpty()) {
			createRelations();
		}
		if (getLocation() != null) {
			SProcessingAnnotation anno = getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS");
			if (anno != null) {
				int[] xy = (int[]) anno.getValue();
				int versionInt = xy[2];
				anno.setValue(new int[] { PartUtils.getRelativeX((SDocumentGraph) getModel().getSGraph(), getModel(), getLocation().x), getLocation().y, versionInt++ });
			}
			else {
				getModel().createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[] { PartUtils.getRelativeX(graph, model, getLocation().x), getLocation().y, 1 }, SDATATYPE.SOBJECT);
			}
		}
	}

	private void createRelations() {
		if (getModel() instanceof SStructure) {
			for (EditPart editPart : getSelectedEditParts()) {
				Object ePModel = editPart.getModel();
				SRelation relation = null;
				if (ePModel instanceof SStructure || ePModel instanceof SToken || ePModel instanceof SSpan) {
					// FIXME either or
					// getGraph().createSRelation(getModel(), (SNode)ePModel,
					// STYPE_NAME.SDOMINANCE_RELATION, null);

					relation = SaltFactory.eINSTANCE.createSDominanceRelation();
					relation.setSSource(getModel());
					relation.setSTarget((SNode) ePModel);
					relation.setGraph(getGraph());
					getModel().eNotify(new NotificationImpl(Notification.ADD, null, relation));
					((SNode) ePModel).eNotify(new NotificationImpl(Notification.ADD, null, relation));
				}
			}
		}
		else if (getModel() instanceof SSpan) {
			for (EditPart editPart : getSelectedEditParts()) {
				Object ePModel = editPart.getModel();
				SRelation relation = null;
				if (ePModel instanceof SToken) {
					relation = SaltFactory.eINSTANCE.createSSpanningRelation();
					relation.setSSource(getModel());
					relation.setSTarget((SNode) ePModel);
					relation.setGraph(getGraph());
					getModel().eNotify(new NotificationImpl(Notification.ADD, null, relation));
					((SNode) ePModel).eNotify(new NotificationImpl(Notification.ADD, null, relation));
				}
			}
		}

	}

	@Override
	public void undo() {
		getModel().setGraph(null);
	}

	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

	/**
	 * @return the graph
	 */
	private SDocumentGraph getGraph() {
		return graph;
	}

	public void setModel(SStructuredNode node) {
		this.model = node;
	}

	/**
	 * @return the model
	 */
	private SStructuredNode getModel() {
		return model;
	}

	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * @return the location
	 */
	private Point getLocation() {
		return location;
	}

	public void setSelectedEditParts(List<EditPart> selectedEditParts) {
		this.selectedEditParts = selectedEditParts;
	}

	/**
	 * @return the selectedEditParts
	 */
	private List<EditPart> getSelectedEditParts() {
		return selectedEditParts;
	}

}
