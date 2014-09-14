/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraph;

/**
 * @author Stephan Druskat
 *
 */
public class NodeDeleteCommand extends Command {

	private SStructuredNode model, oldModel;
	private SGraph graph;
	private Rectangle bounds;

	@Override
	public void execute() {
		setOldModel(getModel());
		if (getModel().getSProcessingAnnotation("ATOMIC::GRAPHEDITOR_COORDS") == null) {
			getModel().createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[]{getBounds().x, getBounds().y, 1}, SDATATYPE.SOBJECT);
		}
//		relations = 
		getModel().setGraph(null);
	}
	
	@Override
	public void undo() {
		getOldModel().setGraph(getGraph());
	}
	
	/**
	 * @return the model
	 */
	private SStructuredNode getModel() {
		return model;
	}

	public void setModel(SStructuredNode model) {
		this.model = model;		
	}

	/**
	 * @return the oldModel
	 */
	public SStructuredNode getOldModel() {
		return oldModel;
	}

	/**
	 * @param oldModel the oldModel to set
	 */
	public void setOldModel(SStructuredNode oldModel) {
		this.oldModel = oldModel;
	}

	public void setGraph(SGraph graph) {
		this.graph = graph;
	}
	
	/**
	 * @return the graph
	 */
	private SGraph getGraph() {
		return graph;
	}

	public void setCoordinates(Rectangle bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}

}
