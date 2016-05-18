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

import org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration;
import org.corpus_tools.atomic.models.AbstractBean;

import com.neovisionaries.i18n.LanguageCode;

/**
 * A configuration bean for {@link SaltTokenizer}s.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 */
public class SaltTokenizerConfiguration extends AbstractBean implements ProcessingComponentConfiguration<SaltTokenizer> {

	private SaltTokenizer tokenizer = null;

	private LanguageCode languageCode = null;

	private HashSet<String> abbreviations = null;
	
	/**
	 * No-args constructor (for Java bean compliance). 
	 */
	public SaltTokenizerConfiguration() {}

	/*
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration#getConfiguredComponent()
	 */
	@Override
	public SaltTokenizer getConfiguredComponent() {
		return tokenizer;
	}

	/*
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponentConfiguration#setConfiguredComponent(java.lang.Object)
	 */
	@Override
	public void setConfiguredComponent(SaltTokenizer component) {
		this.tokenizer = component;
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
		final LanguageCode oldLanguageCode = this.languageCode;
		this.languageCode = languageCode;
		firePropertyChange(ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_languageCode, oldLanguageCode, this.languageCode);
	}

	/**
	 * @return the abbreviations
	 */
	public HashSet<String> getAbbreviations() {
		return abbreviations;
	}

	/**
	 * @param abbreviations the abbreviations to set
	 */
	public void setAbbreviations(HashSet<String> abbreviations) {
		final HashSet<String> oldAbbreviations = this.abbreviations;
		this.abbreviations = abbreviations;
		firePropertyChange(ProcessingComponentConfigurationProperties.SaltTokenizerConfiguration_abbreviations, oldAbbreviations, this.abbreviations);
	}

}
