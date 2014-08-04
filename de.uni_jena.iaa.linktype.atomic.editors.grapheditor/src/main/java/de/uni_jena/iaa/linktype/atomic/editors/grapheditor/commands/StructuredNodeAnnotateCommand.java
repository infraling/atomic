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

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class StructuredNodeAnnotateCommand extends Command {
	
	private SStructuredNode model;
	private TreeMap<String, Pair<String, String>> annotations = new TreeMap<String, Pair<String, String>>();
	
	@Override 
	public void execute() {
		// Remove all annotations; FIXME: Save them in TreeMap<String, Pair<String,String>> oldAnnotations for undo
		for (Iterator<SAnnotation> iterator = model.getSAnnotations().iterator(); iterator.hasNext();) {
			SAnnotation anno = (SAnnotation) iterator.next();
			String name = anno.getName();
			model.removeLabel(name);
			Assert.isTrue(!(model.getSAnnotations().contains(name))); // FIXME: Refactor to unit test method
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
			 
	public void setModel(SStructuredNode model) {
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
