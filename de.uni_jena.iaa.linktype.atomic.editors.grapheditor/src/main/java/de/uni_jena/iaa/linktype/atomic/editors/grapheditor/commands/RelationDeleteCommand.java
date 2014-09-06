/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;

/**
 * @author Stephan Druskat
 *
 */
public class RelationDeleteCommand extends Command {

	private SRelation model;
	private SDocumentGraph graph;
	private SNode source, target;

	@Override
	public boolean canExecute() {
		return model != null;
	}
	
	@Override
	public void execute() {
		graph = (SDocumentGraph) model.getSGraph();
		source = model.getSSource();
		target = model.getSTarget();
		
		model.setSGraph(null);
		source.eNotify(new NotificationImpl(Notification.REMOVE, model, null));
		target.eNotify(new NotificationImpl(Notification.REMOVE, model, null));
	}
	
	@Override
	public void undo() {
		model.setSSource(source);
		model.setSTarget(target);
		model.setSGraph(graph);
	}
	
	public void setRelation(SRelation model) {
		this.model = model;
	}

}
