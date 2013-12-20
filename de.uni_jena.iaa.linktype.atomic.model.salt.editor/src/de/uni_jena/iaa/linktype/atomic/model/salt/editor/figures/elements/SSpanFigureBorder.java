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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.figures.elements;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.GroupBoxBorder;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author Stephan Druskat
 *
 */
public class SSpanFigureBorder extends GroupBoxBorder {

	public SSpanFigureBorder(String string) {
		super(string);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.Border#paint(org.eclipse.draw2d.IFigure, org.eclipse.draw2d.Graphics, org.eclipse.draw2d.geometry.Insets)
	 */
	@Override
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		graphics.pushState();
        Rectangle rect = figure.getBounds();
        graphics.clipRect(rect);
        Rectangle r = rect.getResized(-1, -9).translate(0, 8);
        graphics.setForegroundColor(ColorConstants.red);
        graphics.drawRoundRectangle(r, 10, 10);
        graphics.setForegroundColor(ColorConstants.blue);
        graphics.setBackgroundColor(new Color(Display.getCurrent(), 222, 222, 222));
        graphics.fillText(getLabel(), r.x + 8, r.y - 8);
        graphics.popState();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.draw2d.AbstractLabeledBorder#calculateInsets(org.eclipse.draw2d.IFigure)
	 */
	@Override
	protected Insets calculateInsets(IFigure figure) {
		return new Insets(15, 2, 2, 2); // TODO calculate Insets possible?
	}

}
