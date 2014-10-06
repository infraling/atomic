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

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * @author Stephan Druskat
 *
 */
public class SDominanceRelationCreateCommand extends Command {
	
	// FIXME: Can only link between SNodes at the moment!
	
	private SDocumentGraph graph;
	private SDominanceRelation dominanceRelation;
	private SNode source, target;
			
	@Override
	public boolean canExecute() {
		return source != null && source instanceof SStructure && target != null && dominanceRelation != null;
	}
			   
	@Override 
	public void execute() {
		dominanceRelation.setSSource(source);
		dominanceRelation.setSTarget(target);
		dominanceRelation.setSDocumentGraph(graph);
		source.eNotify(new NotificationImpl(Notification.ADD, null, dominanceRelation));
		target.eNotify(new NotificationImpl(Notification.ADD, null, dominanceRelation));
	}
			 
	@Override 
	public void undo() {
		graph.getOutEdges(dominanceRelation.getSTarget().getSId()).remove(dominanceRelation);
		graph.getInEdges(dominanceRelation.getSTarget().getSId()).remove(dominanceRelation);
		dominanceRelation.setSDocumentGraph(null);
	}
			 
	public void setTarget(SNode target) {
		this.target = target;
	}
			   
	public void setSource(SNode source) {
		this.source = source;
	}
			   
	public void setSDominanceRelation(SDominanceRelation dominanceRelation) {
		this.dominanceRelation = dominanceRelation;
	}
			   
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
