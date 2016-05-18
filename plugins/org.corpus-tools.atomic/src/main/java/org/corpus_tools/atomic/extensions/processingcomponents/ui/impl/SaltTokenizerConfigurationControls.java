/*******************************************************************************
 * Copyright 2016 Stephan Druskat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.extensions.processingcomponents.ui.impl;

import org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer; 
import org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * An implementation of {@link ProcessingComponentConfigurationControls}
 * for {@link SaltTokenizer}s.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltTokenizerConfigurationControls extends ProcessingComponentConfigurationControls {
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls#addControls()
	 */
	@Override
	public void addControls(Composite parent, int style) {
		// FIXME: Implement properly!
		Button button = new Button(parent, SWT.PUSH);
		button.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false, 2, 1));
		button.setText("Button from " + this.getClass().getSimpleName());
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.err.println("Bingo!");
			}
		});
		Label label = new Label(parent, SWT.NONE);
		label.setText("Label 1");
		label.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false, 2, 1));
		Label label2 = new Label(parent, SWT.NONE);
		label2.setText("Label 2");
		label2.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false, 1, 1));
		Label label3 = new Label(parent, SWT.NONE);
		label3.setText("Label 3");
		label3.setLayoutData(new GridData(SWT.TOP, SWT.CENTER, false, false, 1, 1));
	}

}
