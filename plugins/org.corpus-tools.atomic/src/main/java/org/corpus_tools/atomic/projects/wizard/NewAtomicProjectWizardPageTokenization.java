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
import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
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
 * A wizard page that lets the user choose one or more availableTokenizerExtensions 
 * that will be used to create tokenization layers on the documents of a corpus.
 * <p>
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class NewAtomicProjectWizardPageTokenization extends WizardPage {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizardPageTokenization".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizardPageTokenization.class);

	private static final String CONFIGURATION_ELEMENT = "Configuration element";
	private static final String TOKENIZER_OBJECT = "Tokenizer object";

	private static final String CONFIGURE_BUTTON_TEXT = "Configure";

	final private IConfigurationElement[] availableTokenizerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");

	private Label descriptionLabel;

	private Composite targetTokenizerContainer;

	private Composite sourceTokenizerContainer;

	/**
	 * @param projectStructurePage
	 */
	public NewAtomicProjectWizardPageTokenization() {
		super("Tokenize the corpus documents");
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

		Composite outerTokenizerPanelContainer = new Composite(container, SWT.BORDER);
		outerTokenizerPanelContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		outerTokenizerPanelContainer.setLayout(new GridLayout(2, true));
		
		Label availableTokenizersLbl = new Label(outerTokenizerPanelContainer, SWT.NONE);
		availableTokenizersLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		availableTokenizersLbl.setText("Available availableTokenizerExtensions");

		Label tokenizersToUseLbl = new Label(outerTokenizerPanelContainer, SWT.NONE);
		tokenizersToUseLbl.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
		tokenizersToUseLbl.setText("Active availableTokenizerExtensions");

		Composite leftScrollContainer = new Composite(outerTokenizerPanelContainer, SWT.BORDER);
		leftScrollContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		leftScrollContainer.setLayout(new FillLayout());
		Composite rightScrollContainer = new Composite(outerTokenizerPanelContainer, SWT.BORDER);
		rightScrollContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		rightScrollContainer.setLayout(new FillLayout());

		final ScrolledComposite leftScrolledComposite = new ScrolledComposite(leftScrollContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		leftScrolledComposite.setExpandHorizontal(true);
		leftScrolledComposite.setExpandVertical(true);

		final Composite leftIntermediateComposite = new Composite(leftScrolledComposite, SWT.NONE);
		leftIntermediateComposite.setLayout(new GridLayout(2, false));
		leftScrolledComposite.setContent(leftIntermediateComposite);
		leftScrolledComposite.setSize(leftIntermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		sourceTokenizerContainer = new Composite(leftIntermediateComposite, SWT.NONE);
		sourceTokenizerContainer.setLayout(new GridLayout());
		sourceTokenizerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		final ScrolledComposite rightScrolledComposite = new ScrolledComposite(rightScrollContainer, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		rightScrolledComposite.setExpandHorizontal(true);
		rightScrolledComposite.setExpandVertical(true);

		final Composite rightIntermediateComposite = new Composite(rightScrolledComposite, SWT.NONE);
		rightIntermediateComposite.setLayout(new GridLayout(2, false));
		rightScrolledComposite.setContent(rightIntermediateComposite);
		rightScrolledComposite.setSize(rightIntermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

		targetTokenizerContainer = new Composite(rightIntermediateComposite, SWT.NONE);
		targetTokenizerContainer.setLayout(new GridLayout());
		targetTokenizerContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		
		addDropListener(sourceTokenizerContainer, container, leftScrolledComposite, leftIntermediateComposite, targetTokenizerContainer, rightScrolledComposite, rightIntermediateComposite);
		addDropListener(targetTokenizerContainer, container, rightScrolledComposite, rightIntermediateComposite, sourceTokenizerContainer, leftScrolledComposite, leftIntermediateComposite);
		Control[] children = createChildren(sourceTokenizerContainer, container, leftScrolledComposite, leftIntermediateComposite, targetTokenizerContainer, rightScrolledComposite, rightIntermediateComposite);
		for (Control control : children) {
			addDragListener(control);
		}

		setControl(container);

		// Refresh everything once
		parent.layout(true);
		container.layout(true);
		sourceTokenizerContainer.layout(true);
		targetTokenizerContainer.layout(true);
		leftScrolledComposite.layout(true);
		leftScrolledComposite.setMinSize(leftIntermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		rightScrolledComposite.layout(true);
		rightScrolledComposite.setMinSize(rightIntermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/**
	 * Constructs the widgets for each tokenizer:
	 * <p>
	 * <ul>
	 * <li>a label with the tokenizer name</li>
	 * <li>a button a click upon which will show the description text in the GUI</li>
	 * <li>if the tokenizer is configurable:</i>
	 * 	<ul>
	 * 	<li>a button which opens the configuration dialog</li>
	 * 	<li>a label "Configured?"</li>
	 * 	<li>a checkbox showing whether the tokenizer has been configured</li>
	 * 	</ul>
	 * </ul>
	 * <p>
	 * Returns an array containing all created controls.
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
			final IConfigurationElement tokenizer = availableTokenizerExtensions[i];
			boolean isTokenizerConfigurable = (tokenizer.getAttribute("configuration") != null && !tokenizer.getAttribute("configuration").isEmpty());
			final Composite tokenizerArea = new Composite(parent, SWT.BORDER);
			tokenizerArea.setData(CONFIGURATION_ELEMENT, tokenizer);
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
					descriptionLabel.setText(((IConfigurationElement) ((Control) e.widget).getParent().getData(CONFIGURATION_ELEMENT)).getAttribute("description"));
					parent.layout();
					container.layout();
				}
			});
			controls.add(showInfoBtn);
			
			if (isTokenizerConfigurable) {
    			Button configureBtn = new Button(tokenizerArea, SWT.PUSH);
    			configureBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    			configureBtn.setText(CONFIGURE_BUTTON_TEXT);
    			configureBtn.setEnabled(false);
    			controls.add(configureBtn);
    			
    			Label empty = new Label(tokenizerArea, SWT.NONE);
    			empty.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
    			
    			Label configuredLabel = new Label(tokenizerArea, SWT.NONE);
    			configuredLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
    			configuredLabel.setText("Configured:");
    			
        		final Button isConfiguredBtn = new Button(tokenizerArea, SWT.CHECK);
        		isConfiguredBtn.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1));
        		isConfiguredBtn.setSelection(false);
        		isConfiguredBtn.addSelectionListener(new SelectionAdapter() {
        			@Override
        			public void widgetSelected(SelectionEvent e) {
        				Button btn = (Button) e.widget;
        				btn.setSelection(!isConfiguredBtn.getSelection());
        				log.debug("Caught selection event on \"is configured?\" check box and nullified it (i.e., restored the previous state, {}).", btn.getSelection());
        			}
        		});
        		controls.add(isConfiguredBtn);
        		
        		// Add listener to configureBtn now only, as it need isConfiguredButton to exist.
        		configureBtn.addSelectionListener(new SelectionAdapter() {
    				@Override
    				public void widgetSelected(SelectionEvent e) {
    					try {
							ProcessingComponentConfiguration<?> configuration = (ProcessingComponentConfiguration<?>) tokenizer.createExecutableExtension("configuration");
						}
						catch (CoreException e1) {
							log.error("Could not create an executable extension for tokenizer configuration {}!", tokenizer.getAttribute("configuration"), e1);
						}
    					
//    					tokenizerArea.getData(TOKENIZER_OBJECT).setConfiguration()
    				}	
    			});
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

	/**
	 * Adds a drag listener of type to a control.
	 *
	 * @param control
	 */
	private void addDragListener(final Control control) {
		final LocalSelectionTransfer transfer = LocalSelectionTransfer.getTransfer();

		final DragSourceAdapter dragAdapter = new DragSourceAdapter() {
			@Override
			public void dragSetData(final DragSourceEvent event) {
				transfer.setSelection(new StructuredSelection(control));
			}
		};
		final DragSource dragSource;
		dragSource = new DragSource(control, DND.DROP_MOVE | DND.DROP_COPY);
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
			/* 
			 * @copydoc @see org.eclipse.swt.dnd.DropTargetAdapter#drop(org.eclipse.swt.dnd.DropTargetEvent)
			 */
			@Override
			public void drop(final DropTargetEvent event) {
				IConfigurationElement tokenizer = null; 
				final Control droppedTokenizerComposite = (Control) ((StructuredSelection) transfer.getSelection()).getFirstElement();
				final Composite source;
				final Composite target = parent;
				boolean isComposite = false;
				Button configureButton = null;
				
				if (droppedTokenizerComposite instanceof Composite) { // I.e., the dragged object is the whole composite
					source = droppedTokenizerComposite.getParent();
					tokenizer = (IConfigurationElement) droppedTokenizerComposite.getData(CONFIGURATION_ELEMENT);
					isComposite = true;
					for (Control child : ((Composite) droppedTokenizerComposite).getChildren()) {
						if (child instanceof Button && ((Button) child).getText().equals(CONFIGURE_BUTTON_TEXT)) {
							configureButton = (Button) child;
						}
					}
				}
				else {
					source = droppedTokenizerComposite.getParent().getParent();
					tokenizer = (IConfigurationElement) droppedTokenizerComposite.getParent().getData(CONFIGURATION_ELEMENT);
					for (Control child : droppedTokenizerComposite.getParent().getChildren()) {
						if (child instanceof Button && ((Button) child).getText().equals(CONFIGURE_BUTTON_TEXT)) {
							configureButton = (Button) child;
						}
					}
				}
				
				if (source == sourceTokenizerContainer && target == targetTokenizerContainer) {
					if (configureButton != null) {
						configureButton.setEnabled(true);
					}
					if (isComposite) {
						droppedTokenizerComposite.setParent(targetTokenizerContainer);
					}
					else {
						droppedTokenizerComposite.getParent().setParent(targetTokenizerContainer);
					}
					try {
						if (isComposite) {
							droppedTokenizerComposite.setData(TOKENIZER_OBJECT, ((IConfigurationElement) droppedTokenizerComposite.getData(CONFIGURATION_ELEMENT)).createExecutableExtension("class"));
						}
						else {
							droppedTokenizerComposite.getParent().setData(TOKENIZER_OBJECT, ((IConfigurationElement) droppedTokenizerComposite.getData(CONFIGURATION_ELEMENT)).createExecutableExtension("class"));
						}
						log.info("Added tokenizer of type \"{}\" to list of tokenizers to apply.", tokenizer.getAttribute("name"));
					}
					catch (CoreException e) {
						log.error("Could not add a tokenizer of type \"{}\" to the list of tokenizers to use:", tokenizer.getAttribute("name"), e);
					}
				}
				else if (source == targetTokenizerContainer && target == sourceTokenizerContainer) {
					if (configureButton != null) {
						configureButton.setEnabled(false);
					}
					if (isComposite) {
						droppedTokenizerComposite.setParent(sourceTokenizerContainer);
					}
					else {
						droppedTokenizerComposite.getParent().setParent(sourceTokenizerContainer);
					}
					log.info("Removed tokenizer of type \"{}\" from list of tokenizers to apply, but keeping the configuration.", tokenizer.getAttribute("name"));
				}
				else if (source == target) {
					if (isComposite) {
						reOrder(event, (Composite) droppedTokenizerComposite);
					}
					else {
						reOrder(event, droppedTokenizerComposite.getParent());
					}
					log.info("Reordered the list of tokenizers to apply. No model objects changed, as ordering event applies to GUI widgets only.");
				}
				
				source.layout();
				target.layout();
				parent2.layout();
				container.layout();
				scrolledComposite.layout(true, true);
				scrolledComposite.setMinSize(intermediateComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
				scrolledComposite2.layout(true, true);
				scrolledComposite2.setMinSize(intermediateComposite2.computeSize(SWT.DEFAULT, SWT.DEFAULT));
			}

			/**
			 * Reorders the tokenizer widgets upon a drop event
			 * in the target tokenizer container.
			 *
			 * @param event
			 * @param droppedTokenizerComposite
			 */
			private void reOrder(DropTargetEvent event, Composite droppedTokenizerComposite) {
				String name = ((Label) droppedTokenizerComposite.getChildren()[0]).getText();
				Control[] children = targetTokenizerContainer.getChildren();
				Control firstChild = children[0];
				Control lastChild = children[children.length - 1];
				int dropY = targetTokenizerContainer.toControl(event.x, event.y).y;
				int topFirstChild = firstChild.getLocation().y;
				int bottomLastChild = (lastChild.getLocation().y + lastChild.getBounds().height);
				if (dropY < topFirstChild) {
					droppedTokenizerComposite.moveAbove(firstChild);
					log.debug("Moved \"{}\" to the top of the list of tokenizers to apply.", name);
				}
				else if (dropY > bottomLastChild) {
					droppedTokenizerComposite.moveBelow(lastChild);
					log.debug("Moved \"{}\" to the bottom of the list of tokenizers to apply.", name);
				}
				else {
					loopThroughChildren:
					for (int i = 0; i < children.length; i++) {
						Control child = children[i];
						if (child == droppedTokenizerComposite) {
							continue;
						}
						Control nextChild = null;
						boolean isLastChild = (i == (children.length - 1));
						int center = child.getLocation().y + (child.getBounds().height / 2);
						if (!isLastChild) {
							nextChild = children[i + 1];
						}
						String childName = ((Label) ((Composite) child).getChildren()[0]).getText();
						if (dropY < center) {
							droppedTokenizerComposite.moveAbove(child);
							log.debug("Moved \"{}\" above \"{}\" in the list of tokenizers to apply.", name, childName);
							break loopThroughChildren;
						}
						else if (nextChild != null && nextChild instanceof Composite && dropY > center) {
							int nextChildCenter = nextChild.getLocation().y + (nextChild.getBounds().height / 2);
							if (dropY < nextChildCenter) {
								droppedTokenizerComposite.moveBelow(child);
								log.debug("Moved \"{}\" below \"{}\" in the list of tokenizers to apply.", name, childName);
								break loopThroughChildren;
							}
						}
						else if (nextChild == null && dropY > center) {
							droppedTokenizerComposite.moveBelow(child);
							log.debug("Moved \"{}\" below \"{}\" to the very bottom of the list of tokenizers to apply.", name, childName);
							break loopThroughChildren;
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
	 * Constructs a list of tokenizers to apply to corpus documents
	 * from the list of child widgets in the target container.
	 *
	 * @return tokenizerList the list of tokenizers
	 */
	public List<Tokenizer> getTokenizers() {
		List<Tokenizer> tokenizerList = new ArrayList<>();
		Control[] children = targetTokenizerContainer.getChildren();
		for (int i = 0; i < children.length; i++) {
			tokenizerList.add((Tokenizer) children[i].getData(TOKENIZER_OBJECT));
		}
		return tokenizerList;
	}

}
