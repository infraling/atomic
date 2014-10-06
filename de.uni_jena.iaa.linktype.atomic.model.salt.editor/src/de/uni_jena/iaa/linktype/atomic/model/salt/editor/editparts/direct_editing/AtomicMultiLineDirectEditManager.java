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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.direct_editing;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SAnnotationEditPart;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicMultiLineDirectEditManager extends DirectEditManager {

	@SuppressWarnings("rawtypes")
	public AtomicMultiLineDirectEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
		super(source, editorType, locator);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
	 */
	@Override 
	protected void initCellEditor() {
		String initialLabelText = "";
		if (getEditPart() instanceof SAnnotationEditPart) {
			SAnnotation annotation = (SAnnotation) getEditPart().getModel();
			initialLabelText = annotation.getSName() + ":" + annotation.getValueString();
		}
		else {
			for (SAnnotation annotation : ((SAnnotatableElement) getEditPart().getModel()).getSAnnotations()) {
				initialLabelText = initialLabelText + annotation.getSName() + ":" + annotation.getValueString() + "\n";
			}
		}
		getCellEditor().setValue(initialLabelText);
	}
	
	@Override
	protected CellEditor createCellEditorOn(Composite composite) {
		return new TextCellEditor(composite, SWT.MULTI); 
		// Was SWT.MULTI | SWT.WRAP, but SWT.WRAP results in last line disappearing "over the horizon"... 
	}
	
}
