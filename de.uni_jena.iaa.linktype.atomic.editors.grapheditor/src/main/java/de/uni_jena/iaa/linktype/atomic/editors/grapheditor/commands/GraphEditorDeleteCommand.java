/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditorDeleteCommand extends Command {

	private LabelableElement model, oldModel, modelParent;;
	
	@Override
	public void execute(){
		if (model instanceof SAnnotation) {
			setOldModel((SAnnotation) model);
			setModelParent(((SAnnotation) getOldModel()).getSAnnotatableElement());
			SAnnotation annotation = (SAnnotation) getOldModel();
			if (annotation.getNamespace() != null) {
				getModelParent().removeLabel(annotation.getNamespace(), annotation.getName());
			}
			else {
				getModelParent().removeLabel(annotation.getSName());	
			}
		}
	}
	
	@Override
	public void undo() {
		if (getOldModel() instanceof SAnnotation) {
			SAnnotation annotation = (SAnnotation) getOldModel();
			((SAnnotatableElement) getModelParent()).createSAnnotation(annotation.getNamespace(), annotation.getName(), annotation.getValue().toString());
		}
	}

	public void setModel(LabelableElement model) {
		this.model = model;
	}

	/**
	 * @return the oldModel
	 */
	public LabelableElement getOldModel() {
		return oldModel;
	}

	/**
	 * @param oldModel the oldModel to set
	 */
	public void setOldModel(LabelableElement oldModel) {
		this.oldModel = oldModel;
	}

	/**
	 * @return the modelParent
	 */
	public LabelableElement getModelParent() {
		return modelParent;
	}

	/**
	 * @param modelParent the modelParent to set
	 */
	public void setModelParent(LabelableElement modelParent) {
		this.modelParent = modelParent;
	}

}
