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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands;

import java.util.ArrayList;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;

/**
 * @author Stephan Druskat
 *
 */
public class SSpanDeleteCommand extends Command { 
	
	private SSpan sSpan;
	private SDocumentGraph graph;
	private ArrayList<Edge> relations;
			 
	@Override
	public void execute() {
		graph = sSpan.getSDocumentGraph();
		if ((graph.getInEdges(sSpan.getSId())) != null || (graph.getOutEdges(sSpan.getSId())) != null) 
				detachRelations();
		sSpan.setSDocumentGraph(null);			
	}
	
	private void detachRelations() {
		relations = new ArrayList<Edge>();
//		linkSources = new HashMap<OPMLink, OPMThing>();
//		linkTargets = new HashMap<OPMLink, OPMThing>();
		relations.addAll(graph.getInEdges(sSpan.getSId()));
		relations.addAll(graph.getOutEdges(sSpan.getSId()));
		for (Edge edge : relations) {
//			linkSources.put(edge, edge.getSource());
//			linkTargets.put(edge, edge.getTarget());
			edge.setGraph(null);
			edge.getSource().eNotify(new NotificationImpl(Notification.REMOVE, edge, null));
			edge.getTarget().eNotify(new NotificationImpl(Notification.REMOVE, edge, null));
	    }
	}
			 
	@Override
	public boolean canUndo() {
		return false;
	}

	public void setSSpan(SSpan sSpan) {
		this.sSpan = sSpan;
	}

}
