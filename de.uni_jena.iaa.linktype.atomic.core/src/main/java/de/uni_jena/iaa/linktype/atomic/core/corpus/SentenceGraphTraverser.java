/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.HashSet;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 * 
 */
public class SentenceGraphTraverser implements SGraphTraverseHandler {

	private HashSet<Node> nodeSet = new HashSet<Node>();
	private HashSet<SToken> tokenSet;
	private SDocumentGraph graph;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler
	 * #nodeReached(de.hu_berlin.german.korpling.saltnpepper.salt.graph.
	 * GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode, long)
	 */
	@Override
	public void nodeReached(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode, SRelation sRelation, SNode fromNode, long order) {
		if (currNode instanceof SToken) {
			// Skip STokens as we already have a **sorted** list of tokens!
		}
		else {
			nodeSet.add(currNode);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler
	 * #nodeLeft(de.hu_berlin.german.korpling.saltnpepper.salt.graph.
	 * GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode, long)
	 */
	@Override
	public void nodeLeft(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SNode currNode, SRelation edge, SNode fromNode, long order) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler
	 * #checkConstraint(de.hu_berlin.german.korpling.saltnpepper.salt.graph.
	 * GRAPH_TRAVERSE_TYPE, java.lang.String,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation,
	 * de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode, long)
	 */
	@Override
	public boolean checkConstraint(GRAPH_TRAVERSE_TYPE traversalType, String traversalId, SRelation edge, SNode currNode, long order) {
		if ((edge != null) && !(edge instanceof SSpanningRelation) && !(edge instanceof SDominanceRelation)) {
			EList<STYPE_NAME> edgeList = new BasicEList<STYPE_NAME>();
			edgeList.add(STYPE_NAME.SDOMINANCE_RELATION);
			edgeList.add(STYPE_NAME.SSPANNING_RELATION);
			EList<SToken> overlappedTokens = getGraph().getOverlappedSTokens((SNode) edge.getSource(), edgeList);
			for (SToken token : overlappedTokens) {
				if (getTokenSet().contains(token)) {
					return true;
				}
			}
			return false;
		}
		return true;
	}

	/**
	 * @param hashSet
	 */
	public void setTokenSet(HashSet<SToken> tokenSet) {
		this.tokenSet = tokenSet;
	}

	/**
	 * @param sDocumentGraph
	 */
	public void setGraph(SDocumentGraph sDocumentGraph) {
		this.graph = sDocumentGraph;
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @return the tokenSet
	 */
	public HashSet<SToken> getTokenSet() {
		return tokenSet;
	}

	/**
	 * @return the nodeSet
	 */
	public HashSet<Node> getNodeSet() {
		return nodeSet;
	}

}
