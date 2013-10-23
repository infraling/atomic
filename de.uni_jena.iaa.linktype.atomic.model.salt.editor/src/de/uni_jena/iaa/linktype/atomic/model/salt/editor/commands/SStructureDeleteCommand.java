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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;

/**
 * @author Stephan Druskat
 *
 */
public class SStructureDeleteCommand extends Command { 
	
	private SStructure sStructure;
	private SDocumentGraph graph;
	private ArrayList<Edge> relations;
			 
	@Override
	public void execute() {
		graph = sStructure.getSDocumentGraph();
		if ((graph.getInEdges(sStructure.getSId())) != null || (graph.getOutEdges(sStructure.getSId())) != null) 
				detachRelations();
		sStructure.setSDocumentGraph(null);			
	}
	
	private void detachRelations() {
		relations = new ArrayList<Edge>();
//		linkSources = new HashMap<OPMLink, OPMThing>();
//		linkTargets = new HashMap<OPMLink, OPMThing>();
		relations.addAll(graph.getInEdges(sStructure.getSId()));
		relations.addAll(graph.getOutEdges(sStructure.getSId()));
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

	public void setSStructure(SStructure sStructure) {
		this.sStructure = sStructure;
	}

}
