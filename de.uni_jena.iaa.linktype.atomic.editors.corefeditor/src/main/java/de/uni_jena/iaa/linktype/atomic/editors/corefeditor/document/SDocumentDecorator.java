/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * @author Stephan Druskat
 *
 */
public abstract class SDocumentDecorator {

	protected final SDocument decoratedSDocument;
	 
	public SDocumentDecorator(SDocument decoratedSDocument) {
        this.decoratedSDocument = decoratedSDocument;
    }
 
    public SDocumentGraph getGraph() {
    	return decoratedSDocument.getSDocumentGraph();
    }
    
    public EList<SToken> getTokens() {
    	return getGraph().getSTokens();
    }
    
    public String getCorpusText() {
    	return getTextualDS().getSText();
    }
    
    // Assumes that there is only one STextualDS per SDocumentGraph
    public STextualDS getTextualDS() {
    	return getGraph().getSTextualDSs().get(0);
    }

}
