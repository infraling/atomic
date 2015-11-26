/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
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
package org.corpus_tools.atomic.pepper.update;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.progress.UIJob;

/**
 * A {@link UIJob} handling the update of Pepper modules. Internally delegates the update process to an instance of {@link PepperUpdateDelegate}.
 * <p>
 * This job is blocking the UI Thread.
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateUIJob extends UIJob {

	private PepperUpdateDelegate delegate;

	/**
	 * @param name
	 */
	public PepperUpdateUIJob(String name) {
		super(name);
		this.setDelegate(new PepperUpdateDelegate());
	}

	/* 
	 * @copydoc @see org.eclipse.ui.progress.UIJob#runInUIThread(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public IStatus runInUIThread(IProgressMonitor monitor) {
		return delegate.run(monitor);
	}

	/**
	 * @return the delegate
	 */
	public PepperUpdateDelegate getDelegate() {
		return delegate;
	}

	/**
	 * @param delegate the delegate to set
	 */
	public void setDelegate(PepperUpdateDelegate delegate) {
		this.delegate = delegate;
	}

}
