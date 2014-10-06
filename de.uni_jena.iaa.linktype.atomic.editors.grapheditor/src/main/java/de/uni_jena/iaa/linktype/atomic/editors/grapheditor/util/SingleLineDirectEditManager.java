package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart;

public class SingleLineDirectEditManager extends DirectEditManager {

	public SingleLineDirectEditManager(AbstractGraphicalEditPart editPart, Class<TextCellEditor> editorType, CellEditorLocator cellEditorLocator, IFigure figure) {
		super(editPart, editorType, cellEditorLocator);
	}

	@Override
	protected void initCellEditor() {
		String initialLabelText = "";
		if (getEditPart() instanceof AnnotationPart) {
			SAnnotation anno = (SAnnotation) getEditPart().getModel();
			String escapedKey = anno.getSName().replaceAll(":", "\\\\:");
			String escapedValue = anno.getValueString().replaceAll(":", "\\\\:");
			if (anno.getNamespace() != null) {
				String escapedNamespace = anno.getNamespace().replaceAll(":", "\\\\:");
				initialLabelText = escapedNamespace + "::" + escapedKey + ":" + escapedValue;
			}
			else {
				initialLabelText = escapedKey + ":" + escapedValue;
			}
		}
		getCellEditor().setValue(initialLabelText);
	}

}
