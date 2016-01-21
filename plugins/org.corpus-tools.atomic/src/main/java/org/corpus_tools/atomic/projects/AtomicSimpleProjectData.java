/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
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
package org.corpus_tools.atomic.projects;

import java.util.LinkedHashSet;

import org.apache.commons.lang3.tuple.Pair;

/**
 * For documentation see the documentation at {@link AtomicProjectData}.
 * 
 * @see org.corpus_tools.atomic.projects.AtomicProjectData
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface AtomicSimpleProjectData extends AtomicProjectData {
	
	/**
	 * Returns the single corpus in the project, which is a pair where
	 * the left hand side is the name of the corpus, and the right hand
	 * side is a {@link LinkedHashSet} containing the documents, i.e.,
	 * pairs of document name and document source text.
	 *
	 * @return the single corpus
	 */
	public Pair<String, LinkedHashSet<Pair<String, String>>> getCorpus();
	
	/**
	 * Creates a "document", i.e. a pair of document name and document text,  
	 * and attaches the document to the single corpus.
	 *
	 * @param documentName The name of the document
	 * @param documentSourceText The source text of the document
	 */
	public void createDocumentAndAddToCorpus(String documentName, String documentSourceText);

}
