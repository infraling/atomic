/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SDATATYPE;

/**
 * @author Stephan Druskat
 *
 */
public class NodeCreateCommand extends Command {
	
	private SDocumentGraph graph;
	private SStructuredNode model;
	private Point location;

	@Override
	public void execute() {
		getModel().setGraph(getGraph());
		getModel().createSProcessingAnnotation("ATOMIC", "GRAPHEDITOR_COORDS", new int[]{getLocation().x, getLocation().y, 1}, SDATATYPE.SOBJECT);
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

}
