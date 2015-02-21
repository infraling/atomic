/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import com.google.common.collect.HashBiMap;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.core.corpus.SubGraphService;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.GraphXYLayoutEditPolicy;

/**
 * @author Stephan Druskat
 * 
 */
public class GraphPart extends AbstractGraphicalEditPart {

	private GraphAdapter adapter;
	private Map<SToken, String> tokenTextRegistry;
	public Object removingObject;
	private HashBiMap<String, EObject> visualIDMap = HashBiMap.create();
	private List<Object> dynamicModelChildrenList = new ArrayList<Object>();
	private EList<SToken> sortedTokens = new BasicEList<SToken>();

	public GraphPart(SDocumentGraph model) {
		setModel(model);
		setAdapter(new GraphAdapter());
		setTokenTextRegistry(new HashMap<SToken, String>());
		registerTokenTexts();
	}

	private void registerTokenTexts() {
		String text = getModel().getSTextualDSs().get(0).getSText();
		for (SToken token : getModel().getSTokens()) {
			for (Edge edge : getModel().getOutEdges(token.getSId())) {
				if (edge instanceof STextualRelation) {
					STextualRelation textualRelation = (STextualRelation) edge;
					getTokenTextRegistry().put(token, text.substring(textualRelation.getSStart(), textualRelation.getSEnd()));
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		Figure figure = new FreeformLayer();
		figure.setBorder(new MarginBorder(3)); // FIXME: Remove
		figure.setLayoutManager(new FreeformLayout());
		return figure;
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new GraphXYLayoutEditPolicy());
	}

	@Override
	protected List<Object> getModelChildren() {
		 List<Object> modelChildren = new ArrayList<Object>();
		// for (SToken token : getModel().getSTokens()) {
		// if (token.getSDocumentGraph() == getModel()) {
		// modelChildren.add(token);
		// if (!getVisualIDMap().containsValue(token))
		// getVisualIDMap().put("T" + (getModel().getSTokens().indexOf(token) +
		// 1), token);
		// }
		// }
		// for (SStructure structure : getModel().getSStructures()) {
		// if (structure.getSDocumentGraph() == getModel()) {
		// modelChildren.add(structure);
		// if (!getVisualIDMap().containsValue(structure))
		// getVisualIDMap().put("N" +
		// (getModel().getSStructures().indexOf(structure) + 1), structure);
		// }
		// }
		// for (SSpan span : getModel().getSSpans()) {
		// if (span.getSDocumentGraph() == getModel()) {
		// modelChildren.add(span);
		// if (!getVisualIDMap().containsValue(span))
		// getVisualIDMap().put("S" + (getModel().getSSpans().indexOf(span) +
		// 1), span);
		// }
		// }
		// addRelationIDsToVisualIDMap();
		// return modelChildren;
		if (getSortedTokens() != null) {
			modelChildren.addAll(getSortedTokens());
//			modelChildren.addAll(getDynamicModelChildrenList());
			if (!getSortedTokens().isEmpty()) {
				List<Node> others = SubGraphService.getSentenceGraph(getSortedTokens());
				System.err.println("OTHERS: " + others);
				modelChildren.addAll(SubGraphService.getSentenceGraph(getSortedTokens()));
			}
			return modelChildren;
//			return Arrays.asList(getSortedTokens().toArray());
		}
		return null;
//		else {
//			return Arrays.asList(getModel().getSTokens().toArray());
//		}
	}

	private void addRelationIDsToVisualIDMap() {
		for (SDominanceRelation dominanceRel : getModel().getSDominanceRelations()) {
			if (dominanceRel.getSDocumentGraph() == getModel()) {
				if (!getVisualIDMap().containsValue(dominanceRel))
					getVisualIDMap().put("D" + (getModel().getSDominanceRelations().indexOf(dominanceRel) + 1), dominanceRel);
			}
		}
		for (SSpanningRelation spanningRel : getModel().getSSpanningRelations()) {
			if (spanningRel.getSDocumentGraph() == getModel()) {
				if (!getVisualIDMap().containsValue(spanningRel))
					getVisualIDMap().put("R" + (getModel().getSSpanningRelations().indexOf(spanningRel) + 1), spanningRel);
			}
		}
		for (SOrderRelation orderRel : getModel().getSOrderRelations()) {
			if (orderRel.getSDocumentGraph() == getModel()) {
				if (!getVisualIDMap().containsValue(orderRel))
					getVisualIDMap().put("O" + (getModel().getSOrderRelations().indexOf(orderRel) + 1), orderRel);
			}
		}
		for (SPointingRelation pointingRel : getModel().getSPointingRelations()) {
			if (pointingRel.getSDocumentGraph() == getModel()) {
				if (!getVisualIDMap().containsValue(pointingRel))
					getVisualIDMap().put("P" + (getModel().getSPointingRelations().indexOf(pointingRel) + 1), pointingRel);
			}
		}

	}

	public SDocumentGraph getModel() {
		return (SDocumentGraph) super.getModel();
	}

	/**
	 * @return the adapter
	 */
	public GraphAdapter getAdapter() {
		return adapter;
	}

	/**
	 * @param adapter
	 *            the adapter to set
	 */
	public void setAdapter(GraphAdapter adapter) {
		this.adapter = adapter;
	}

	/**
	 * @author Stephan Druskat
	 * 
	 */
	public class GraphAdapter extends EContentAdapter {

		@Override
		public void notifyChanged(Notification n) {
			switch (n.getEventType()) {
			case Notification.REMOVE:
				refreshChildren();
				break;
			case Notification.ADD:
				refreshChildren();
				break;
			case Notification.SET:
				refreshChildren();
				break;

			default:
				break;
			}
		}

		@Override
		public Notifier getTarget() {
			return (SDocumentGraph) getModel();
		}

		@Override
		public boolean isAdapterForType(Object type) {
			return type.equals(SDocumentGraph.class);
		}

	}

	@Override
	public void activate() {
		if (!isActive()) {
			((SDocumentGraph) getModel()).eAdapters().add(getAdapter());
		}
		super.activate();
	}

	@Override
	public void deactivate() {
		if (isActive()) {
			((SDocumentGraph) getModel()).eAdapters().remove(getAdapter());
		}
		super.deactivate();
	}

	/**
	 * @return the tokenTextRegistry
	 */
	public Map<SToken, String> getTokenTextRegistry() {
		return tokenTextRegistry;
	}

	/**
	 * @param tokenTextRegistry
	 *            the tokenTextRegistry to set
	 */
	public void setTokenTextRegistry(Map<SToken, String> tokenTextRegistry) {
		this.tokenTextRegistry = tokenTextRegistry;
	}

	/**
	 * @return the visualIDMap
	 */
	public HashBiMap<String, EObject> getVisualIDMap() {
		return visualIDMap;
	}

	/**
	 * @param visualIDMap
	 *            the visualIDMap to set
	 */
	public void setVisualIDMap(HashBiMap<String, EObject> visualIDMap) {
		this.visualIDMap = visualIDMap;
	}

	/**
	 * @return the dynamicModelChildrenList
	 */
	public List<Object> getDynamicModelChildrenList() {
		return dynamicModelChildrenList;
	}

	/**
	 * @param dynamicModelChildrenList
	 *            the dynamicModelChildrenList to set
	 */
	public void setDynamicModelChildrenList(ArrayList<Object> dynamicModelChildrenList) {
		this.dynamicModelChildrenList = dynamicModelChildrenList;
	}

	/**
	 * @param tokens
	 */
	public void setSortedTokens(EList<SToken> tokens) {
		this.sortedTokens = tokens;
	}

	/**
	 * @return the sortedTokens
	 */
	public EList<SToken> getSortedTokens() {
		return sortedTokens;
	}

}
