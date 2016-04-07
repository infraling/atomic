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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
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
		// Set up local vars
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
		final ArrayList<Composite> activeTokenizerWidgets = new ArrayList<>();
				
		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout containerLayout = new GridLayout(2, false);
		containerLayout.verticalSpacing = 30;
		container.setLayout(containerLayout);
		
		Label infoLabel = new Label(container, SWT.WRAP);
		infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		infoLabel.setText("The basic annotatable unit in Atomic's data model is the token, hence in order to create annotations at all, the corpus documents must be tokenized.\n"
				+ "Please pick the tokenizer that should be used to tokenize all documents in the corpus. Note that tokens can be added or removed, and completely new token layers created later.");
		
		Label separator = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
	    separator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		final Group descriptionGroup = new Group(container, SWT.NONE);
		descriptionGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		GridLayout descriptionGroupLayout = new GridLayout(1, true);
		descriptionGroupLayout.marginHeight = 10;
		descriptionGroupLayout.marginWidth = 10;
		descriptionGroup.setLayout(descriptionGroupLayout);
		descriptionGroup.setText("Description of currently selected tokenizer");

		descriptionLabel = new Label(descriptionGroup, SWT.WRAP);
		descriptionLabel.setLayoutData(new GridData(SWT.HORIZONTAL, SWT.TOP, true, false, 1, 1));
		descriptionLabel.setText(tokenizerDescriptionList.get(0));
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		// Spinner
	    Label spinnerLabel = new Label(container, SWT.NONE);
	    spinnerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		spinnerLabel.setText("Number of tokenizers to apply to corpus:");
		Spinner tokenizersSpinner = new Spinner(container, SWT.BORDER);
		tokenizersSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizersSpinner.setMinimum(1);
		tokenizersSpinner.setIncrement(1);
		tokenizersSpinner.setSelection(1);
		tokenizersSpinner.setPageIncrement(5);
		
		// Composites
		final Composite tokenizerContainer = new Composite(container, SWT.NONE);
		tokenizerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		tokenizerContainer.setLayout(new FillLayout());
		
		final ScrolledComposite scrolledComposite = new ScrolledComposite(tokenizerContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
        scrolledComposite.setExpandHorizontal(true);
        scrolledComposite.setExpandVertical(true);

        final Composite intermediateComposite = new Composite(scrolledComposite, SWT.NONE);
        intermediateComposite.setLayout(new GridLayout(2, false));
        scrolledComposite.setContent(intermediateComposite);
        scrolledComposite.setSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        
        final Composite tokenizerCompositeContainer = new Composite(intermediateComposite, SWT.NONE);
        tokenizerCompositeContainer.setLayout(new GridLayout(3, false));
        tokenizerCompositeContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		tokenizersSpinner.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner spinner = (Spinner) e.widget;
				int spinnerValue = Integer.parseInt(spinner.getText());
				int numberOfActiveTokenizerWidgets = activeTokenizerWidgets.size();
				int indexOfFirstWidgetToRemove = numberOfActiveTokenizerWidgets - spinnerValue;
				if (spinnerValue < numberOfActiveTokenizerWidgets) {
					for (Composite compositeToClear : activeTokenizerWidgets.subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets)) {
						compositeToClear.dispose();
					}
					activeTokenizerWidgets.subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets).clear();
				}
				else if (spinnerValue > numberOfActiveTokenizerWidgets) { // Add
					int numberOfWidgetsToAdd = (spinnerValue - numberOfActiveTokenizerWidgets);
					for (int i = 0; i < numberOfWidgetsToAdd; i++) {
						activeTokenizerWidgets.add(createTokenizerControls(tokenizerCompositeContainer, container, activeTokenizerWidgets.size() + 1));
					}
				}
				else return;

				container.layout();
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}
		});

		if (activeTokenizerWidgets.isEmpty()) {
			activeTokenizerWidgets.add(createTokenizerControls(tokenizerCompositeContainer, container, 1));
		}

		setControl(container);
	}

	/**
	 * TODO: Description
	 *
	 * @param areaContainer
	 * @param i 
	 */
	private Composite createTokenizerControls(final Composite areaContainer, final Composite parent, int i) {
		final Composite tokenizerArea = new Composite(areaContainer, SWT.BORDER_DOT);
		tokenizerArea.setLayout(new GridLayout(3, false));
		tokenizerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Group group = new Group(tokenizerArea, SWT.BORDER);
		group.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		GridLayout tokCompLayout = new GridLayout(2, false);
		tokCompLayout.marginHeight = 20;
		tokCompLayout.marginWidth = 20;
		group.setLayout(tokCompLayout);
		group.setText("Tokenizer");
		
		Label tokenizerLabel = new Label(group, SWT.NONE);
		tokenizerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizerLabel.setText("Tokenizer " + i + ":");
		final Combo tokenizerCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		tokenizerCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		for (int j = 0; j < tokenizerNameList.size(); j++) {
			tokenizerCombo.add(tokenizerNameList.get(j), j);
		}
		if (tokenizerCombo.getItemCount() > 0) {
			tokenizerCombo.select(0);
			setTokenizer(tokenizers[0]);
		}
		
		Label creatorLabel = new Label(group, SWT.NONE);
		creatorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		creatorLabel.setText("Creator:");
		final Text creatorText = new Text(group, SWT.READ_ONLY);
		creatorText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		creatorText.setText(tokenizerCreatorList.get(tokenizerCombo.getSelectionIndex()));
		
		Label configurableLabel = new Label(group, SWT.NONE);
		configurableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		configurableLabel.setText("Configurable:");
		final Button configurableButton = new Button(group, SWT.CHECK);
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
				areaContainer.layout();
				parent.layout();
			}
		});
		return tokenizerArea;
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
