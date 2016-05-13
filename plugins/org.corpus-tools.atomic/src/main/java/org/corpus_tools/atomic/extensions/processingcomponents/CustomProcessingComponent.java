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

import org.corpus_tools.atomic.extensions.ProcessingComponent;
import org.corpus_tools.salt.common.SDocument;

/**
 * An abstraction of a custom processing component. Clients must work
 * directly on an instance of {@link SDocument}. This allows for maximum
 * implementation flexibility, but requires knowledge about Salt models.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
@SuppressWarnings("rawtypes")
public abstract class CustomProcessingComponent implements ProcessingComponent {

	/* 
	 * @copydoc @see org.corpus_tools.atomic.extensions.ProcessingComponent#processDocument(de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument)
	 */
	@Override
	public abstract void processDocument(SDocument document);

}
