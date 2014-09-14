/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class AnnotationDeleteCommand extends Command {

	private SAnnotation model;

	SAnnotation oldModel;
	SAnnotatableElement modelParent;;
	
	@Override
	public void execute(){
		setOldModel((SAnnotation) model);
		if (getModel().getNamespace() != null) {
			getModelParent().removeLabel(getModel().getNamespace(), getModel().getName());
		}
		else {
			getModelParent().removeLabel(getModel().getSName());	
		}
	}
	
	@Override
	public void undo() {
		getModelParent().createSAnnotation(getOldModel().getNamespace(), getOldModel().getName(), getOldModel().getValue().toString());
	}

	public void setModel(SAnnotation model) {
		this.model = model;
	}
	
	/**
	 * @return the model
	 */
	private SAnnotation getModel() {
		return model;
	}

	/**
	 * @return the oldModel
	 */
	public SAnnotation getOldModel() {
		return oldModel;
	}

	/**
	 * @param oldModel the oldModel to set
	 */
	public void setOldModel(SAnnotation oldModel) {
		this.oldModel = oldModel;
	}

	/**
	 * @return the modelParent
	 */
	public SAnnotatableElement getModelParent() {
		return modelParent;
	}

	/**
	 * @param modelParent the modelParent to set
	 */
	public void setModelParent(SAnnotatableElement modelParent) {
		this.modelParent = modelParent;
	}

}
