/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Platform;
import org.eclipse.gef.commands.Command;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Label;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.AccessorUtil;

/**
 * @author Stephan Druskat
 * 
 */
public class ElementAnnotateCommand extends Command {

	private SAnnotatableElement model;
	// Contains annotations in the format <"Annotation key",
	// Pair<"Namespace","Value">>
	private TreeMap<String, Pair<String, String>> annotations = new TreeMap<String, Pair<String, String>>();

	@Override
	public void execute() {
		// Get reserved keys
		String typeKey;
		String useProjectSettings = AccessorUtil.getReservedKeysProperty("useProjectSettings");
		if (useProjectSettings != null && useProjectSettings.equals("true")) {
			typeKey = AccessorUtil.getReservedKeysProperty("sType");
		} else {
			typeKey = Platform.getPreferencesService().getString("de.uni_jena.iaa.linktype.atomic.core", "sType", "t", null);
		}

		// Remove all annotations; FIXME: Save them in TreeMap<String,
		// Pair<String,String>> oldAnnotations for undo
		for (Iterator<SAnnotation> iterator = model.getSAnnotations().iterator(); iterator.hasNext();) {
			SAnnotation anno = (SAnnotation) iterator.next();
			if (anno.getNamespace() != null) {
				model.removeLabel(anno.getNamespace(), anno.getName());
			} else {
				model.removeLabel(anno.getName());
			}
			String name = anno.getName();
			Assert.isTrue(!(model.getSAnnotations().contains(name))); // FIXME:
																		// Refactor
																		// to
																		// unit
																		// test
																		// method
			Assert.isTrue(!(model.getSAnnotations().contains(anno))); // FIXME:
																		// Refactor
																		// to
																		// unit
																		// test
																		// method
		}
		// Create annotations from input
		for (Entry<String, Pair<String, String>> anno : getAnnotations().entrySet()) {
			// Check whether key is the reserved key for STYPE, and act
			// accordingly
			if (model instanceof SRelation && anno.getKey().equals(typeKey)) {
				setSType(anno.getValue().getRight());
				return;
			} else {
				model.createSAnnotation(anno.getValue().getLeft(), anno.getKey(), anno.getValue().getRight());
			}
		}
	}

	/**
	 * Check whether the SRelation model already has an STYPE.
	 * If it does not, add one with value 'value'.
	 * If it does, remove the specific Label, and re-add it with value 'value'.
	 * 
	 * @param value
	 */
	private void setSType(String value) {
		SRelation rel = (SRelation) model;
		Label sTypeLabel = rel.getLabel("saltCore", "STYPE");
		if (sTypeLabel == null) { // Element has no STYPE yet
			rel.addSType(value);
		}
		else { // Element already has an STYPE
			rel.removeLabel("saltCore", "STYPE"); // Doing it the hard way because it won't work by just setting valueString, no sir!
			rel.addSType(value); // Re-add
		}
	}

	@Override
	// FIXME: Remove once undo is implemented
	public boolean canUndo() {
		return false;
	}

	public void setModel(SAnnotatableElement model) {
		this.model = model;
	}

	/**
	 * @return the annotations
	 */
	public TreeMap<String, Pair<String, String>> getAnnotations() {
		return annotations;
	}

	/**
	 * @param annotations
	 *            the annotations to set
	 */
	public void setAnnotations(TreeMap<String, Pair<String, String>> annotations) {
		this.annotations = annotations;
	}

}
