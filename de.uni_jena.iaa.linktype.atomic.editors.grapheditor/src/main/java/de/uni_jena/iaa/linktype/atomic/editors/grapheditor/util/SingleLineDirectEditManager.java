package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.TextCellEditor;

public class SingleLineDirectEditManager extends DirectEditManager {

	private IFigure figure;

	public SingleLineDirectEditManager(AbstractGraphicalEditPart editPart, Class<TextCellEditor> editorType, CellEditorLocator cellEditorLocator, IFigure figure) {
		super(editPart, editorType, cellEditorLocator);
		this.figure = figure;
	}

	@Override
	protected void initCellEditor() {
		String initialLabelText = ((Label) figure).getText();
		getCellEditor().setValue(initialLabelText);
	}

}
