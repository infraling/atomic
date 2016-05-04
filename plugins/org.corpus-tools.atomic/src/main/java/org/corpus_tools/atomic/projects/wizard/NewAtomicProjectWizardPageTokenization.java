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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.plaf.synth.SynthSliderUI;

import java.util.TreeMap;

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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
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

	final private IConfigurationElement[] availableTokenizerExtensions = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");

	private Label descriptionLabel;

	private final List<Tokenizer> tokenizersToUse = new ArrayList<>();
	
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
		
		//################################################
//		Transfer[] types = new Transfer[] { TextTransfer.getInstance() };
//	    DragSource source = new DragSource(documentList, DND.DROP_MOVE | DND.DROP_COPY);
//	    source.setTransfer(types);
//		DropTarget target = new DropTarget(targetTokenizerContainer, DND.DROP_MOVE | DND.DROP_COPY
//	            | DND.DROP_DEFAULT);
//	    target.setTransfer(types);
//	    target.addDropListener(new DropTargetAdapter()
//	    {
//	        @Override
//	        public void dragEnter(DropTargetEvent event)
//	        {
//	            if (event.detail == DND.DROP_DEFAULT)
//	            {
//	                event.detail = (event.operations & DND.DROP_COPY) != 0 ? DND.DROP_COPY
//	                        : DND.DROP_NONE;
//	            }
//
//	            // Allow dropping text only
//	            for (int i = 0, n = event.dataTypes.length; i < n; i++)
//	            {
//	                if (TextTransfer.getInstance().isSupportedType(event.dataTypes[i]))
//	                {
//	                    event.currentDataType = event.dataTypes[i];
//	                }
//	            }
//	        }
//
//	        @Override
//	        public void dragOver(DropTargetEvent event)
//	        {
//	            event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL;
//	        }
//
//	        @Override
//	        public void drop(DropTargetEvent event)
//	        {
//	            if (TextTransfer.getInstance().isSupportedType(event.currentDataType))
//	            {
//	                  //HELP: How to get the element were the drop occurs and swap it with the draged element.
//	            }
//	        }
//	    });
		
		
		// ###############################################
		
		addDropListener(sourceTokenizerContainer, container, leftScrolledComposite, leftIntermediateComposite, targetTokenizerContainer, rightScrolledComposite, rightIntermediateComposite);
		addDropListener(targetTokenizerContainer, container, rightScrolledComposite, rightIntermediateComposite, sourceTokenizerContainer, leftScrolledComposite, leftIntermediateComposite);
		final Control[] children = createChildren(sourceTokenizerContainer, container, leftScrolledComposite, leftIntermediateComposite, targetTokenizerContainer, rightScrolledComposite, rightIntermediateComposite);
		for (final Control control : children) {
			addDragListener(control);
		}

		setControl(container);
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
			IConfigurationElement tokenizer = availableTokenizerExtensions[i];
			boolean isTokenizerConfigurable = (tokenizer.getAttribute("configuration") != null && !tokenizer.getAttribute("configuration").isEmpty());
			final Composite tokenizerArea = new Composite(parent, SWT.BORDER);
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
				
				final Control droppedTokenizerComposite = (Control) ((StructuredSelection) transfer.getSelection()).getFirstElement();
				final Composite source;
				final Composite target = parent;
				boolean isComposite = false;
				
				if (droppedTokenizerComposite instanceof Composite) { // I.e., the dragged object is the whole composite
					source = droppedTokenizerComposite.getParent();
					isComposite = true;
				}
				else {
					source = droppedTokenizerComposite.getParent().getParent();
				}
				
				if (source == sourceTokenizerContainer && target == targetTokenizerContainer) {
					if (isComposite) {
						droppedTokenizerComposite.setParent(targetTokenizerContainer);
						System.err.println("ADD :" + ((Label) ((Composite) droppedTokenizerComposite).getChildren()[0]).getText());
					}
					else {
						droppedTokenizerComposite.getParent().setParent(targetTokenizerContainer);
						System.err.println("ADD :" + ((Label) droppedTokenizerComposite.getParent().getChildren()[0]).getText());
					}
				}
				else if (source == targetTokenizerContainer && target == sourceTokenizerContainer) {
					if (isComposite) {
						droppedTokenizerComposite.setParent(sourceTokenizerContainer);
						System.err.println("REMOVE :" + ((Label) ((Composite) droppedTokenizerComposite).getChildren()[0]).getText());
					}
					else {
						droppedTokenizerComposite.getParent().setParent(sourceTokenizerContainer);
						System.err.println("REMOVE :" + ((Label) droppedTokenizerComposite.getParent().getChildren()[0]).getText());
					}
				}
				else if (source == target) {
					if (isComposite) {
						reOrder(event, droppedTokenizerComposite);
						System.err.println("REORDER :" + ((Label) ((Composite) droppedTokenizerComposite).getChildren()[0]).getText());
					}
					else {
						System.err.println("\n\n----------------------REORDER :" + ((Label) droppedTokenizerComposite.getParent().getChildren()[0]).getText());
						reOrder(event, droppedTokenizerComposite.getParent());
					}
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

			private void reOrder(DropTargetEvent event, Control droppedTokenizerComposite) {
				Control[] children = targetTokenizerContainer.getChildren();
				Control firstChild = children[0];
				Control lastChild = children[children.length - 1];
				int dropY = targetTokenizerContainer.toControl(event.x, event.y).y;
				int topFirstChild = firstChild.getLocation().y;
				int bottomLastChild = (lastChild.getLocation().y + lastChild.getBounds().height);
				if (dropY < topFirstChild) {
					droppedTokenizerComposite.moveAbove(firstChild);
					System.err.println("--- TOP DROP");
				}
				else if (dropY > bottomLastChild) {
					droppedTokenizerComposite.moveBelow(lastChild);
					System.err.println("--- BOTTOM DROP");
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
						if (dropY < center) {
							droppedTokenizerComposite.moveAbove(child);
							System.err.println("--- ABOVE-CENTER DROP");
							break loopThroughChildren;
						}
						else if (nextChild != null && nextChild instanceof Composite && dropY > center) {
							int nextChildCenter = nextChild.getLocation().y + (nextChild.getBounds().height / 2);
							if (dropY < nextChildCenter) {
								droppedTokenizerComposite.moveBelow(child);
								System.err.println("--- IN-BETWEEN DROP");
								break loopThroughChildren;
							}
						}
						else if (nextChild == null && dropY > center) {
							droppedTokenizerComposite.moveBelow(child);
							System.err.println("--- BELOW LAST CHILD'S CENTER DROP");
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
	 * @return the tokenizersToUse
	 */
	private List<Tokenizer> getTokenizersToUse() {
		return tokenizersToUse;
	}

//	/**
//	 * A tokenizer composite references exactly one tokenizer.
//	 * This class is intended to be used for <code>instanceof</code> checks
//	 * only, and is <b>not</b> intended to be sub-classed. 
//	 *
//	 * @author Stephan Druskat <mail@sdruskat.net>
//	 */
//	public class TokenizerComposite extends Composite {
//
//		/**
//		 * @param parent
//		 * @param style
//		 */
//		public TokenizerComposite(Composite parent, int style) {
//			super(parent, style);
//		}
//
//	}

}
