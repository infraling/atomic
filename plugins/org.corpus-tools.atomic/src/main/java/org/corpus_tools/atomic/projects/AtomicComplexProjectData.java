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
import java.util.Map;

/**
 * For documentation see the documentation at {@link AtomicProjectData}.
 * 
 * @see org.corpus_tools.atomic.projects.AtomicProjectData
 * 
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface AtomicComplexProjectData extends AtomicProjectData {
	
	/**
	 * Returns a {@link Map} of the root corpora contained in this project.
	 * <p>
	 * The map's keys are the names of the root corpora. The map's values
	 * are {@link LinkedHashSet}s of the names of the root corpus' sub-corpora.
	 *
	 * @return a {@link Map} of the root corpora
	 */
	public Map<String, LinkedHashSet<String>> getRootCorpora();
	
	/**
	 * Creates a "document", i.e. a pair of document name and document text, and
	 * adds this document to a specific corpus under the single root corpus.
	 * <p>
	 * <b>NOTE: This method should be used in cases where a project only contains
	 * one single root corpus.</b> 
	 * <p>
	 * <b>NOTE: Clients are responsible for providing the possibility to set the
	 * name of the single root corpus.</b> 
	 * <p>
	 * If the respective corpus already exists under the single root corpus, get that corpus, 
	 * and attach the document to the corpus. If the corpus doesn't not exist, create a
	 * new set to take up all documents for that corpus, and add the corpus 
	 * (here: corpus name) to the list of corpora, bringing its (newly created) document set.
	 *
	 * @param rootCorpusName
	 * @param corpusName
	 * @param documentName
	 * @param documentSourceText
	 */
	public void createDocumentAndAddToSingleRootSubCorpus(String subCorpusName, String documentName, String documentSourceText);
	
	/**
	 * Creates a "document", i.e. a pair of document name and document text, and
	 * adds this document to a specific corpus under the specified root corpus.
	 * <p>
	 * If the respective corpus already exists under the specified root corpus, get that corpus, 
	 * and attach the document to the corpus. If the corpus doesn't not exist, create a
	 * new set to take up all documents for that corpus, and add the corpus 
	 * (here: corpus name) to the list of corpora, bringing its (newly created) document set.
	 *
	 * @param rootCorpusName
	 * @param corpusName
	 * @param documentName
	 * @param documentSourceText
	 */
	public void createDocumentAndAddToSubCorpus(String rootCorpusName, String corpusName, String documentName, String documentSourceText);

}
