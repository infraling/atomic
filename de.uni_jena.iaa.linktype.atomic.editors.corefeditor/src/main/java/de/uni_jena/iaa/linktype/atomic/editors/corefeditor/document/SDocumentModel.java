/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document;

import java.util.HashSet;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * @author Stephan Druskat
 *
 */
public class SDocumentModel extends SDocumentDecorator {

	private HashSet<Object[]> corpusTokens;
	private URI graphURI;

	public SDocumentModel(SDocument decoratedSDocument) {
		super(decoratedSDocument);
		setCorpusTokens(buildCorpusTokens());
	}
	
	private final HashSet<Object[]> buildCorpusTokens() {
		HashSet<Object[]> tokenSet = new HashSet<Object[]>();
		EList<SToken> tokens = super.getGraph().getSortedSTokenByText();
		String text = super.getCorpusText();
		EList<STYPE_NAME> stypeList = new BasicEList<STYPE_NAME>();
        stypeList.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
        for (SToken token: tokens) {
        	EList<SDataSourceSequence> overlap = super.getGraph().getOverlappedDSSequences(token, stypeList);
            SDataSourceSequence ha = overlap.get(0);
            Integer start = ha.getSStart();
            Integer end = ha.getSEnd();
            String tokenText = text.substring(start, end);
            tokenSet.add(new Object[]{tokens.indexOf(token),start,end,tokenText});
        }
		return tokenSet;
	}

	public void setCorpusTokens(HashSet<Object[]> corpusTokens) {
		this.corpusTokens = corpusTokens;
	}

	public HashSet<Object[]> getCorpusTokens() {
		return corpusTokens;
	}

	public void setGraphURI(URI graphURI) {
		this.graphURI = graphURI;
	}

	/**
	 * @return the graphURI
	 */
	public URI getGraphURI() {
		return graphURI;
	}

}
