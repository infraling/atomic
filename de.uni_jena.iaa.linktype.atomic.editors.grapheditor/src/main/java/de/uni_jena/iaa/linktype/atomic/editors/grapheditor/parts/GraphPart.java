/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Layer;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphService;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.GraphXYLayoutEditPolicy;

/**
 * @author Stephan Druskat
 * 
 */
public class GraphPart extends AbstractGraphicalEditPart {

	private GraphAdapter adapter;
	private SLayer activeLayer;
	private Map<SToken, String> tokenTextRegistry;
	public Object removingObject;
	private List<Object> dynamicModelChildrenList = new ArrayList<Object>();
	private EList<SToken> sortedTokens = new BasicEList<SToken>();
	private HashSet<String> layers = new HashSet<String>();
	private FreeformLayer graphFigure;

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
		graphFigure = new FreeformLayer();
		graphFigure.setBorder(new MarginBorder(3)); // FIXME: Remove
		graphFigure.setLayoutManager(new FreeformLayout());
		graphFigure.addLayoutListener(new LayoutListener.Stub() {

			@Override
			public void postLayout(IFigure host) {
				System.err.println("############ POST LAYOUT ##############");
			}
		});
		return graphFigure;
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
		long a = System.nanoTime();
		long endTime = 0, startTime = 0;
		List<Object> modelChildren = new ArrayList<Object>();
		if (getSortedTokens() != null) {
			// for (SToken sortedToken : getSortedTokens()) {
			// for (SLayer layer : sortedToken.getSLayers()) {
			// if (getLayers().contains(layer)) {
			// modelChildren.add(sortedToken);
			// }
			// }
			// }
			modelChildren.addAll(getSortedTokens());
			if (!getSortedTokens().isEmpty()/* && !getLayers().isEmpty() */) {
				startTime = System.nanoTime();
				List<Node> sentenceGraph = GraphService.getSentenceGraph(getSortedTokens());
				endTime = System.nanoTime();
				// System.err.println("Getting sentenceGraph took " + ((endTime
				// - startTime) / 1000000) + "ms");
				// for (Node node : sentenceGraph) {
				// if (node.getLayers().isEmpty()) {
				// // FIXME
				// System.err.println("Found node without layers: " + node);
				// }
				// }
				long timerStart = System.nanoTime();
				for (Node node : sentenceGraph) {
					SNode sNode = (SNode) node;
					System.out.println("LAYERS EMPTY: " + sNode.getSLayers().isEmpty());

					System.out.println("IN ELSE");
					for (SLayer layer : sNode.getSLayers()) {
						for (String selectedLayer : getLayers()) {
							if (layer.getSName().equals((selectedLayer))) {
								modelChildren.add(sNode);
							}
						}
						// else {
						// System.err.println("Found Layer that's not in SLayers: "
						// + layer);
						// }
						
					}
					if (sNode.getSLayers().isEmpty() && getLayers().contains("\u269B NO ASSIGNED LEVEL \u269B")) { // FIXME
						// Externalize
						// here
						// and
						// in
						// layer
						// view
						// +
						// write
						// unit
						// test!
						System.err.println("CHILD IS LAYERLESS NODE!");
						modelChildren.add(sNode);
					}

				}
				long timerEnd = System.nanoTime();
				System.err.println("Checking for layered nodes took " + ((timerEnd - timerStart) / 1000000) + "ms to complete (" + ((timerEnd - timerStart) / 1000000000) + "s).");

				// modelChildren.addAll(GraphService.getSentenceGraph(getSortedTokens()));
			}
			getDynamicModelChildrenList().clear();
			getDynamicModelChildrenList().addAll(modelChildren);
			System.err.println("Getting model children took " + (System.nanoTime() - a) / 1000000 + "ms vs " + ((endTime - startTime) / 1000000) + "ms for getting the sentence graph");
			return modelChildren;
		}
		return null;
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
				long a = System.nanoTime();
				refreshChildren();
				long b = System.nanoTime();
				System.err.println("II: " + ((b - a) / 1000000000) + "s");
				break;
			case Notification.ADD:
				System.err.println("GRAPH PART CALLS ADD");
				long c = System.nanoTime();
				refreshChildren();
				long d = System.nanoTime();
				System.err.println("III: " + ((d - c) / 1000000) + "ms");

				break;
			// TODO: KANN WOHL RAUS!
			// case Notification.SET:
			// long e = System.nanoTime();
			// // refreshChildren();
			// long f = System.nanoTime();
			// System.err.println(n.getOldValue() + ">" + n.getNewValue() +
			// " IV: " + ((f-e) / 1000000000) + "s");

			// break;

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
		System.err.println("======== GRAPH PART ACTIVATED! ========");
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

	/**
	 * @return the layers
	 */
	public HashSet<String> getLayers() {
		return layers;
	}

	/**
	 * @param layers
	 *            the layers to set
	 */
	public void setLayers(HashSet<String> layers) {
		this.layers = layers;
	}

	/**
	 * @return the activeLayer
	 */
	public SLayer getActiveLayer() {
		return activeLayer;
	}

	/**
	 * @param activeLayer
	 *            the activeLayer to set
	 */
	public void setActiveLayer(SLayer activeLayer) {
		System.err.println("---- setting active layer");
		this.activeLayer = activeLayer;
	}

}
