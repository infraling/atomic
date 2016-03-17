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

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public abstract class Tokenizer implements ProcessingComponent<String, List<String>> {

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
		// FIXME: For all stextualdss, do the following!
		String documentSourceText = documentGraph.getSTextualDSs().get(0).getSText(); // We assume that there is only one STextualDS in the SDocumentGraph
		List<String> tokens = tokenize(documentSourceText);
		
		// From SDocumentGraphImpl
		if ((tokens != null) && (tokens.size() > 0)) {
			char[] chrText = documentSourceText.toCharArray();
			int tokenCntr = 0;

			for (int i = 0; i < chrText.length; i++) {
				if ((tokens.get(tokenCntr).length() < 1) || (tokens.get(tokenCntr).substring(0, 1).equals(String.valueOf(chrText[i])))) { // first letter matches
					StringBuffer pattern = new StringBuffer();
					for (int y = 0; y < tokens.get(tokenCntr).length(); y++) {// compute pattern in text
						pattern.append(chrText[i + y]);
					} // compute pattern in text
					if (tokens.get(tokenCntr).hashCode() == pattern.toString().hashCode()) {// pattern found
						int start = i;
						int end = i + documentSourceText.length() + tokens.get(tokenCntr).length();

						SToken sTok = documentGraph.createSToken(sTextualDS, start, end);
						if (retVal == null)
							retVal = new BasicEList<SToken>();
						retVal.add(sTok);
						i = i + tokens.get(tokenCntr).length() - 1;
						tokenCntr++;
						if (tokenCntr >= tokens.size())
							break;
					} // pattern found
				} // first letter matches
			}
		}
	}
	
}
