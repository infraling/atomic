/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts;

import java.util.ArrayList;
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
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.policies.GraphXYLayoutEditPolicy;

/**
 * @author Stephan Druskat
 *
 */
public class GraphPart extends AbstractGraphicalEditPart {
	
	private GraphAdapter adapter;
	private Map<SToken, String> tokenTextRegistry;
	public Object removingObject;

	public GraphPart(SDocumentGraph model) {
		setModel(model);
		setAdapter(new GraphAdapter());
		setTokenTextRegistry(new HashMap<SToken, String>());
		registerTokenTexts();
	}

	private void registerTokenTexts() {
		String text = getModel().getSTextualDSs().get(0).getSText();
		for (SToken token : getModel().getSTokens()) {
			for (Edge edge: getModel().getOutEdges(token.getSId())) {
				if (edge instanceof STextualRelation) {
					STextualRelation textualRelation = (STextualRelation) edge;
					getTokenTextRegistry().put(token, text.substring(textualRelation.getSStart(), textualRelation.getSEnd()));
				}
			}
		}
	}

	/* (non-Javadoc)
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

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new GraphXYLayoutEditPolicy());
	}
	
	@Override
	protected List<SNode> getModelChildren() {
		List<SNode> modelChildren = new ArrayList<SNode>();
		for (SToken token : getModel().getSTokens()) {
			if (token.getSDocumentGraph() == getModel()) {
				modelChildren.add(token);
			}
		}
		for (SStructure structure : getModel().getSStructures()) {
			if (structure.getSDocumentGraph() == getModel()) {
				modelChildren.add(structure);
			}
		}
		for (SSpan span : getModel().getSSpans()) {
			if (span.getSDocumentGraph() == getModel()) {
				modelChildren.add(span);
			}
		}
		return modelChildren;
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
	 * @param adapter the adapter to set
	 */
	public void setAdapter(GraphAdapter adapter) {
		this.adapter = adapter;
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class GraphAdapter extends EContentAdapter {
		
		@Override public void notifyChanged(Notification n) {
			switch (n.getEventType()) {
			case Notification.REMOVE:
				refreshChildren();
				break;
			case Notification.ADD:
				refreshChildren();
				break;

			default:
				break;
			}
	    }
	 
		@Override public Notifier getTarget() {
	    	return (SDocumentGraph) getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SDocumentGraph.class);
	    }

	}
	
	@Override 
	public void activate() {
		if(!isActive()) {
			((SDocumentGraph) getModel()).eAdapters().add(getAdapter());
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
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
	 * @param tokenTextRegistry the tokenTextRegistry to set
	 */
	public void setTokenTextRegistry(Map<SToken, String> tokenTextRegistry) {
		this.tokenTextRegistry = tokenTextRegistry;
	}

}
