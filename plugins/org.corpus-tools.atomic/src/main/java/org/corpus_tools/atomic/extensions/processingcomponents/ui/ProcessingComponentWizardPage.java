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
package org.corpus_tools.atomic.extensions.processingcomponents.ui;

import org.corpus_tools.atomic.extensions.ProcessingComponent;
import org.corpus_tools.atomic.extensions.processingcomponents.CustomProcessingComponent;
import org.corpus_tools.salt.common.SDocument;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

// TODO FIXME: Change to return one or more Processing Components!

/**
 * A {@link ProcessingComponentWizardPage} provides a GUI, embedded
 * in a {@link WizardPage} for an Eclipse JFace {@link Wizard}, e.g.,
 * the "New Wizard". The page is responsible for
 * <ul>
 * <li>Creating the GUI components for the wizard page</li>
 * <li>Collecting preferences and operating instructions for "its" {@link ProcessingComponent},
 * passing them to the component, and supply the component's output via a getter method</li>
 * </ul>
 * Clients must override {@link #getConfiguredProcessingComponent()} and can 
 * optionally override {@link #getProcessingComponentOutput(SDocument)} if
 * more complex processing - outside of the component itself - is needed.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class ProcessingComponentWizardPage extends WizardPage {

	/**
	 * @param pageName
	 */
	public ProcessingComponentWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		/*
		 * TODO: This is the method where all the SWT/JFace widgets go that are
		 * needed for creating the custom wizard page.
		 */
	}
	
	/**
	 * Returns an instance of the {@link ProcessingComponent} to be used. The
	 * configuration of the {@link ProcessingComponent} should be implemented
	 * here, i.e., user input from this page should be applied, and 
	 * the readily configured {@link ProcessingComponent} returned.
	 * <p>
	 * Pseudo-code example:
	 * <pre>
	 * {@code
	 * ProcessingComponent pc = new ExampleTokenizer();
	 * pc.setRegex(this.regexField.getText());
	 * return pc;
	 * }
	 * </pre>
	 *
	 * @return the readily configured processing component
	 */
	public abstract CustomProcessingComponent getConfiguredProcessingComponent();

	/**
	 * Is passed an {@link SDocument} in its initial state (e.g., as
	 * returned from another {@link ProcessingComponent}), and returns
	 * the same {@link SDocument} in its transformed state after it has
	 * been processed by the respective processing component of the
	 * respective type.
	 * <p>
	 * This method will be used in the wizard during performance of
	 * the processing pipeline tasks, possibly as follows in pseudo-
	 * code.
	 * <p>
	 * <pre>
	 * {@code
	 * SDocument document = getSDocumentFromSomewhere();
	 * // document is in its "initial state" (whatever that may be)
	 * document = pageBeforeThisOne.getProcessingComponentOutput(document); // E.g., from a segmenter
	 * // document is now, e.g., sentence segmented.  
	 * document = thisPage.getProcessingComponentOutput(document); // E.g., from this page's tokenizer
	 * // document is now (sentence segmented and) tokenized.
	 * }
	 * </pre>
	 * 
	 * @param inputDocument The input document
	 * @return the transformed input document
	 */
	public SDocument getProcessingComponentOutput(SDocument inputDocument) {
		getConfiguredProcessingComponent().processDocument(inputDocument);
		return inputDocument;
	}

}
