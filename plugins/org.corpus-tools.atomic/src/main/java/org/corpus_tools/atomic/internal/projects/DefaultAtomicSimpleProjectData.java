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
package org.corpus_tools.atomic.internal.projects;

import java.util.Iterator;
import java.util.LinkedHashSet;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.AtomicProjectData;
import org.corpus_tools.atomic.projects.AtomicSimpleProjectData;

/**
 * For documentation see the documentation at {@link AtomicProjectData}.
 * 
 * @see org.corpus_tools.atomic.projects.AtomicProjectData
 *
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class DefaultAtomicSimpleProjectData extends DefaultAtomicProjectData implements AtomicSimpleProjectData {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DefaultAtomicSimpleProjectData".
	 */
	private static final Logger log = LogManager.getLogger(DefaultAtomicSimpleProjectData.class);
	
	private Pair<String, LinkedHashSet<Pair<String, String>>> corpus = null;

	/**
	 * Constructor taking the project name as well as the name of the single corpus contained in the project.
	 * Also initializes the single corpus.
	 * 
	 * @see org.corpus_tools.atomic.internal.projects.DefaultAtomicProjectData
	 * @param projectName
	 */
	public DefaultAtomicSimpleProjectData(String projectName, String corpusName) {
		super(projectName);
		this.corpus = new MutablePair<String, LinkedHashSet<Pair<String,String>>>(corpusName, null);
	}
	
	/*
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicSimpleProjectData#createDocumentAndAddToCorpus(java.lang.String, java.lang.String)
	 */
	@Override
	public void createDocumentAndAddToCorpus(String documentName, String documentSourceText) {
		Pair<String, String> document = new MutablePair<String, String>(documentName, documentSourceText);

		// Check if corpus has documents, if not, add this.
		if (getCorpus().getRight() == null) {
			log.trace("Corpus does not contain any documents yet, adding {} as the first one!", document);
			getCorpus().setValue(new LinkedHashSet<Pair<String, String>>());
		}
		
		// Check if a document with #documentName already exists in corpus. If it does, grab it.
		Pair<String, String> documentInCorpus = null;		
		Iterator<Pair<String, String>> documentIterator = getCorpus().getRight().iterator();
		while (documentIterator.hasNext()) {
			Pair<java.lang.String, java.lang.String> nextDocument = (Pair<java.lang.String, java.lang.String>) documentIterator.next();
			if (nextDocument.getKey().equals(documentName)) {
				documentInCorpus = nextDocument;
			}
		}
		
		// If #documentInCorpus is not null, it is already in the corpus, hence, replace its source text.
		if (documentInCorpus != null) {
			if (replaceDocumentSourceText(documentInCorpus, documentSourceText)) {
				log.warn("Source text in {} could not be replaced with new source text ({})!", documentName, documentSourceText);
			}
		}
		// #documentInCorpus is still null, therefore it doesn't exist in the corpus yet, add it.
		else {
			boolean isDocumentAddedToCorpus = getCorpus().getRight().add(document);
			if (!isDocumentAddedToCorpus) {
				log.warn("Could not add document {} to corpus {}.", document, getCorpus().getLeft());
			}
		}
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AtomicSimpleProjectData#getCorpus()
	 */
	@Override
	public Pair<String, LinkedHashSet<Pair<String, String>>> getCorpus() {
		return corpus;
	}

}
