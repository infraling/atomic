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

import org.corpus_tools.atomic.extensions.processingcomponents.impl.ProcessingComponentConfigurationProperties;
import org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer;  
import org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.neovisionaries.i18n.LanguageCode;

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
		Label languageCodeLabel = new Label(parent, SWT.NONE);
		languageCodeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		languageCodeLabel.setText("Language");
		
		Combo languageCodeCombo = new Combo(parent, SWT.READ_ONLY);
		languageCodeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1));
		LanguageCode[] languageCodes = LanguageCode.values();
		String[] items = new String[languageCodes.length];
		for (int i = 0; i < items.length; i++) {
			items[i] = languageCodes[i].getName();
		}
		languageCodeCombo.setItems(items);
		languageCodeCombo.select(0);
		setProperty(languageCodeCombo, ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_languageCode);
		
		Label abbreviationsLabel = new Label(parent, SWT.NONE);
		abbreviationsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		abbreviationsLabel.setText("Language-specific abbreviations");
		
		Text abbreviationsText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		abbreviationsText.setText("Enter comma-separated list of abbreviations or browse for abbreviations file ...\n"
				+ "Abbreviations files must contain one abbreviation per line, e.g.:\n"
				+ "Abk.\n"
				+ "Bzgl.\n"
				+ "d.h.");
		GridData abbreviationsTextLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		abbreviationsTextLayoutData.heightHint = 5 * abbreviationsText.getLineHeight();
		abbreviationsText.setLayoutData(abbreviationsTextLayoutData);
		setProperty(abbreviationsText ,ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations);
		
		
		Button abbreviationsFileBrowseButton = new Button(parent, SWT.PUSH);
		abbreviationsFileBrowseButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		abbreviationsFileBrowseButton.setText("Browse ...");
	}

}
