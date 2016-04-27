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

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;

import com.neovisionaries.i18n.LanguageCode;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class SaltTokenizerConfiguration implements ProcessingComponentConfiguration<SaltTokenizer> {

	private SaltTokenizer tokenizer;
	
	private LanguageCode languageCode = null;

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration#getConfiguredComponent()
	 */
	@Override
	public SaltTokenizer getConfiguredComponent() {
		return tokenizer;
	}

	/**
	 * @param tokenizer the tokenizer to set
	 */
	public void setTokenizer(SaltTokenizer tokenizer) {
		this.tokenizer = tokenizer;
	}

	/**
	 * @return the languageCode
	 */
	public LanguageCode getLanguageCode() {
		return languageCode;
	}

	/**
	 * @param languageCode the languageCode to set
	 */
	public void setLanguageCode(LanguageCode languageCode) {
		this.languageCode = languageCode;
	}

}
