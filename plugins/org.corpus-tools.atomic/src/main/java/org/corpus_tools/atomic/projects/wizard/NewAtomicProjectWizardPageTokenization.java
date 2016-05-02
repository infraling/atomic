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
import java.util.Iterator;
import java.util.LinkedHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.extensions.processingcomponents.ProcessingComponentMetaData;
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * A wizard page that lets the user choose one or more availableTokenizerExtensions that will be used to create tokenization layers on the documents of a corpus.
 * <p>
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class NewAtomicProjectWizardPageTokenization extends WizardPage {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizardPageTokenization".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizardPageTokenization.class);

	private NewAtomicProjectWizardPageProjectStructure projectStructurePage;

	final private IConfigurationElement[] availableTokenizerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");

	private Label descriptionLabel;

	private final ArrayList<Composite> activeTokenizerWidgets = new ArrayList<>();
	private final LinkedHashSet<Tokenizer> tokenizersToUse = new LinkedHashSet<>();
	
	private Composite targetTokenizerContainer;

	private Composite tokenizerSourceContainer;

	/**
	 * @param projectStructurePage
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
		for (int i = 0; i < availableTokenizerExtensions.length; i++) {
			metaDataList.add(i, new ProcessingComponentMetaData().bulkCompleteFields(availableTokenizerExtensions[i]));
		}

		final Composite container = new Composite(parent, SWT.NONE);
		GridLayout containerLayout = new GridLayout(2, false);
		containerLayout.verticalSpacing = 30;
		container.setLayout(containerLayout);

		Label infoLabel = new Label(container, SWT.WRAP);
		infoLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
		infoLabel.setText("The basic annotatable unit in Atomic's data model is the token, hence in order to create annotations at all, the corpus documents must be tokenized.\n" + "Please pick the tokenizer that should be used to tokenize all documents in the corpus. Note that tokens can be added or removed, and completely new token layers created later.");

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
		descriptionLabel.setText(metaDataList.get(0).getDescription());

		// #####################################################################
		Composite c1 = new Composite(container, SWT.BORDER);
		c1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		c1.setLayout(new GridLayout(2, true));
		
		Label availableTokenizersLbl = new Label(c1, SWT.NONE);
		availableTokenizersLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		availableTokenizersLbl.setText("Available availableTokenizerExtensions");

		Label tokenizersToUseLbl = new Label(c1, SWT.NONE);
		tokenizersToUseLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizersToUseLbl.setText("Active availableTokenizerExtensions");

		Composite c1_1 = new Composite(c1, SWT.BORDER);
		c1_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c1_1.setLayout(new FillLayout());
		Composite c1_2 = new Composite(c1, SWT.BORDER);
		c1_2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		c1_2.setLayout(new FillLayout());

		// Inside c1_1
		final ScrolledComposite scrolledComposite = new ScrolledComposite(c1_1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		final Composite intermediateComposite = new Composite(scrolledComposite, SWT.NONE);
		intermediateComposite.setLayout(new GridLayout(2, false));
		scrolledComposite.setContent(intermediateComposite);
		scrolledComposite.setSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		tokenizerSourceContainer = new Composite(intermediateComposite, SWT.NONE);
		tokenizerSourceContainer.setLayout(new GridLayout());
		tokenizerSourceContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		// Inside c1_2
		final ScrolledComposite scrolledComposite2 = new ScrolledComposite(c1_2, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		scrolledComposite2.setExpandHorizontal(true);
		scrolledComposite2.setExpandVertical(true);

		final Composite intermediateComposite2 = new Composite(scrolledComposite2, SWT.NONE);
		intermediateComposite2.setLayout(new GridLayout(2, false));
		scrolledComposite2.setContent(intermediateComposite2);
		scrolledComposite2.setSize(intermediateComposite2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		targetTokenizerContainer = new Composite(intermediateComposite2, SWT.NONE);
		targetTokenizerContainer.setLayout(new GridLayout());
		targetTokenizerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		addDropListener(tokenizerSourceContainer, container, scrolledComposite, intermediateComposite, targetTokenizerContainer, scrolledComposite2, intermediateComposite2);
		addDropListener(targetTokenizerContainer, container, scrolledComposite2, intermediateComposite2, tokenizerSourceContainer, scrolledComposite, intermediateComposite);
		final Control[] children = createChildren(tokenizerSourceContainer, container, scrolledComposite, intermediateComposite, targetTokenizerContainer, scrolledComposite2, intermediateComposite2);
		for (final Control control : children) {
			addDragListener(control);
		}

		// final Composite availableTokenizers = new Composite(c1, SWT.BORDER);
		// availableTokenizers.setLayout(new GridLayout());
		// availableTokenizers.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// final Control[] children = createChildren(availableTokenizers);
		// for (final Control control : children) {
		// addDragListener(control);
		// }
		// addDropListener(availableTokenizers);

		// final Composite tokenizersToUse = new Composite(c1, SWT.BORDER);
		// tokenizersToUse.setLayout(new GridLayout());
		// tokenizersToUse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		// final Control[] children2 = createChildren(tokenizersToUse);
		// for (final Control control : children2) {
		// addDragListener(control);
		// }
		// addDropListener(tokenizersToUse);

		// #####################################################################

		// Label spinnerLabel = new Label(container, SWT.NONE);
		// spinnerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// spinnerLabel.setText("Number of availableTokenizerExtensions to apply to corpus:");
		// Spinner tokenizersSpinner = new Spinner(container, SWT.BORDER);
		// tokenizersSpinner.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// tokenizersSpinner.setMinimum(1);
		// tokenizersSpinner.setIncrement(1);
		// tokenizersSpinner.setSelection(1);
		// tokenizersSpinner.setPageIncrement(5);

		// final Composite tokenizerContainer = new Composite(container, SWT.NONE);
		// tokenizerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		// tokenizerContainer.setLayout(new FillLayout());
		//
		// final ScrolledComposite scrolledComposite = new ScrolledComposite(tokenizerContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		// scrolledComposite.setExpandHorizontal(true);
		// scrolledComposite.setExpandVertical(true);
		//
		// final Composite intermediateComposite = new Composite(scrolledComposite, SWT.NONE);
		// intermediateComposite.setLayout(new GridLayout(2, false));
		// scrolledComposite.setContent(intermediateComposite);
		// scrolledComposite.setSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//
		// final Composite tokenizerCompositeContainer = new Composite(intermediateComposite, SWT.NONE);
		// tokenizerCompositeContainer.setLayout(new GridLayout(3, false));
		// tokenizerCompositeContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		//
		// for (IConfigurationElement tokenizer : availableTokenizerExtensions) {
		// final Composite tokenizerArea = new Composite(tokenizerCompositeContainer, SWT.BORDER_DOT);
		// tokenizerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		// tokenizerArea.setLayout(new GridLayout(4, false));
		//
		// Button activateTokenizerBtn = new Button(tokenizerArea, SWT.CHECK);
		// activateTokenizerBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		//
		// Label name = new Label(tokenizerArea, SWT.NONE);
		// name.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// name.setText(tokenizer.getAttribute("name"));
		//
		// if (tokenizer.getAttribute("wizardPage") != null) {
		// Button showInfoBtn = new Button(tokenizerArea, SWT.PUSH);
		// showInfoBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// showInfoBtn.setText("Show details");
		//
		// Button configureBtn = new Button(tokenizerArea, SWT.PUSH);
		// configureBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// configureBtn.setText("Configure");
		// }
		// else {
		// Button showInfoBtn = new Button(tokenizerArea, SWT.PUSH);
		// showInfoBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		// showInfoBtn.setText("Show details");
		// }
		//
		// }
		//
		// tokenizersSpinner.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// Spinner spinner = (Spinner) e.widget;
		// int spinnerValue = Integer.parseInt(spinner.getText());
		// int numberOfActiveTokenizerWidgets = getActiveTokenizerWidgets().size();
		// int indexOfFirstWidgetToRemove = numberOfActiveTokenizerWidgets - spinnerValue;
		// if (spinnerValue < numberOfActiveTokenizerWidgets) {
		// for (Composite compositeToClear : getActiveTokenizerWidgets().subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets)) {
		// compositeToClear.dispose();
		// }
		// getActiveTokenizerWidgets().subList(numberOfActiveTokenizerWidgets - indexOfFirstWidgetToRemove, numberOfActiveTokenizerWidgets).clear();
		// updateTokenizerSet();
		// }
		// else if (spinnerValue > numberOfActiveTokenizerWidgets) { // Add
		// int numberOfWidgetsToAdd = (spinnerValue - numberOfActiveTokenizerWidgets);
		// for (int i = 0; i < numberOfWidgetsToAdd; i++) {
		// getActiveTokenizerWidgets().add(createTokenizerControls(tokenizerCompositeContainer, container, (getActiveTokenizerWidgets().size() + 1), metaDataList));
		// }
		// }
		// else return;
		//
		// container.layout();
		// scrolledComposite.layout(true, true);
		// scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		//
		// }
		// });

		// if (getActiveTokenizerWidgets().isEmpty()) {
		// getActiveTokenizerWidgets().add(createTokenizerControls(tokenizerCompositeContainer, container, 1, metaDataList));
		// }

		setControl(container);
	}

	/**
	 * TODO: Description
	 *
	 * @param parent
	 * @param intermediateComposite 
	 * @param scrolledComposite 
	 * @param container 
	 * @param intermediateComposite2 
	 * @param scrolledComposite2 
	 * @param parent2 
	 * @return
	 */
	private Control[] createChildren(final Composite parent, final Composite container, ScrolledComposite scrolledComposite, Composite intermediateComposite, Composite parent2, ScrolledComposite scrolledComposite2, Composite intermediateComposite2) {
		ArrayList<Control> controls = new ArrayList<>();
		
		for (int i = 0; i < availableTokenizerExtensions.length; i++) {
			IConfigurationElement tokenizer = availableTokenizerExtensions[i];
			boolean isTokenizerConfigurable = (tokenizer.getAttribute("configuration") != null && !tokenizer.getAttribute("configuration").isEmpty());
			final TokenizerComposite tokenizerArea = new TokenizerComposite(parent, SWT.BORDER);
        	tokenizerArea.setData(tokenizer);
        		tokenizerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
    		tokenizerArea.setLayout(new GridLayout(6, false));
    		
    		Label name = new Label(tokenizerArea, SWT.NONE);
    		name.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    		name.setText(tokenizer.getAttribute("name"));
    		controls.add(name);
    		
    		Button showInfoBtn = new Button(tokenizerArea, SWT.PUSH);
			showInfoBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
			showInfoBtn.setText("Show description");
			showInfoBtn.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					descriptionLabel.setText(((IConfigurationElement) ((Control) e.widget).getParent().getData()).getAttribute("description"));
					parent.layout();
					container.layout();
				}
			});
			controls.add(showInfoBtn);
			
			if (isTokenizerConfigurable) {
    			Button configureBtn = new Button(tokenizerArea, SWT.PUSH);
    			configureBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    			configureBtn.setText("Configure");
    			controls.add(configureBtn);
    			
    			Label empty = new Label(tokenizerArea, SWT.NONE);
    			empty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
    			
    			Label configuredLabel = new Label(tokenizerArea, SWT.NONE);
    			configuredLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    			configuredLabel.setText("Configured:");
    			
        		final Button isConfiguredBtn = new Button(tokenizerArea, SWT.CHECK);
        		isConfiguredBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        		isConfiguredBtn.setSelection(true);
        		isConfiguredBtn.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				Button btn = (Button) e.widget;
        				btn.setSelection(!isConfiguredBtn.getSelection());
        				log.debug("Caught selection event on \"is configured?\" check box and nullified it (i.e., restored the previous state, {}).", btn.getSelection());
        			}
        		});
        		controls.add(isConfiguredBtn);
    		}

    		controls.add(tokenizerArea);
        }
		
		parent.layout();
		parent2.layout();
		container.layout();
		scrolledComposite.layout(true, true);
		scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		scrolledComposite2.layout(true, true);
		scrolledComposite2.setMinSize(intermediateComposite2.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		return controls.toArray(new Control[controls.size()]);
	}

	private void addDragListener(final Control control) {
		// Step 1: Get JFace's LocalSelectionTransfer instance
		final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

		final DragSourceAdapter dragAdapter = new DragSourceAdapter() {
			@Override
			public void dragSetData(final DragSourceEvent event) {
				// Step 2: On drag events, create a new JFace StructuredSelection
				// with the dragged control.
				// if (control instanceof Composite) {
				transfer.setSelection(new StructuredSelection(control));
				// }
				// else {
				// transfer.setSelection(new StructuredSelection(control.getParent()));
				// }
			}
		};
		final DragSource dragSource;
		// if (control instanceof Composite) {
		//
		dragSource = new DragSource(control, DND.DROP_MOVE | DND.DROP_COPY);
		// }
		// else {
		// System.err.println(control.getClass());
		// dragSource = new DragSource(control.getParent(), DND.DROP_MOVE | DND.DROP_COPY);
		// }
		dragSource.setTransfer(new Transfer[] { transfer });
		dragSource.addDragListener(dragAdapter);
	}

	/**
	 * Assumes that either the dropped object or its parent is a 
	 *
	 * @param parent
	 * @param container
	 * @param scrolledComposite
	 * @param intermediateComposite
	 * @param parent2
	 * @param scrolledComposite2
	 * @param intermediateComposite2
	 */
	private void addDropListener(final Composite parent, final Composite container, final ScrolledComposite scrolledComposite, final Composite intermediateComposite, final Composite parent2, final ScrolledComposite scrolledComposite2, final Composite intermediateComposite2) {
		final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

		final DropTargetAdapter dragAdapter = new DropTargetAdapter() {
			@Override
			public void drop(final DropTargetEvent event) {
				IConfigurationElement tokenizer = null; 
				// Step 1: Get first element from the StructuredSelection
				final Control droppedObj = (Control) ((StructuredSelection) transfer.getSelection()).getFirstElement();

				// Step 2: Get that control's parent from which it's being dragged
				final Composite oldParent;
				if (droppedObj instanceof Composite) {
					oldParent = droppedObj.getParent();
					if (droppedObj instanceof TokenizerComposite) {
						tokenizer = (IConfigurationElement) droppedObj.getData();
					}
				}
				else {
					oldParent = droppedObj.getParent().getParent();
					if (droppedObj.getParent() instanceof TokenizerComposite) {
						tokenizer = (IConfigurationElement) droppedObj.getParent().getData();
					}
				}

				// If we drag and drop on the same parent, do nothing
				if (oldParent == parent)
					return;

				// Step 3: Figure out what are we dropping
				// This may be done in the dropAccept implementation
				// if (droppedObj instanceof Label)
				// {
				// final Label droppedLabel = (Label) droppedObj;
				// droppedLabel.setParent(parent); // Change parent
				// }
				//
				// if (droppedObj instanceof Button)
				// {
				// final Button droppedButton = (Button) droppedObj;
				// droppedButton.setParent(parent); // Change parent
				// }

				if (droppedObj instanceof Composite) {
					final Composite droppedButton = (Composite) droppedObj;
					droppedButton.setParent(parent); // Change parent
				}
				else {
					final Composite droppedButton = (Composite) droppedObj.getParent();
					droppedButton.setParent(parent); // Change parent
				}

				// Step 4: Tell all parent that the layout has changed
				// This is not necessary, but it looks nicer.
				oldParent.layout();
				parent.layout();
				parent2.layout();
				container.layout();
				scrolledComposite.layout(true, true);
				scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				scrolledComposite2.layout(true, true);
				scrolledComposite2.setMinSize(intermediateComposite2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				
				if (parent == targetTokenizerContainer) {
					try {
						getTokenizersToUse().add((Tokenizer) tokenizer.createExecutableExtension("class"));
						log.info("Added tokenizer of type \"{}\" to the list of availableTokenizerExtensions to use on the project.", tokenizer.getAttribute("name"));
					}
					catch (CoreException e) {
						log.error("Could not add a tokenizer of type {} to the list of availableTokenizerExtensions to use on the project: ", tokenizer.getAttribute("name"));
					}
				}
				else if (parent == tokenizerSourceContainer) {
					Iterator<Tokenizer> iterator = getTokenizersToUse().iterator();
					while (iterator.hasNext()) {
						Tokenizer tokenizerToRemove = iterator.next();
						if (tokenizer.getAttribute("class").equals(tokenizerToRemove.getClass().getName())) {
							iterator.remove();
							log.info("Removed tokenizer of type \"{}\" from the list of availableTokenizerExtensions to use on the project.", tokenizer.getAttribute("name"));
						}
					}
				}
			}
		};

		final DropTarget dropTarget = new DropTarget(parent, DND.DROP_MOVE | DND.DROP_COPY);
		dropTarget.setTransfer(new Transfer[] { transfer });
		dropTarget.addDropListener(dragAdapter);
	}

	/**
	 * Constructs a {@link Composite} containing widgets to select the tokenizer to use, and info about the selected tokenizer:
	 * <p>
	 * <ul>
	 * <li>a combo box to choose the tokenizer from</li>
	 * <li>a text field displaying the creator of the selected tokenizer</li>
	 * <li>a checkbox showing whether the tokenizer is configurable via a dedicated wizard page</i>
	 * </ul>
	 * <p>
	 * Returns the constructed {@link Composite}.
	 *
	 * @param areaContainer
	 * @param parent
	 * @param placeInListOfTokenizers
	 * @return the constructed composite containing the respective widgets for one tokenizer
	 */
//	private Composite createTokenizerControls(final Composite areaContainer, final Composite parent, final int placeInListOfTokenizers, final ArrayList<ProcessingComponentMetaData> metaDataList) {
//		final Composite tokenizerArea = new Composite(areaContainer, SWT.BORDER_DOT);
//		tokenizerArea.setData(availableTokenizerExtensions[0]);
//		getTokenizerSet().add(availableTokenizerExtensions[0]);
//
//		tokenizerArea.setLayout(new GridLayout(3, false));
//		tokenizerArea.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
//
//		final Group group = new Group(tokenizerArea, SWT.BORDER);
//		group.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
//		GridLayout tokCompLayout = new GridLayout(2, false);
//		tokCompLayout.marginHeight = 20;
//		tokCompLayout.marginWidth = 20;
//		group.setLayout(tokCompLayout);
//		group.setText("Tokenizer");
//
//		Label tokenizerLabel = new Label(group, SWT.NONE);
//		tokenizerLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
//		tokenizerLabel.setText("Tokenizer " + placeInListOfTokenizers + ":");
//		final Combo tokenizerCombo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
//		tokenizerCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
//		for (int j = 0; j < metaDataList.size(); j++) {
//			tokenizerCombo.add(metaDataList.get(j).getName(), j);
//		}
//		if (tokenizerCombo.getItemCount() > 0) {
//			tokenizerCombo.select(0);
//		}
//
//		Label creatorLabel = new Label(group, SWT.NONE);
//		creatorLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
//		creatorLabel.setText("Creator:");
//		final Text creatorText = new Text(group, SWT.READ_ONLY);
//		creatorText.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, false, 1, 1));
//		creatorText.setText(metaDataList.get(tokenizerCombo.getSelectionIndex()).getCreator());
//
//		Label configurableLabel = new Label(group, SWT.NONE);
//		configurableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
//		configurableLabel.setText("Configurable:");
//		final Button configurableButton = new Button(group, SWT.CHECK);
//		configurableButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
//		configurableButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				Button btn = (Button) e.widget;
//				btn.setSelection(!configurableButton.getSelection());
//				log.debug("Caught selection event on \"Configurable\" check box and nullified it (i.e., restored the previous state, {}).", btn.getSelection());
//			}
//		});
//
//		final Button configureButton = new Button(group, SWT.PUSH);
//		configureButton.setBounds(0, 0, 0, 0);
//		configureButton.setSize(0, 0);
//		configureButton.setVisible(false);
//
//		tokenizerCombo.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				int index = tokenizerCombo.getSelectionIndex();
//				ProcessingComponentMetaData metaData = metaDataList.get(index);
//				descriptionLabel.setText(metaData.getDescription());
//				creatorText.setText(metaData.getCreator());
//				configurableButton.setSelection(metaData.isConfigurable());
//				if (metaData.isConfigurable()) {
//					configureButton.setParent(group);
//					configureButton.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
//					configureButton.setText("Configure");
//					configureButton.setVisible(true);
//				}
//				else {
//					configureButton.setLayoutData(null);
//					configureButton.setText("");
//					configureButton.setBounds(0, 0, 0, 0);
//					configureButton.setSize(0, 0);
//					configureButton.setVisible(false);
//				}
//				areaContainer.layout();
//				parent.layout();
//
//				tokenizerArea.setData(availableTokenizerExtensions[index]);
//				updateTokenizerSet(availableTokenizerExtensions[index]);
//			}
//		});
//		return tokenizerArea;
//	}

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
	 * @return the tokenizersToUse
	 */
	private LinkedHashSet<Tokenizer> getTokenizersToUse() {
		return tokenizersToUse;
	}

	/**
	 * A tokenizer composite references exactly one tokenizer.
	 * This class is intended to be used for <code>instanceof</code> checks
	 * only, and is <b>not</b> intended to be sub-classed. 
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 */
	public class TokenizerComposite extends Composite {

		/**
		 * @param parent
		 * @param style
		 */
		public TokenizerComposite(Composite parent, int style) {
			super(parent, style);
		}

	}

}
