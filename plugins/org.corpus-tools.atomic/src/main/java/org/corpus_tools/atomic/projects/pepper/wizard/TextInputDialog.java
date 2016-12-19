/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena 
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

package org.corpus_tools.atomic.projects.pepper.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * Dialog allowing for text input.
 * 
 * @author Michael Grübsch
 */
public class TextInputDialog extends MessageDialog {

	protected final String labelText;
	protected final String initialText;
	protected final TextInputVerifier textInputVerifier;

	protected String inputText;

	protected Text text;

	/**
	 * Creates a new instance of type {@link TextInputDialog}.
	 * 
	 * @param parentShell
	 * @param dialogTitle
	 * @param dialogMessage
	 */
	public TextInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String labelText, String initialText, TextInputVerifier textInputVerifier) {
		super(parentShell, dialogTitle, null, dialogMessage, MessageDialog.QUESTION, new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

		this.labelText = labelText;
		this.initialText = initialText;
		this.textInputVerifier = textInputVerifier;

		setShellStyle(getShellStyle() | SWT.SHEET);
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.MessageDialog#createCustomArea(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected Control createCustomArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		GridLayout gl = new GridLayout(2, false);
		gl.marginHeight = gl.marginWidth = 0;
		gl.marginLeft = 50;
		gl.marginBottom = 20;
		gl.verticalSpacing = 0;
		composite.setLayout(gl);

		Label label = new Label(composite, SWT.RIGHT);
		label.setText(labelText);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));

		text = new Text(composite, SWT.SINGLE | SWT.BORDER);
		text.setText(initialText != null ? initialText : "");
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				inputText = text.getText();
				if (textInputVerifier != null) {
					TextInputDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(textInputVerifier.verifyText(inputText));
				}
			}
		});

		return composite;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.MessageDialog#createButtonsForButtonBar(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		if (textInputVerifier != null) {
			TextInputDialog.this.getButton(IDialogConstants.OK_ID).setEnabled(textInputVerifier.verifyText(text.getText()));
		}
	}

	public String getInputText() {
		return inputText;
	}

	public static interface TextInputVerifier {
		public boolean verifyText(String text);
	}

	public static class RequiredTextInputVerifier implements TextInputVerifier {
		/* 
		 * @copydoc @see org.corpus_tools.atomic.pepper.wizard.TextInputDialog.TextInputVerifier#verifyText(java.lang.String)
		 */
		@Override
		public boolean verifyText(String text) {
			return text != null && 0 < text.trim().length();
		}

	}
}
