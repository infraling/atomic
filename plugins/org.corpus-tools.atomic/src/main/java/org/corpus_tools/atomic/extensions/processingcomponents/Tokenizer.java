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
import org.corpus_tools.atomic.extensions.SaltProcessingComponent;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

/**
 * A semi-abstract tokenizer for raw corpus document source texts, working on
 * input of type {@link String} and providing output of type {@link List<String>}. This class
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
 * Clients of this class subscribe to using {@link String} as
 * their input type, and {@link List<String>} as their output
 * type.
 * <p>
 * This class is meant to be used with Salt 2.1.1.
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class Tokenizer extends SaltProcessingComponent implements ProcessingComponent<String, List<String>> {
	
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
		if (documentGraph.getSTextualDSs().size() > 0 && documentGraph.getSTextualDSs().get(0) == null) {
			throw new ProcessingException("The processed document's document graph does not have an STextualDS!", new NullPointerException());
		}
		for (STextualDS sTextualDS : documentGraph.getSTextualDSs()) {
			String documentSourceText = sTextualDS.getSText();
			List<String> stringTokens = tokenize(documentSourceText);
			
			// Create char array
			char[] charText = documentSourceText.toCharArray();
			int tokenCounter = 0;
			SToken previousToken = null;

			// Iterate char array
			for (int i = 0; i < charText.length; i++) {
				if ((stringTokens.get(tokenCounter).length() < 1) || (stringTokens.get(tokenCounter).substring(0, 1).equals(String.valueOf(charText[i])))) {
					StringBuffer pattern = new StringBuffer();

					// Iterate through length of one String token
					for (int y = 0; y < stringTokens.get(tokenCounter).length(); y++) {
						// For the length of the String token, add all chars
						pattern.append(charText[i + y]);
					}
					if (stringTokens.get(tokenCounter).hashCode() == pattern.toString().hashCode()) {
						// Pattern is exactly like current string token
						int start = i;
						int end = i + stringTokens.get(tokenCounter).length();

						SToken token = documentGraph.createSToken(sTextualDS, start, end);
						
						// Add SOrderRelation from previous to this token if previous token is not null
						if (previousToken != null) {
							SOrderRelation orderRelation = SaltFactory.eINSTANCE.createSOrderRelation();
							orderRelation.setSTarget(token);
							orderRelation.setSSource(previousToken);
							documentGraph.addSRelation(orderRelation);
						}
						previousToken = token;
					
						// Add token to layer
						SLayer layer;
						if ((layer = getTargetLayer(document)) != null && documentGraph.getSLayers().contains(layer)) {
							if (layer.getSName() == null || layer.getSName().isEmpty()) {
								layer.setSName(getTargetLayerName(document));
							}
							layer.getSNodes().add(token);
						}
						i = i + stringTokens.get(tokenCounter).length() - 1;
						tokenCounter++;
						if (tokenCounter >= stringTokens.size()) {
							break;
						}
					}
				}
			}
		}
	}
	
}
