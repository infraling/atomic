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
package org.corpus_tools.atomic.extensions;

import org.corpus_tools.atomic.exceptions.AtomicException;

/**
 * An exception that is thrown when something in the 
 * processing phase of a {@link ProcessingComponent}
 * goes wrong.
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class ProcessingException extends AtomicException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1156757477745165635L;

	/**
	 * 
	 */
	public ProcessingException() {
		super();
	}
	
	public ProcessingException(String s) {
		super(s);
	}
	
	public ProcessingException(String s, Throwable e) {
		super(s, e);
	}
}
