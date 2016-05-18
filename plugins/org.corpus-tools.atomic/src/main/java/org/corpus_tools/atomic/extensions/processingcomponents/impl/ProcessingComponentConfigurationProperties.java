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

import org.eclipse.osgi.util.NLS;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class ProcessingComponentConfigurationProperties extends NLS {
	private static final String BUNDLE_NAME = "org.corpus_tools.atomic.extensions.processingcomponents.impl.processing-component-configuration"; //$NON-NLS-1$
	public static String SaltTokenizerConfiguration_abbreviations;
	public static String SaltTokenizerConfiguration_languageCode;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ProcessingComponentConfigurationProperties.class);
	}

	private ProcessingComponentConfigurationProperties() {
	}
}
