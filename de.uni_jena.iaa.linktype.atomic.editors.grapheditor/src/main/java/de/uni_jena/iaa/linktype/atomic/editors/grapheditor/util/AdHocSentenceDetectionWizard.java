/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.jface.wizard.Wizard;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class AdHocSentenceDetectionWizard extends Wizard {
	
	private SDocumentGraph graph;

	/**
	 * @param sDocumentGraph 
	 * 
	 */
	public AdHocSentenceDetectionWizard(SDocumentGraph sDocumentGraph) {
		super();
		this.setGraph(sDocumentGraph);
		setNeedsProgressMonitor(false);
	}
	
	@Override
	public void addPages() {
		AdHocSentenceDetectionPage page = new AdHocSentenceDetectionPage("Sentence detection", getGraph());
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
