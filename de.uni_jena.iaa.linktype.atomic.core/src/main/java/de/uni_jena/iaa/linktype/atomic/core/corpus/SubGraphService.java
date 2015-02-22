/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * @author Stephan Druskat
 * 
 * FIXME: Add Javadoc
 * 
 */
public class SubGraphService {

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
