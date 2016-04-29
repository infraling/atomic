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

import java.util.List;

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer;

/**
 * Wraps the tokenizer included in Salt ({@link de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer})
 * in a processing component of type {@link Tokenizer}. 
 * Only works with the simplest of tokenize methods without 
 * arguments, i.e., checks against the included default 
 * abbreviation sets for English, German, Italian and French.
 * <p>
 * A customizable version of this tokenizer is implemented in {@link org.corpus_tools.atomic.extensions.processingcomponents.impl.SaltTokenizer}
 * This class is meant to be used with Salt version 2.1.1.
 * 
 * <p>
 * @see <a href="https://github.com/korpling/salt/releases/tag/salt-2.1.1">Salt version 2.1.1</a>
 * @see <a href="http://corpus-tools.org/salt">http://corpus-tools.org/salt</a>
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SimpleSaltTokenizer extends Tokenizer {

	public static final String UID = "de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer.simple";

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.processingcomponents.Tokenizer#tokenize(java.lang.String)
	 */
	@Override
	public List<String> tokenize(String rawSourceText) {
		de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer saltTokenizer = new de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer();
		return saltTokenizer.tokenizeToString(rawSourceText, null);
	}
	
	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponent#getConfiguration()
	 */
	@Override
	public ProcessingComponentConfiguration<SimpleSaltTokenizer> getConfiguration() {
		return null;
	}

}
