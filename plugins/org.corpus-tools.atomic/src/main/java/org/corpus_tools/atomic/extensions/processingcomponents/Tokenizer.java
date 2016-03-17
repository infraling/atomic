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
package org.corpus_tools.atomic.extensions.processingcomponents;

import java.util.List; 

import org.corpus_tools.atomic.extensions.ProcessingComponent;
import org.corpus_tools.atomic.extensions.ProcessingException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * A semi-abstract tokenizer for raw corpus document source texts. This class
 * provides a default implementation for {@link #processDocument(SDocument)} which
 * uses the {@link List<String>} returned by {@link #tokenize(String)} to
 * create tokens for all {@link STextualDS}s in the {@link SDocument}s
 * {@link SDocumentGraph}.
 * <p>
 * Clients extending this class can choose to 
 * <ul>
 * <li>override {@link #tokenize(String)}, which is the simplest option</li>
 * <li>override {@link #processDocument(SDocument)} which means that they
 * need to handle the {@link SDocument} itself</li>
 * <li>Override both methods for maximum flexibility.</li>
 * <p>
 * This class is meant to be used with Salt 2.1.1.
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class Tokenizer implements ProcessingComponent<String, List<String>> {
	
	// FIXME: Needs unit tests!

	/**
	 * Takes a {@link String} (here the raw source text of a corpus document) and tokenizes it, 
	 * i.e., breaks it into meaningful elements. Returns the complete ordered list of tokens.
	 *
	 * @param rawSourceText The raw text to process
	 * @return an ordered list of String representations of the resulting tokens
	 */
	public abstract List<String> tokenize(String rawSourceText);
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponent#processDocument(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)
	 */
	@Override
	public void processDocument(SDocument document) {
		if (document.getSDocumentGraph() == null) {
			throw new ProcessingException("The processed document does not have an SDocumentGraph!", new NullPointerException());
		}
		SDocumentGraph documentGraph = document.getSDocumentGraph();
		if (documentGraph.getSTextualDSs().get(0) == null) {
			throw new ProcessingException("The processed document's document graph does not have an STextualDS!", new NullPointerException());
		}
		for (STextualDS sTextualDS : documentGraph.getSTextualDSs()) {
			String documentSourceText = sTextualDS.getSText(); // We assume that there is only one STextualDS in the SDocumentGraph
			List<String> tokens = tokenize(documentSourceText);

			// Adapted from de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.impl.SDocumentGraphImpl
			if ((tokens != null) && (tokens.size() > 0)) {
				EList<SToken> tokenEList = null;
				char[] documentSourceTextChars = documentSourceText.toCharArray();
				int tokenCounter = 0;
				for (int i = 0; i < documentSourceTextChars.length; i++) {
					if ((tokens.get(tokenCounter).length() < 1) || (tokens.get(tokenCounter).substring(0, 1).equals(String.valueOf(documentSourceTextChars[i])))) { // first letter matches
						StringBuffer pattern = new StringBuffer();
						for (int y = 0; y < tokens.get(tokenCounter).length(); y++) {// compute pattern in text
							pattern.append(documentSourceTextChars[i + y]);
						}
						if (tokens.get(tokenCounter).hashCode() == pattern.toString().hashCode()) {// pattern found
							int start = i;
							int end = i + documentSourceText.length() + tokens.get(tokenCounter).length();

							SToken sTok = documentGraph.createSToken(sTextualDS, start, end);
							if (tokenEList == null) {
								tokenEList = new BasicEList<SToken>();
							}
							tokenEList.add(sTok);
							i = i + tokens.get(tokenCounter).length() - 1;
							tokenCounter++;
							if (tokenCounter >= tokens.size()) {
								break;
							}
						}
					}
				}
			}
		}
	}
	
}
