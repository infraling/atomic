/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.Assert;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class ElementAnnotateCommand extends Command {
	
	private SAnnotatableElement model;
	// Contains annotations in the format <"Annotation key", Pair<"Namespace","Value">>
	private TreeMap<String, Pair<String, String>> annotations = new TreeMap<String, Pair<String, String>>();
	
	@Override 
	public void execute() {
		// Remove all annotations; FIXME: Save them in TreeMap<String, Pair<String,String>> oldAnnotations for undo
		for (Iterator<SAnnotation> iterator = model.getSAnnotations().iterator(); iterator.hasNext();) {
			SAnnotation anno = (SAnnotation) iterator.next();
			if (anno.getNamespace() != null) {
				model.removeLabel(anno.getNamespace(), anno.getName());
			}
			else {
				model.removeLabel(anno.getName());
			}
			String name = anno.getName();
			Assert.isTrue(!(model.getSAnnotations().contains(name))); // FIXME: Refactor to unit test method
			Assert.isTrue(!(model.getSAnnotations().contains(anno))); // FIXME: Refactor to unit test method
		}
		// Create annotations from input
		for (Entry<String, Pair<String, String>> anno : getAnnotations().entrySet()) {
			model.createSAnnotation(anno.getValue().getLeft(), anno.getKey(), anno.getValue().getRight());
		}
	}
	
	@Override // FIXME: Remove once undo is implemented
	public boolean canUndo() {
		return false;
	}
			 
	public void setModel(SAnnotatableElement model) {
		this.model = model;
	}

	/**
	 * @return the annotations
	 */
	public TreeMap<String,Pair<String,String>> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations the annotations to set
	 */
	public void setAnnotations(TreeMap<String, Pair<String, String>> annotations) {
		this.annotations = annotations;
	}

}
