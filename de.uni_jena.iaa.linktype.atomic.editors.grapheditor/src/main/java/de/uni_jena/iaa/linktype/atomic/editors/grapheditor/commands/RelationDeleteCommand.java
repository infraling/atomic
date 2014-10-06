/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
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
		return getModel() != null;
	}
	
	@Override
	public void execute() {
		graph = (SDocumentGraph) getModel().getSGraph();
		source = getModel().getSSource();
		target = getModel().getSTarget();
		
		getModel().setSGraph(null);
		source.eNotify(new NotificationImpl(Notification.REMOVE, getModel(), null));
		target.eNotify(new NotificationImpl(Notification.REMOVE, getModel(), null));
	}
	
	@Override
	public void undo() {
		getModel().setSSource(source);
		getModel().setSTarget(target);
		getModel().setSGraph(graph);
		source.eNotify(new NotificationImpl(Notification.ADD, null, getModel()));
		target.eNotify(new NotificationImpl(Notification.ADD, null, getModel()));

	}
	
	@Override
	public boolean canUndo() {
		return false;
	}
	
	public void setRelation(SRelation model) {
		this.setModel(model);
	}

	/**
	 * @return the model
	 */
	public SRelation getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(SRelation model) {
		this.model = model;
	}

}
