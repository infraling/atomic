/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SGraphTraverseHandler;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 * 
 */
public class LinkedSentencesTraverser implements SGraphTraverseHandler {

	private HashSet<SToken> tokenSet;
	private HashSet<SSpan> linkedSentences = new HashSet<SSpan>();

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
		SDocumentGraph graph = (SDocumentGraph) currNode.getSGraph();
		BasicEList<STYPE_NAME> relationTypes = new BasicEList<STYPE_NAME>(Arrays.asList(STYPE_NAME.SDOMINANCE_RELATION, STYPE_NAME.SSPANNING_RELATION, STYPE_NAME.SORDER_RELATION, STYPE_NAME.SPOINTING_RELATION));
		for (Edge edge : graph.getOutEdges(currNode.getSId())) {
			for (SToken token : graph.getOverlappedSTokens((SNode) edge.getTarget(), relationTypes)) {
				if (!getTokenSet().contains(token)) {
					for (Edge tokenEdge : graph.getInEdges(token.getSId())) {
						if (tokenEdge instanceof SSpanningRelation) {
							for (SLayer layer : ((SSpanningRelation) tokenEdge).getSSpan().getSLayers()) {
								if (layer.getSId().equals("ATOMIC::SENTENCES")) {
									getLinkedSentences().add(((SSpanningRelation) tokenEdge).getSSpan());
								}
							}
						}
					}
				}
			}
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
			EList<SToken> overlappedTokens = ((SDocumentGraph) currNode.getSGraph()).getOverlappedSTokens((SNode) edge.getSource(), edgeList);
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
	 * @return the tokenSet
	 */
	public HashSet<SToken> getTokenSet() {
		return tokenSet;
	}

	/**
	 * @param tokenSet
	 *            the tokenSet to set
	 */
	public void setTokenSet(HashSet<SToken> tokenSet) {
		this.tokenSet = tokenSet;
	}

	/**
	 * @return the linkedSentences
	 */
	public HashSet<SSpan> getLinkedSentences() {
		return linkedSentences;
	}

}
