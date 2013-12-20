/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.edit_policies.SDocumentGraphXYLayoutEditPolicy;

/**
 * @author Stephan Druskat
 *
 */
public class SDocumentGraphEditPart extends AbstractGraphicalEditPart {
	
	private SDocumentGraphAdapter adapter;
	private HashMap<SToken, Integer> tokenMap; // Perhaps change to HashTable later if sync is needed
	
	public SDocumentGraphEditPart(SDocumentGraph model) {
		super();
		setModel(model);
		adapter = new SDocumentGraphAdapter();
		setAdapter(adapter);
		List<SToken> tokenList = ((SDocumentGraph) getModel()).getSTokens();
		tokenMap = new HashMap<SToken, Integer>();
		for (int i = 0; i < tokenList.size(); i++) {
			tokenMap.put(tokenList.get(i), i);
		}

	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	@Override
	protected IFigure createFigure() {
		FreeformLayer layer = new FreeformLayer();
		layer.setLayoutManager(new FreeformLayout());
		layer.setBorder(new LineBorder(1));
		return layer;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new SDocumentGraphXYLayoutEditPolicy());
	}
	
	@Override 
	protected List<EObject> getModelChildren() { // FIXME: Currently only some model elements taken into account
		List<EObject> childrenList = new ArrayList<EObject>();
		SDocumentGraph graph = (SDocumentGraph) getModel();
		childrenList.addAll(graph.getSTokens());
		childrenList.addAll(graph.getSStructures());
		childrenList.addAll(graph.getSSpans());
		return childrenList;
	}
	
	@Override 
	public void activate() {
		if(!isActive()) {
			((SDocumentGraph) getModel()).eAdapters().add(adapter);
	    }
		super.activate();
	}
	 
	@Override 
	public void deactivate() {
		if(isActive()) {
			((SDocumentGraph) getModel()).eAdapters().remove(adapter);
		}
		super.deactivate();
	}
	
	public class SDocumentGraphAdapter extends EContentAdapter {
		 
	    @Override public void notifyChanged(Notification notification) {
	    	refreshChildren();
	    }
	 
		@Override public Notifier getTarget() {
	    	return (SDocumentGraph) getModel();
	    }
	 
	    @Override public boolean isAdapterForType(Object type) {
	    	return type.equals(SDocumentGraph.class);
	    }

		@Override
		public void setTarget(Notifier newTarget) {
			// TODO Auto-generated method stub
		}
	}

	public SDocumentGraphAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(SDocumentGraphAdapter adapter) {
		this.adapter = adapter;
	}

	public HashMap<SToken, Integer> getTokenMap() {
		return tokenMap;
	}

}

