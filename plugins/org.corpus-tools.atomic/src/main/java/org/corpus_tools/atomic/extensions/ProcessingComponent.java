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
package org.corpus_tools.atomic.extensions;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * A processing component is a component (usually an NLP component)
 * that - abstractly put - manipulates a corpus. In terms of types,
 * implementations of {@link ProcessingComponent}s take an instance
 * of {@link SDocument} and manipulate it. The manipulation will
 * usually be made on the {@link SDocument}'s {@link SDocumentGraph},
 * but can also be made on the {@link SDocument} itself.
 * <p>
 * {@link ProcessingComponent}s need a name, a description, and a unique,
 * path-style ID, where
 * <ul>
 * <li>the <b>name</b> will be used in GUIs to let the user identify
 * the component and its functionality, e.g., "TreeTagger-like Tokenizer";</li>
 * <li>the <b>description</b> will be used in GUIs to inform the user
 * about the functionality of the component, in a brief but comprehensive
 * manner;</li>
 * <li>the <b>unique ID</b> may be used in APIs to identify the component,
 * e.g., "org.corpus_tools.tokenizers.treetaggerlike".</li>
 * </ul>
 * <p>
 * The formal type parameters define the input type and output type used 
 * to restrict parameter and return values for methods customized for
 * specific processing component types. Cf., for example, the
 * abstract classes in sub-package <i>processingcomponent</i> of
 * this package.
 * <p>
 * Note that <b>clients should not implement this interface directly</b>, 
 * but instead extend one of the abstract classes in package 
 * org.corpus_tools.atomic.extensions.processingcomponents.
 * <p>
 * <i>Acknowledgements:</i> I'd like to thank Thomas Krause for pointing
 * me in the right direction concerning the type structure for this 
 * functionality.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public interface ProcessingComponent<InputType, OutputType> {

	/**
	 * Manipulates a given {@link SDocument}. The term "manipulation"
	 * in this context is unrestricted, i.e., it can mean diverse
	 * types of manipulation from partition detection to parsing.
	 *
	 * @param document The document to process
	 */
	public void processDocument(SDocument document);
	
	/**
	 * Returns the component's name.
	 *
	 * @return the components name
	 */
	public String getName();
	
	/**
	 * Returns a brief, comprehensive description of the
	 * component's functionality.
	 *
	 * @return the component's description
	 */
	public String getDescription();
	
	/**
	 * Returns the unique, path-like identifier for
	 * the component.
	 *
	 * @return the unique identifier
	 */
	public String getUID();
	
}
