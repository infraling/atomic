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
	
	private Composite parent = null;

	/**
	 * @param parent
	 * @param style
	 */
	public SaltTokenizerConfigurationControls(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls#addControls()
	 */
	@Override
	public void addControls() {
		Label label = new Label(parent, SWT.NONE);
		label.setText("Label from " + this.getClass().getSimpleName());
	}

}
