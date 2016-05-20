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

import java.io.BufferedReader; 
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.databinding.converters.HashSetToALinebrokenStringConverter;
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.neovisionaries.i18n.LanguageCode;

/**
 * An implementation of {@link ProcessingComponentConfigurationControls} for {@link SaltTokenizer}s.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class SaltTokenizerConfigurationControls extends ProcessingComponentConfigurationControls {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "SaltTokenizerConfigurationControls".
	 */
	private static final Logger log = LogManager.getLogger(SaltTokenizerConfigurationControls.class);
	
	private ComboViewer languageCodeCombo;
	
	private Text abbreviationsText;
	
	private static final String ABBREVIATIONS_TEXT = "Enter comma-separated list of abbreviations or browse for abbreviations file ...\n" + "Abbreviations files must contain one abbreviation per line, e.g.:\n" + "Abk.\n" + "Bzgl.\n" + "d.h.";

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
		languageCodeCombo.getControl().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));

		Label abbreviationsLabel = new Label(parent, SWT.NONE);
		abbreviationsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		abbreviationsLabel.setText("Language-specific abbreviations");

		abbreviationsText = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		GridData abbreviationsTextLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		abbreviationsTextLayoutData.heightHint = 5 * abbreviationsText.getLineHeight();
		abbreviationsTextLayoutData.widthHint = 600;
		abbreviationsText.setLayoutData(abbreviationsTextLayoutData);
		setProperty(abbreviationsText, ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations);
		abbreviationsText.setText(ABBREVIATIONS_TEXT);

		Button abbreviationsFileBrowseButton = new Button(parent, SWT.PUSH);
		abbreviationsFileBrowseButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		abbreviationsFileBrowseButton.setText("&Browse ...");
		abbreviationsFileBrowseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), SWT.OPEN | SWT.SINGLE);
				String path = dialog.open();
				if (path != null) {
					File abbreviationsFile = new File(path);
					StringBuffer stringBuffer = new StringBuffer();
					try (BufferedReader br = new BufferedReader(new FileReader(abbreviationsFile))) {
						for (String line; (line = br.readLine()) != null;) {
							stringBuffer.append(line);
							stringBuffer.append("\n");
						}
						br.close();
						abbreviationsText.setText(stringBuffer.toString());
					}
					catch (IOException e1) {
						log.error("Could not process file \"{}\".", abbreviationsFile, e1);
					}
				}
			}
		});

		Label placeHolder = new Label(parent, SWT.WRAP);
		placeHolder.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		placeHolder.setVisible(false);
		
		Label abbreviationsDescription = new Label(parent, SWT.WRAP);
		abbreviationsDescription.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		abbreviationsDescription.setText(ABBREVIATIONS_TEXT);

		initDataBindings();
	}

	/**
	 * Initializes the data bindings for the widgets working on actual data.
	 */
	private void initDataBindings() {
		getConfiguration().setLanguageCode(LanguageCode.aa);
		
		DataBindingContext bindingContext = new DataBindingContext();
		IViewerObservableValue languageCodewidgetObservable = ViewersObservables.observeSingleSelection(languageCodeCombo);
		bindingContext.bindValue(languageCodewidgetObservable, BeanProperties.value(getConfiguration().getClass(), ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_languageCode).observe(getConfiguration()));

		UpdateValueStrategy targetStrategy = new UpdateValueStrategy();
		UpdateValueStrategy modelStrategy = new UpdateValueStrategy();
		targetStrategy.setConverter(new LinebrokenStringToHashSetConverter(String.class, HashSet.class));
		modelStrategy.setConverter(new HashSetToALinebrokenStringConverter(HashSet.class, String.class));
		IObservableValue abbreviationsWidgetObservable = WidgetProperties.text(SWT.Modify).observe(abbreviationsText);
		bindingContext.bindValue(abbreviationsWidgetObservable, BeanProperties.value(getConfiguration().getClass(), ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations).observe(getConfiguration()), targetStrategy, modelStrategy);
	}

}
