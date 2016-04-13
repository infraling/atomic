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
import java.util.LinkedHashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.extensions.processingcomponents.ProcessingComponentMetaData;
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
 * A wizard page that lets the user choose one or more tokenizers that
 * will be used to create tokenization layers on the documents of a corpus.
 *
 * <p>@author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewAtomicProjectWizardPageTokenization extends WizardPage {
	
	/* TODO:
	 * ####################################################
	 * 
	 * Create new class for tokenizer widget, that has a
	 * field for the type of tokenizer. Thus, the widget list
	 * at the same time contains the actual tokenizer to pass
	 * on as well!
	 *  
	 *  ####################################################
	 */
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizardPageTokenization".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizardPageTokenization.class);
	
	private NewAtomicProjectWizardPageProjectStructure projectStructurePage;
	
	final private IConfigurationElement[] tokenizers = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");

	private Label descriptionLabel;

	private final ArrayList<Composite> activeTokenizerWidgets = new ArrayList<>();
	private final LinkedHashSet<IConfigurationElement> tokenizerSet = new LinkedHashSet<>();

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
		final ArrayList<ProcessingComponentMetaData> metaDataList = new ArrayList<>();
		for (int i = 0; i < tokenizers.length; i++) {
			metaDataList.add(i, new ProcessingComponentMetaData().bulkCompleteFields(tokenizers[i]));
		}
				
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
		descriptionLabel.setText(metaDataList. get(0).getDescription());
		
	    Label spinnerLabel = new Label(container, SWT.NONE);
	    spinnerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		spinnerLabel.setText("Number of tokenizers to apply to corpus:");
		Spinner tokenizersSpinner = new Spinner(container, SWT.BORDER);
		tokenizersSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizersSpinner.setMinimum(1);
		tokenizersSpinner.setIncrement(1);
		tokenizersSpinner.setSelection(1);
		tokenizersSpinner.setPageIncrement(5);
		
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
				int numberOfActiveTokenizerWidgets = getActiveTokenizerWidgets().size();
				int indexOfFirstWidgetToRemove = numberOfActiveTokenizerWidgets - spinnerValue;
				if (spinnerValue < numberOfActiveTokenizerWidgets) {
					for (Composite compositeToClear : getActiveTokenizerWidgets().subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets)) {
						compositeToClear.dispose();
					}
					getActiveTokenizerWidgets().subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets).clear();
					updateTokenizerSet();
				}
				else if (spinnerValue > numberOfActiveTokenizerWidgets) { // Add
					int numberOfWidgetsToAdd = (spinnerValue - numberOfActiveTokenizerWidgets);
					for (int i = 0; i < numberOfWidgetsToAdd; i++) {
						getActiveTokenizerWidgets().add(createTokenizerControls(tokenizerCompositeContainer, container, (getActiveTokenizerWidgets().size() + 1), metaDataList));
					}
				}
				else return;

				container.layout();
				scrolledComposite.layout(true, true);
                scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
                
			}
		});

		if (getActiveTokenizerWidgets().isEmpty()) {
			getActiveTokenizerWidgets().add(createTokenizerControls(tokenizerCompositeContainer, container, 1, metaDataList));
		}

		setControl(container);
	}

	/**
	 * Constructs a {@link Composite} containing widgets to select the 
	 * tokenizer to use, and info about the selected tokenizer:
	 * <p>
	 * <ul>
	 * <li>a combo box to choose the tokenizer from</li>
	 * <li>a text field displaying the creator of the selected tokenizer</li>
	 * <li>a checkbox showing whether the tokenizer is configurable via a
	 * dedicated wizard page</i>
	 * </ul>
	 * <p>
	 * Returns the constructed {@link Composite}.
	 *
	 * @param areaContainer
	 * @param parent
	 * @param placeInListOfTokenizers
	 * @return the constructed composite containing the respective widgets for one tokenizer
	 */
	private Composite createTokenizerControls(final Composite areaContainer, final Composite parent, final int placeInListOfTokenizers, final ArrayList<ProcessingComponentMetaData> metaDataList) {
		final Composite tokenizerArea = new Composite(areaContainer, SWT.BORDER_DOT);
		tokenizerArea.setData(tokenizers[0]);
		getTokenizerSet().add(tokenizers[0]);

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
		tokenizerLabel.setText("Tokenizer " + placeInListOfTokenizers + ":");
		final Combo tokenizerCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		tokenizerCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		for (int j = 0; j < metaDataList.size(); j++) {
			tokenizerCombo.add(metaDataList.get(j).getName(), j);
		}
		if (tokenizerCombo.getItemCount() > 0) {
			tokenizerCombo.select(0);
		}
		
		Label creatorLabel = new Label(group, SWT.NONE);
		creatorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		creatorLabel.setText("Creator:");
		final Text creatorText = new Text(group, SWT.READ_ONLY);
		creatorText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
		creatorText.setText(metaDataList.get(tokenizerCombo.getSelectionIndex()).getCreator());
		
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
				ProcessingComponentMetaData metaData = metaDataList.get(index);
				descriptionLabel.setText(metaData.getDescription());
				creatorText.setText(metaData.getCreator());
				configurableButton.setSelection(metaData.isConfigurable());
				areaContainer.layout();
				parent.layout();
				
				tokenizerArea.setData(tokenizers[index]);
				updateTokenizerSet();
			}
		});
		return tokenizerArea;
	}

	/**
	 * Updates the set of tokenizers by clearing it and re-filling
	 * it from the <code>data</code> fields of the active tokenizer widgets. 
	 *
	 */
	private void updateTokenizerSet() {
		getTokenizerSet().clear();
		for (Composite activeTokenizerWidgets : getActiveTokenizerWidgets()) {
			getTokenizerSet().add((IConfigurationElement) activeTokenizerWidgets.getData());
		}
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
	 * These are the widgets (i.e., {@link Composite}s) which "hold"
	 * the selection for one tokenizer to be used. 
	 * <p>
	 * <b>It is imperative</b> to keep in mind that the {@link Composite#getData()} 
	 * method for each widget will return the actual {@link IConfigurationElement}
	 * for each tokenizer, which can in turn be instantiated at a later
	 * point in time. 
	 * 
	 * @return the activeTokenizerWidgets
	 */
	public ArrayList<Composite> getActiveTokenizerWidgets() {
		return activeTokenizerWidgets;
	}

	/**
	 * @return the tokenizerSet
	 */
	public LinkedHashSet<IConfigurationElement> getTokenizerSet() {
		return tokenizerSet;
	}

}
