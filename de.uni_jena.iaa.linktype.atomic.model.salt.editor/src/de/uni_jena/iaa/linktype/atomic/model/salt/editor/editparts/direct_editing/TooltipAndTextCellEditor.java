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

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Stephan Druskat
 *
 */
public class TooltipAndTextCellEditor extends TextCellEditor {

	private Label label;

	/**
	 * 
	 */
	public TooltipAndTextCellEditor() {
		super();
	}

	/**
	 * @param parent
	 */
	public TooltipAndTextCellEditor(Composite parent) {
		super(parent);
	}

	/**
	 * @param parent
	 * @param style
	 */
	public TooltipAndTextCellEditor(Composite parent, int style) {
		super(parent, style);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.viewers.TextCellEditor#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createControl(Composite parent) {
		Text text = (Text) super.createControl(parent);
		label = new Label(parent, SWT.SHADOW_NONE);
		label.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
		label.setText("Annotation format (one annotation per line): key:value. To apply annotation(s) press CTRL/CMD + Return. To abort press ESC.");
		label.setLocation(2, 2);
		label.pack();
		text.pack();
		return text;
	}
	
	/**
	 * Processes a focus lost event that occurred in this cell editor.
	 * <p>
	 * The default implementation of this framework method applies the current
	 * value and deactivates the cell editor. Subclasses should call this method
	 * at appropriate times. Subclasses may also extend or reimplement.
	 * </p>
	 */
	@Override
	protected void focusLost() {
		if (!label.isDisposed())
			label.dispose();
		super.focusLost();
	}

}
