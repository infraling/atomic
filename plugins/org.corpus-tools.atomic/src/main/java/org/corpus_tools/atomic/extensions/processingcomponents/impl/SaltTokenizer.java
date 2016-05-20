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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.exceptions.AtomicException;
import org.corpus_tools.atomic.extensions.ConfigurableProcessingComponent;
import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;
import com.neovisionaries.i18n.LanguageCode;

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
public class SaltTokenizer extends Tokenizer implements ConfigurableProcessingComponent {
	
	public static final String UID = "de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer";
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "SaltTokenizer".
	 */
	private static final Logger log = LogManager.getLogger(SaltTokenizer.class);
	
	private SaltTokenizerConfiguration configuration = null;

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#tokenize(java.lang.String)
	 */
	@Override
	public List<String> tokenize(String rawSourceText) {
		org.corpus_tools.salt.common.tokenizer.Tokenizer saltTokenizer = new org.corpus_tools.salt.common.tokenizer.Tokenizer();
		ProcessingComponentConfiguration<SaltTokenizer> config;
		SaltTokenizerConfiguration saltConfig = null;
		if ((config = getConfiguration()) != null && config instanceof SaltTokenizerConfiguration) {
			saltConfig = (SaltTokenizerConfiguration) config;
			HashSet<String> abbreviations = saltConfig.getAbbreviations();
			LanguageCode languageCode = saltConfig.getLanguageCode();
			if (abbreviations != null && !abbreviations.isEmpty()) {
				saltTokenizer.addAbbreviation(saltConfig.getLanguageCode(), abbreviations);
			}
			return saltTokenizer.tokenizeToString(rawSourceText, languageCode);
		}
		else {
			return null;
		}
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponent#getConfiguration()
	 */
	@Override
	public ProcessingComponentConfiguration<SaltTokenizer> getConfiguration() {
		return configuration;
	}

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ConfigurableProcessingComponent#setConfiguration(org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration)
	 */
	@Override
	public void setConfiguration(ProcessingComponentConfiguration<?> configuration) {
		if (configuration instanceof SaltTokenizerConfiguration) {
			this.configuration = (SaltTokenizerConfiguration) configuration;
			return;
		}
		log.error("Configuration for {} is not of type {}!", this.getClass().getSimpleName(), SaltTokenizerConfiguration.class, new AtomicException());
	}
}
