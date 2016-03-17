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
package org.corpus_tools.atomic.exceptions;

/**
 * The highest level exception. All other exceptions in Atomic
 * are derived form this class.
 * <p>
 * If none of the other existing exceptions map to the problem,
 * clients can instantiate this class. 
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class AtomicException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7265395820798870119L;
	
	public AtomicException() {
		super();
	}
	
	public AtomicException(String s) {
		super(s);
	}
	
	public AtomicException(String s, Throwable e) {
		super(s, e);
	}
	
}
