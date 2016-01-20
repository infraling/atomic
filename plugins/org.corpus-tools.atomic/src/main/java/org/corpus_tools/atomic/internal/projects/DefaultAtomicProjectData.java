/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.IAtomicProjectData;

/**
 * This internal class is the default implementation of {@link IAtomicProjectData}. It is not API and will only be used internally.
 * <p>
 * Clients should implement their own project data class if necessary.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class DefaultAtomicProjectData implements IAtomicProjectData {

	private String projectName;

	/**
	 * @param projectNameInput
	 */
	public DefaultAtomicProjectData(String projectName) {
		this.projectName  = projectName;
	}

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DefaultAtomicProjectData".
	 */
	private static final Logger log = LogManager.getLogger(DefaultAtomicProjectData.class);

	/**
	 * A map of corpora, whose key is the corpus name, and whose value is a sorted set of
	 * pairs, of which the left value is the name of a document contained in the corpus,
	 * and of which the right value is the source text of this document.
	 */
	private Map<String, LinkedHashSet<Pair<String, String>>> corpora = new HashMap<>(); // TODO: Set to final?

	/**
	 * A map of corpora which function as root corpora, whose key is the name of the
	 * root corpus, and whose value is a set of names of its sub corpora.
	 */
	private Map<String, Set<String>> rootCorpora = new HashMap<>();

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IAtomicProjectData#createDocumentAndAddToCorpus(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void createDocumentAndAddToCorpus(String corpusName, String documentName, String documentSourceText) {
		Pair<String, String> document = new MutablePair<String, String>(documentName, documentSourceText);

		// Check if the corpus is already in the list of corpora
		if (getCorpora().containsKey(corpusName)) {
			LinkedHashSet<Pair<String, String>> corpus = getCorpora().get(corpusName);

			/*
			 * Check if a document with #documentName already exists in corpus. 
			 * If it does, replace its source text. 
			 * If it doesn't, add it.
			 */
			Pair<String, String> documentInCorpus = null;
			for (Iterator<Pair<String, String>> iterator = corpus.iterator(); iterator.hasNext();) {
				Pair<String, String> nextDocument = (Pair<String, String>) iterator.next();
				if (nextDocument.getKey().equals(documentName)) {
					documentInCorpus = nextDocument;
				}
			}
			if (documentInCorpus != null) {
				if (!replaceDocumentSourceText(documentInCorpus, documentSourceText)) {
					log.warn("Source text in {} could not be replaced with new source text ({})!", documentName, documentSourceText);
				}
			}
			else {
				boolean isDocumentAddedToCorpus = getCorpora().get(corpusName).add(document);
				if (!isDocumentAddedToCorpus) {
					log.warn("Could not add document {} to corpus {}.", document, corpusName);
				}
			}
		}
		else {
			LinkedHashSet<Pair<String, String>> newDocumentSet = new LinkedHashSet<>();
			newDocumentSet.add(document);
			getCorpora().put(corpusName, newDocumentSet);
		}
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.AbstractAtomicProjectData#replaceDocumentSourceText(org.apache.commons.lang3.tuple.Pair, java.lang.String)
	 */
//	@Override
//	public boolean replaceDocumentSourceText(Pair<String, String> documentInCorpus, String replacementSourceText) {
//	}

	/**
	 * @return The map of corpora, or an empty {@link Map}.
	 */
	public Map<String, LinkedHashSet<Pair<String, String>>> getCorpora() {
		return corpora;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IAtomicProjectData#getProjectName()
	 */
	public String getProjectName() {
		return projectName;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IAtomicProjectData#replaceDocumentSourceText(org.apache.commons.lang3.tuple.Pair, java.lang.String)
	 */
	@Override
	public boolean replaceDocumentSourceText(Pair<String, String> documentInCorpus, String replacementSourceText) {
		String originalSourceText = documentInCorpus.getValue();
		if (originalSourceText.equals(replacementSourceText)) {
			return true; // Not logically correct, but nothing actually changes.
		}
		originalSourceText = documentInCorpus.setValue(replacementSourceText); // SET is called here!
		return !replacementSourceText.equals(originalSourceText);
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IAtomicProjectData#getRootCorpora()
	 */
	@Override
	public Map<String, Set<String>> getRootCorpora() {
		return rootCorpora;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.projects.IAtomicProjectData#createCorpus(java.lang.String, java.lang.String)
	 */
	@Override
	public void createCorpus(String rootCorpusName, String corpusName) {
		if (rootCorpusName != null) {
			Set<String> rootCorpus = getRootCorpora().get(rootCorpusName);
			if (rootCorpus != null) {
				rootCorpus.add(corpusName);
			}
			else {
				getRootCorpora().put(rootCorpusName, new HashSet<String>(Arrays.asList(new String[]{corpusName})));
			}
		}
		getCorpora().put(corpusName, null);
	}
	
}
