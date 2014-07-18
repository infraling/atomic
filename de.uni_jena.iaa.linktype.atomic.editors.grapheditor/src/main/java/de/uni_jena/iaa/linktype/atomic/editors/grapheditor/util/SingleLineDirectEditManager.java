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
			if (anno.getNamespace() != null) {
				initialLabelText = anno.getNamespace() + "::" + anno.getSName() + ":" + anno.getSValue();
			}
			else {
				initialLabelText = anno.getSName() + ":" + anno.getSValue();
			}
		}
		getCellEditor().setValue(initialLabelText);
	}

}
