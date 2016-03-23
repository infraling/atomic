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

import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class TokenizerWizardPage extends WizardPage implements ProcessingComponentWizardPage<Tokenizer> {

	/**
	 * @param pageName
	 */
	protected TokenizerWizardPage(String pageName) {
		super(pageName);
		// TODO Auto-generated constructor stub
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
	}
	
	/**
	 * Returns an instance of the {@link Tokenizer} to be used. The
	 * configuration of the {@link Tokenizer} should be implemented
	 * here, i.e., user input from this page should be applied, and 
	 * the readily configured {@link Tokenizer} returned.
	 * <p>
	 * Pseudo-code example:
	 * <pre>
	 * {@code
	 * ExampleTokenizer tokenizer = new ExampleTokenizer();
	 * tokenizer.setRegex(this.regexTextField.getText());
	 * return tokenizer;
	 * }
	 * </pre>
	 *
	 * @return the readily configured tokenizer
	 */
	public abstract Tokenizer getConfiguredTokenizer();
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.ui.ProcessingComponentWizardPage#getProcessingComponentOutput(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)
	 */
	@Override
	public SDocument getProcessingComponentOutput(SDocument inputDocument) {
		getConfiguredTokenizer().processDocument(inputDocument);
		return inputDocument;
	}

}
