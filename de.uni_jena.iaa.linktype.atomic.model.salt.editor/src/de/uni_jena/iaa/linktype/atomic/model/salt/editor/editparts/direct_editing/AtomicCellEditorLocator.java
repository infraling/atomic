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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Text;

import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SDominanceRelationFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SPointingRelationFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.SStructureFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.STokenFigure;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.elements.IDLabel;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicCellEditorLocator implements CellEditorLocator {

	private IFigure figure;

	public AtomicCellEditorLocator(IFigure figure) {
		this.figure = figure;
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
		if (figure instanceof SStructureFigure) {
			rect = figure.getBounds().getCopy();
			figure.translateToAbsolute(rect);
			y = rect.y + 17;  // FIXME Calculate dynamically
			x = rect.x + 3;
		}
		else if (figure instanceof STokenFigure) {
			rect = figure.getBounds().getCopy();
			figure.translateToAbsolute(rect);
			y = rect.y + 17; // FIXME Calculate dynamically
			x = rect.x + 3;
		}
		else if (figure instanceof SDominanceRelationFigure) {
			IDLabel iDLabel = ((SDominanceRelationFigure) figure).getiDLabel();
			rect = ((SDominanceRelationFigure) figure).getiDLabel().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			y = (rect.y - 1) + iDLabel.getPreferredSize().height;
			x = rect.x;
		}
		else if (figure instanceof SPointingRelationFigure) {
			IDLabel iDLabel = ((SPointingRelationFigure) figure).getiDLabel();
			rect = ((SPointingRelationFigure) figure).getiDLabel().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			y = (rect.y - 1) + iDLabel.getPreferredSize().height;
			x = rect.x;
		}
	
		text.setBounds(x, y, pref.x + 1, pref.y + 1);
	}

}
