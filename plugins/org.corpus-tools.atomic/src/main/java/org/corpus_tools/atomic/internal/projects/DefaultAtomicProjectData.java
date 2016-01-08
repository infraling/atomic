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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
public class DefaultAtomicProjectData implements IAtomicProjectData {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "DefaultAtomicProjectData".
	 */
	private static final Logger log = LogManager.getLogger(DefaultAtomicProjectData.class);

	private String projectName = null;
	private Map<String, Set<Pair<String, String>>> corpora = new HashMap<>(); // TODO: Set to final?

	/**
	 * Constructor taking the name of the project as argument.
	 */
	public DefaultAtomicProjectData(String projectName) {
		this.projectName = projectName;
	}

	/**
	 * Creates a "document", i.e. a pair of document name and document Text. 
	 * <p>
	 * If the respective corpus already exists in the list of corpora, get that corpus, 
	 * and attach the document to the corpus. If the corpus doesn't not exist, create a
	 * new set to take up all documents for that corpus, and add the corpus 
	 * (here: corpus name) to the list of corpora, bringing its (newly created) document set.
	 *
	 * @param corpusName The name of the corpus
	 * @param documentName The name of the document
	 * @param documentSourceText The source text of the document
	 */
	public void createDocumentAndAddToCorpus(String corpusName, String documentName, String documentSourceText) {
		Pair<String, String> document = new MutablePair<String, String>(documentName, documentSourceText);

		// Check if the corpus is already in the list of corpora
		if (getCorpora().containsKey(corpusName)) {
			Set<Pair<String, String>> corpus = getCorpora().get(corpusName);

			/*
			 * Check if a document with #documentName already exists in corpus. 
			 * If it does, replace its source text. 
			 * If it doesn't add it.
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
			Set<Pair<String, String>> newDocumentSet = new HashSet<>();
			newDocumentSet.add(document);
			getCorpora().put(corpusName, newDocumentSet);
		}
	}

	/**
	 * Replaces the source text of a document with a replacement source text. Returns true if the original source text (returned by {@link Pair#setValue(Object)}) does not equal the replacement source text parameter.
	 *
	 * @param documentInCorpus The document for which the source text should be changed
	 * @param replacementSourceText The replacement source text
	 * @return True if the replacement source does not equal the original source text, otherwise false
	 */
	private boolean replaceDocumentSourceText(Pair<String, String> documentInCorpus, String replacementSourceText) {
		String originalSourceText = documentInCorpus.getValue();
		if (originalSourceText.equals(replacementSourceText)) {
			return true; // Not logically correct, but nothing actually changes.
		}
		originalSourceText = documentInCorpus.setValue(replacementSourceText); // SET is called here!
		return !replacementSourceText.equals(originalSourceText);
	}

	/**
	 * @return the name of the project
	 */
	public String getProjectName() {
		return projectName;
	}

	/**
	 * @return The map of corpora, or an empty {@link Map}.
	 */
	public Map<String, Set<Pair<String, String>>> getCorpora() {
		return corpora;
	}

}
