/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
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
	
	/**
	 * @param checkedElement
	 * @return
	 */
	public static HashSet<SSpan> getLinkedSentences(SSpan checkedSentence) {
		long a = System.nanoTime();
		EList<SToken> tokens = GraphService.getOverlappedTokens(checkedSentence);
		LinkedSentencesTraverser traverser = new LinkedSentencesTraverser();
		traverser.setTokenSet(new HashSet<SToken>(tokens));
		checkedSentence.getSDocumentGraph().traverse(tokens, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_BREADTH_FIRST, "linkedSentences", traverser, false);
		long b = System.nanoTime();
		System.err.println("11: getting linked sentences took " + ((b - a) / 1000000 + "ms (" + ((b - a) / 1000000000) + "s) ("));
		return traverser.getLinkedSentences();
	}
	
	/**
	 * @return
	 */
	public static List<Node> getSentenceGraph(EList<SToken> tokens) {
		SDocumentGraph graph = tokens.get(0).getSDocumentGraph();
		ArrayList<Node> subGraph = new ArrayList<Node>();
		SentenceGraphTraverser traverser = new SentenceGraphTraverser();
		traverser.setTokenSet(new HashSet<SToken>(tokens));
		traverser.setGraph(graph);
		graph.traverse(tokens, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_BREADTH_FIRST, "subtree", traverser, false);
		subGraph.addAll(traverser.getNodeSet());
		return subGraph;
	}

}
