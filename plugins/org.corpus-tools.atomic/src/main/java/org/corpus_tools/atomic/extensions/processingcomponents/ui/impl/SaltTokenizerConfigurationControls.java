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

import java.util.HashSet;
import org.corpus_tools.atomic.databinding.converters.LinebrokenStringToHashSetConverter;
import org.corpus_tools.atomic.extensions.processingcomponents.impl.ProcessingComponentConfigurationProperties;
import org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer;
import org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizerConfiguration;
import org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.IViewerObservableValue;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.neovisionaries.i18n.LanguageCode;

/**
 * An implementation of {@link ProcessingComponentConfigurationControls} for {@link SaltTokenizer}s.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class SaltTokenizerConfigurationControls extends ProcessingComponentConfigurationControls {

	private ComboViewer languageCodeCombo;
	
	private SaltTokenizerConfiguration configuration = null;

	private Text abbreviationsText;

	/*
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentConfigurationControls#addControls()
	 */
	@Override
	public void addControls(Composite parent, int style) {
		Label languageCodeLabel = new Label(parent, SWT.NONE);
		languageCodeLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		languageCodeLabel.setText("Language");

		languageCodeCombo = new ComboViewer(parent, SWT.READ_ONLY | SWT.DROP_DOWN);
		languageCodeCombo.setContentProvider(ArrayContentProvider.getInstance());
		languageCodeCombo.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof LanguageCode) {
					LanguageCode code = (LanguageCode) element;
					return code.getName();
				}
				return super.getText(element);
			}
		});
		languageCodeCombo.setInput(LanguageCode.values());
		languageCodeCombo.getCombo().select(0);

		Label abbreviationsLabel = new Label(parent, SWT.NONE);
		abbreviationsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		abbreviationsLabel.setText("Language-specific abbreviations");

		abbreviationsText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		abbreviationsText.setText("Enter comma-separated list of abbreviations or browse for abbreviations file ...\n" + "Abbreviations files must contain one abbreviation per line, e.g.:\n" + "Abk.\n" + "Bzgl.\n" + "d.h.");
		GridData abbreviationsTextLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		abbreviationsTextLayoutData.heightHint = 5 * abbreviationsText.getLineHeight();
		abbreviationsText.setLayoutData(abbreviationsTextLayoutData);
		setProperty(abbreviationsText, ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations);

		Button abbreviationsFileBrowseButton = new Button(parent, SWT.PUSH);
		abbreviationsFileBrowseButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		abbreviationsFileBrowseButton.setText("Browse ...");

		initDataBindings();
	}

	/**
	 * Initializes the data bindings for the widgets working on actual data.
	 */
	private void initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		IViewerObservableValue languageCodewidgetObservable = ViewersObservables.observeSingleSelection(languageCodeCombo);
		bindingContext.bindValue(languageCodewidgetObservable, BeanProperties.value(getConfiguration().getClass(), ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_languageCode).observe(getConfiguration()));

		UpdateValueStrategy targetStrategy = new UpdateValueStrategy();
		targetStrategy.setConverter(new LinebrokenStringToHashSetConverter(String.class, HashSet.class));
		IObservableValue abbreviationsWidgetObservable = WidgetProperties.text(SWT.Modify).observe(abbreviationsText);
		bindingContext.bindValue(abbreviationsWidgetObservable, BeanProperties.value(getConfiguration().getClass(), ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations).observe(getConfiguration()), targetStrategy, null);
	}

	/**
	 * @return the configuration
	 */
	public SaltTokenizerConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(SaltTokenizerConfiguration configuration) {
		this.configuration = configuration;
	}

}
