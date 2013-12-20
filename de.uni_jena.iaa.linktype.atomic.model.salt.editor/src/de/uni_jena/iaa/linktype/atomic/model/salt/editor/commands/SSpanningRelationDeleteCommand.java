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
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;

/**
 * @author Stephan Druskat
 *
 */
public class SSpanningRelationDeleteCommand extends Command {
	
	private SSpanningRelation sSpanningRelation;
	private SDocumentGraph graph;
	private SNode source, target;

	@Override
	public boolean canExecute() {
		return sSpanningRelation != null;
	}
	
	@Override
	public void execute() {
		graph = sSpanningRelation.getSDocumentGraph();
		source = sSpanningRelation.getSSource();
		target = sSpanningRelation.getSTarget();
		
		sSpanningRelation.setSDocumentGraph(null);
		source.eNotify(new NotificationImpl(Notification.REMOVE, sSpanningRelation, null));
		target.eNotify(new NotificationImpl(Notification.REMOVE, sSpanningRelation, null));
	}
	
	@Override
	public void undo() {
		sSpanningRelation.setSSource(source);
		sSpanningRelation.setSTarget(target);
		sSpanningRelation.setSDocumentGraph(graph);
	}
	
	public void setSSpanningRelation(SSpanningRelation sSpanningRelation) {
		this.sSpanningRelation = sSpanningRelation;
	}

}
