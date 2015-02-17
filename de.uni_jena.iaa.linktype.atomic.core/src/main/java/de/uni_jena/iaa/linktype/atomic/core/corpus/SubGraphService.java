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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * @author Stephan Druskat
 * 
 * FIXME: Add Javadoc
 * 
 */
public class SubGraphService {

	/**
	 * @param span
	 * @return
	 */
	public static List<Node> getSentenceGraph(SSpan span) {
		EList<SToken> tokens = retrieveOverlappedTokensFromSpan(span);
		System.err.println(tokens);
		ArrayList<Node> subGraph = new ArrayList<Node>();
		subGraph.addAll(tokens);
		SentenceGraphTraverser traverser = new SentenceGraphTraverser();
		traverser.setTokenSet(new HashSet<SToken>(tokens));
		traverser.setGraph(span.getSDocumentGraph());
		span.getSDocumentGraph().traverse(tokens, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_BREADTH_FIRST, "subtree", traverser, false);
//		subGraph.addAll(traverser.getIncludedNodes());
		return subGraph;
	}

	private static EList<SToken> retrieveOverlappedTokensFromSpan(SSpan span) {
		EList<SToken> overlappedTokens = span.getSDocumentGraph().getOverlappedSTokens(span, new BasicEList<STYPE_NAME>(Arrays.asList(STYPE_NAME.SSPANNING_RELATION)));
		EList<SToken> sortedTokens = span.getSDocumentGraph().getSortedSTokenByText(overlappedTokens);
		return sortedTokens;
	}

}
