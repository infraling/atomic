/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditorDeleteCommand extends Command {

	private LabelableElement model;
	
	@Override
	public void execute(){
		if (model instanceof SAnnotation) {
			SAnnotation annotation = (SAnnotation) model;
			if (annotation.getNamespace() != null) {
				annotation.getSAnnotatableElement().removeLabel(annotation.getNamespace(), annotation.getName());
			}
			else {
				annotation.getSAnnotatableElement().removeLabel(annotation.getSName());	
			}
		}
	}

	public void setModel(LabelableElement model) {
		this.model = model;
	}

}
