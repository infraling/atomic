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

import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.commands.Command;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.LabelableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class SAnnotationAnnotateCommand extends Command {
	
	private String annotationInput;
	private SAnnotation model;
	private LabelableElement modelParent;
	
	public void setModel(SAnnotation model) {
		this.model = model;
	}

	public void setNewAnnotation(String value) {
		this.annotationInput = value;
	}
	
	@Override 
	public void execute() {
		// Parse annotation String
		String[] annotationKeyValue = annotationInput.split(":");
		String key = null;
		String value = null;
		try {
			key = annotationKeyValue[0];
			value = annotationKeyValue[1];
		} catch (ArrayIndexOutOfBoundsException e) {
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Annotation error", "The annotation format is not correct.\nPlease use [key]:[value], where neither\nvalue may be empty.");
			return;
		}
		// Determine type of model parent & get its List of annotations
		EList<SAnnotation> existingAnnotations = getExistingAnnotationsFromModelParent(modelParent);
		if (existingAnnotations != null) {
			boolean keyExistsInPreexistingAnnotations = false;
			for (SAnnotation preExistingAnnotation : existingAnnotations) {
				if (preExistingAnnotation.getSName().equalsIgnoreCase(key) && preExistingAnnotation != model) {
					if (preExistingAnnotation.getValueString().equalsIgnoreCase(value)) { // Exact duplicate annotation exists
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Duplicate annotation!", "This exact annotation exists already for this annotatable element.\nThe process will be aborted.");
						return;
					}
					else {
						MessageDialog dialog = new MessageDialog(Display.getCurrent().getActiveShell(), 
								"Duplicate annotation key!", 
								null, 
								"An annotation with the key " + 
									key + 
									" already exists!\n" +
									"Its current value is " +
									preExistingAnnotation.getValueString() + ".\n" +
									"Do you want to overwrite the value of the existing annotation,\n" +
									"and delete the annotation you are currently editing?", 
								MessageDialog.QUESTION, 
								new String[] {"Yes", "No"}, 
								0);
								int result = dialog.open();
								switch (result) {
								case 0: // "Yes"
									preExistingAnnotation.setSValue(value);
									LabelableElement parent = model.getLabelableElement();
									parent.removeLabel(model.getSName());
									parent.eNotify(new NotificationImpl(Notification.REMOVE, model, null));
									break;
								case 1: // "No"
									// Do nothing
									break;
								default:
									break;
								}
					}
					keyExistsInPreexistingAnnotations = true;
				}
				else
					keyExistsInPreexistingAnnotations = false;
			}
			if (!keyExistsInPreexistingAnnotations) { // Key doesn't exist in preexisting annotations.
				model.setSName(annotationKeyValue[0]);
				model.setSValue(annotationKeyValue[1]);
			}
		}
		else { // existingAnnotations == null -> model parent has no annotations. Should never be called...
			model.setSName(annotationKeyValue[0]);
			model.setSValue(annotationKeyValue[1]);
		}
	}

	private EList<SAnnotation> getExistingAnnotationsFromModelParent(LabelableElement modelParent2) {
		EList<SAnnotation> existingAnnotationsFromModelParent = null;
		if (modelParent2 instanceof SStructure) {
			SStructure parent = (SStructure) modelParent2;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent2 instanceof SToken) {
			SToken parent = (SToken) modelParent2;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent2 instanceof SDominanceRelation) {
			SDominanceRelation parent = (SDominanceRelation) modelParent2;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		else if (modelParent2 instanceof SPointingRelation) {
			SPointingRelation parent = (SPointingRelation) modelParent2;
			existingAnnotationsFromModelParent = parent.getSAnnotations();
		}
		return existingAnnotationsFromModelParent;
	}

	public void setModelParent(LabelableElement labelableElement) {
		this.modelParent = labelableElement;
	}
			 
//	@Override 
//	public void undo() {
//		if (model.getSAnnotations() == null)
//			//model.createSAnnotation(null, "ATOMIC__PRIMARY_ANNOTATION", oldAnnotation);
//		model.getSAnnotation("ATOMIC__PRIMARY_ANNOTATION").setSValue(oldAnnotation);
//	}
			   
}
