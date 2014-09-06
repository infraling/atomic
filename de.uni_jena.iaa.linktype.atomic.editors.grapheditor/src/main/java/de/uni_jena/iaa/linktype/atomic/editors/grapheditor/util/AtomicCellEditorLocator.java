/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.draw2d.IFigure; 
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.NodeFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.RelationFigure;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart.AnnotationFigure;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicCellEditorLocator implements CellEditorLocator {

	private IFigure figure;

	public AtomicCellEditorLocator(IFigure figure) {
		this.setFigure(figure);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.tools.CellEditorLocator#relocate(org.eclipse.jface.viewers.CellEditor)
	 */
	@Override
	public void relocate(CellEditor celleditor) {
		Text text = (Text) celleditor.getControl();
		Point pref = text.computeSize(SWT.DEFAULT, SWT.DEFAULT);

		Rectangle rect = null;
		int x = 0;
		int y = 0;
		if (getFigure() instanceof AnnotationFigure) {
			rect = getFigure().getBounds().getCopy();
			getFigure().translateToAbsolute(rect);
			y = rect.y - 1;
			x = rect.x;
		}

		else if (getFigure() instanceof NodeFigure) {
			rect = getFigure().getBounds().getCopy();
			getFigure().translateToAbsolute(rect);
			y = rect.y + 34; // FIXME Calculate dynamically
			x = rect.x + 3;
		}
		else if (getFigure() instanceof RelationFigure) {
			de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures.IDLabel iDLabel = ((RelationFigure) figure).getLabel();
			rect = ((RelationFigure) figure).getLabel().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			y = (rect.y - 1) + iDLabel.getPreferredSize().height;
			x = rect.x;
		}
		text.setBounds(x, y, pref.x + 1, pref.y + 1);

	}

	/**
	 * @return the figure
	 */
	public IFigure getFigure() {
		return figure;
	}

	/**
	 * @param figure the figure to set
	 */
	public void setFigure(IFigure figure) {
		this.figure = figure;
	}

}
