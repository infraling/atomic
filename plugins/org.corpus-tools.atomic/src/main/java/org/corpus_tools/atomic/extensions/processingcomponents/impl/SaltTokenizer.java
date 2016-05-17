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
package org.corpus_tools.atomic.extensions.processingcomponents.impl;

import java.util.HashSet;
import java.util.List;

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;

/**
 * Wraps the tokenizer included in Salt ({@link de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer})
 * in a processing component of type {@link Tokenizer}. 
 * Provides configuration for abbreviation sets.
 * <p>
 * A simple version of this tokenizer is implemented in {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer}
 * This class is meant to be used with Salt version 2.1.1.
 * 
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 * 
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltTokenizer extends Tokenizer {
	
	public static final String UID = "de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer";
	
	private SaltTokenizerConfiguration configuration = null;

	/**
	 * 
	 */
	public SaltTokenizer() {
		// TODO Auto-generated constructor stub
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#tokenize(java.lang.String)
	 */
	@Override
	public List<String> tokenize(String rawSourceText) {
		/** Not implemented because the wrapped tokenizer works on instances of the Salt model itself per default. */
		return null;
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#processDocument(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)
	 */
	@Override
	public void processDocument(SDocument document) {
		SDocumentGraph graph = document.getDocumentGraph();
		if (getConfiguration() != null) {
			org.corpus_tools.salt.common.tokenizer.Tokenizer tokenizer = new org.corpus_tools.salt.common.tokenizer.Tokenizer();
			tokenizer.setsDocumentGraph(document.getDocumentGraph());
			if (graph.getTextualDSs().size() > 0) {
				for (STextualDS sTextualDS : graph.getTextualDSs()) {
					if (sTextualDS != null) {
						// FIXME TODO: Add check for options in config
						// TODO: Add clitics as soon as they are available in a Salt release
						if (getConfiguration() instanceof SaltTokenizerConfiguration) {
							SaltTokenizerConfiguration config = (SaltTokenizerConfiguration) getConfiguration();
							HashSet<String> abbreviations;
							if ((abbreviations = config.getAbbreviations()) != null && !abbreviations.isEmpty()) {
								tokenizer.addAbbreviation(config.getLanguageCode(), abbreviations);
							}
							// TODO tokenizer.addClitics(config.getLanguageCode(), config.getClitics());
							if (config.getLanguageCode() != null) {
								tokenizer.tokenize(sTextualDS, config.getLanguageCode());
							}
							else {
								document.getDocumentGraph().tokenize();
							}
						}
					}
				}
			}
		}
		else {
			document.getDocumentGraph().tokenize();
		}
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponent#getConfiguration()
	 */
	@Override
	public ProcessingComponentConfiguration<SaltTokenizer> getConfiguration() {
		return configuration;
	}

	/**
	 * @param configuration the configuration to set
	 */
	public void setConfiguration(SaltTokenizerConfiguration configuration) {
		this.configuration = configuration;
	}
}
