/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;

/**
 * @author Stephan Druskat
 *
 */
public class MultiLineDirectEditManager extends DirectEditManager {

	@SuppressWarnings("rawtypes")
	public MultiLineDirectEditManager(GraphicalEditPart source, Class editorType, CellEditorLocator locator) {
		super(source, editorType, locator);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.DirectEditManager#initCellEditor()
	 */
	@Override 
	protected void initCellEditor() {
		String initialLabelText = "";
		for (SAnnotation annotation : ((SAnnotatableElement) getEditPart().getModel()).getSAnnotations()) {
			String escapedKey = annotation.getSName().replaceAll(":", "\\\\:");
			String escapedValue = annotation.getValueString().replaceAll(":", "\\\\:");
			if (annotation.getNamespace() != null) {
				String escapedNamespace = annotation.getNamespace().replaceAll(":", "\\\\:");
				initialLabelText = initialLabelText + escapedNamespace + "::" + escapedKey + ":" + escapedValue + "\n";
			}
			else {
				initialLabelText = initialLabelText + escapedKey + ":" + escapedValue + "\n";
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
