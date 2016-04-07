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
package org.corpus_tools.atomic.projects.wizard;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewAtomicProjectWizardPageTokenization extends WizardPage {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizardPageTokenization".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizardPageTokenization.class);
	
	private NewAtomicProjectWizardPageProjectStructure projectStructurePage;
	private IConfigurationElement tokenizer = null;

	private ArrayList<String> tokenizerNameList;

	private ArrayList<String> tokenizerCreatorList;
	
	final private IConfigurationElement[] tokenizers = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");

	private Label descriptionLabel;

	private ArrayList<String> tokenizerDescriptionList;

	private ArrayList<Boolean> tokenizerIsConfigurableList;
	
	/**
	 * @param projectStructurePage 
	 * 
	 */
	public NewAtomicProjectWizardPageTokenization(NewAtomicProjectWizardPageProjectStructure projectStructurePage) {
		super("Tokenize the corpus documents");
		this.setProjectStructurePage(projectStructurePage);
		setTitle("Tokenize the corpus documents");
		setDescription("Pick a tokenizer to use for tokenization of all documents in the corpus.");
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout containerLayout = new GridLayout(2, false);
		containerLayout.verticalSpacing = 30;
		container.setLayout(containerLayout);
		
		
		Label label = new Label(container, SWT.WRAP);
		label.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		label.setText("The basic annotatable unit in Atomic's data model is the token, hence in order to create annotations at all, the corpus documents must be tokenized.\n"
				+ "Please pick the tokenizer that should be used to tokenize all documents in the corpus. Note that tokens can be added or removed, and completely new token layers created later.");
		
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		// FIXME: Put all in one data structure (Map?)
		tokenizerNameList = new ArrayList<>();
		tokenizerDescriptionList = new ArrayList<>();
		tokenizerCreatorList = new ArrayList<>();
		tokenizerIsConfigurableList = new ArrayList<>();
		for (int i = 0; i < tokenizers.length; i++) {
			tokenizerNameList.add(i, tokenizers[i].getAttribute("name"));
			tokenizerDescriptionList.add(i, tokenizers[i].getAttribute("description"));
			tokenizerCreatorList.add(i, tokenizers[i].getAttribute("creator"));
			tokenizerIsConfigurableList.add(i, tokenizers[i].getAttribute("wizardPage") != null);
		}
		
		final Group descriptionGroup = new Group(container, SWT.NONE);
		descriptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		GridLayout descriptionGroupLayout = new GridLayout(1, true);
		descriptionGroupLayout.marginHeight = 10;
		descriptionGroupLayout.marginWidth = 10;
		descriptionGroup.setLayout(descriptionGroupLayout);
		descriptionGroup.setText("Description");

		descriptionLabel = new Label(descriptionGroup, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
		descriptionLabel.setText(tokenizerDescriptionList.get(0));
		
		createTokenizerControls(container);
		
		setControl(container);
	}

	/**
	 * TODO: Description
	 *
	 * @param container
	 */
	private void createTokenizerControls(final Composite container) {
		Label tokenizerLabel = new Label(container, SWT.NONE);
		tokenizerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizerLabel.setText("Tokenizer:");
		final Combo tokenizerCombo = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		tokenizerCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		for (int j = 0; j < tokenizerNameList.size(); j++) {
			tokenizerCombo.add(tokenizerNameList.get(j), j);
		}
		if (tokenizerCombo.getItemCount() > 0) {
			tokenizerCombo.select(0);
			setTokenizer(tokenizers[0]);
		}
		
		Label creatorLabel = new Label(container, SWT.NONE);
		creatorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		creatorLabel.setText("Creator:");
		final Text creatorText = new Text(container, SWT.READ_ONLY);
		creatorText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		creatorText.setText(tokenizerCreatorList.get(tokenizerCombo.getSelectionIndex()));
		
		Label configurableLabel = new Label(container, SWT.NONE);
		configurableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		configurableLabel.setText("Configurable:");
		final Button configurableButton = new Button(container, SWT.CHECK);
		configurableButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		configurableButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.widget;
				btn.setSelection(!configurableButton.getSelection());
				log.debug("Caught selection event on \"Configurable\" check box and nullified it (i.e., restored the previous state, {}).", btn.getSelection());
			}
		});
		tokenizerCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				int index = tokenizerCombo.getSelectionIndex();
				descriptionLabel.setText(tokenizerDescriptionList.get(index));
				creatorText.setText(tokenizerCreatorList.get(index));
				configurableButton.setSelection(tokenizerIsConfigurableList.get(index));
				setTokenizer(tokenizers[index]);
				container.layout();
			}
		});
	}

	/**
	 * @return the projectStructurePage
	 */
	private NewAtomicProjectWizardPageProjectStructure getProjectStructurePage() {
		return projectStructurePage;
	}

	/**
	 * @param projectStructurePage the projectStructurePage to set
	 */
	private void setProjectStructurePage(NewAtomicProjectWizardPageProjectStructure projectStructurePage) {
		this.projectStructurePage = projectStructurePage;
	}

	/**
	 * @return the tokenizer
	 */
	public IConfigurationElement getTokenizer() {
		return tokenizer;
	}

	/**
	 * @param tokenizer the tokenizer to set
	 */
	private void setTokenizer(IConfigurationElement tokenizer) {
		this.tokenizer = tokenizer;
	}

}
