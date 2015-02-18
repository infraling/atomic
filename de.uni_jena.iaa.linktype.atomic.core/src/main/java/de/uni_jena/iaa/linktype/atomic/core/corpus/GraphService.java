/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * Provides different services related to the SDocumentGraph.
 * 
 * @author Stephan Druskat
 * 
 */
public class GraphService {

	/**
	 * @param span
	 * @return
	 */
	public static EList<SToken> getOverlappedTokens(SSpan span) {
		EList<SToken> overlappedTokens = span.getSDocumentGraph().getOverlappedSTokens(span, new BasicEList<STYPE_NAME>(Arrays.asList(STYPE_NAME.SSPANNING_RELATION)));
		EList<SToken> sortedTokens = span.getSDocumentGraph().getSortedSTokenByText(overlappedTokens);
		return sortedTokens;
	}

	/**
	 * @param list
	 * @return
	 */
	public static EList<SToken> getOrderedTokensForSentenceSpans(List<?> sentenceSpanList) {
		EList<SToken> unorderedTokens = new BasicEList<SToken>();
		for (Object listElement : sentenceSpanList) {
			if (!(listElement instanceof SSpan)) {
				// sentenceSpanList cannot be a valid sentenceSpanList!
				return null;
			}
			SSpan span = (SSpan) listElement;
			unorderedTokens.addAll(getOverlappedTokens(span));
		}
		return ((SSpan) sentenceSpanList.get(0)).getSDocumentGraph().getSortedSTokenByText(unorderedTokens);
	}

}
