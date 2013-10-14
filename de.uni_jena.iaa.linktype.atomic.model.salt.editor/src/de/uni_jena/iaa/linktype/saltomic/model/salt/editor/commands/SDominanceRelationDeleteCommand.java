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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * @author Stephan Druskat
 *
 */
public class SDominanceRelationDeleteCommand extends Command {
	
	private SDominanceRelation sDominanceRelation;
	private SDocumentGraph graph;
	private SNode source, target;

	@Override
	public boolean canExecute() {
		return sDominanceRelation != null;
	}
	
	@Override
	public void execute() {
		graph = sDominanceRelation.getSDocumentGraph();
		source = sDominanceRelation.getSSource();
		target = sDominanceRelation.getSTarget();
		
		sDominanceRelation.setSDocumentGraph(null);
		source.eNotify(new NotificationImpl(Notification.REMOVE, sDominanceRelation, null));
		target.eNotify(new NotificationImpl(Notification.REMOVE, sDominanceRelation, null));
	}
	
	@Override
	public void undo() {
		sDominanceRelation.setSSource(source);
		sDominanceRelation.setSTarget(target);
		sDominanceRelation.setSDocumentGraph(graph);
	}
	
	public void setSDominanceRelation(SDominanceRelation model) {
		this.sDominanceRelation = model;
	}

}
