/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model;

import java.util.ArrayList;
import java.util.List;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public abstract class SDocumentGraphDecorator {
	
	private final SDocumentGraph decoratedSDocumentGraph;
	private List<Reference> references = new ArrayList<Reference>();
	 
	public SDocumentGraphDecorator(SDocumentGraph decoratedSDocumentGraph) {
        this.decoratedSDocumentGraph = decoratedSDocumentGraph;
    }
	
	public List<Reference> getReferences() {
		return references;
	}
	
	public void addReference(Reference reference) {
		getReferences().add(reference);
	}

	/**
	 * @return the decoratedSDocumentGraph
	 */
	public SDocumentGraph getDecoratedSDocumentGraph() {
		return decoratedSDocumentGraph;
	}
 
}
