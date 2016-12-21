/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena,
 * Humboldt-Universität zu Berlin, INRIA
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
 *     Martin Klotz - nested class {@link ModuleTableReader} initial API
 *     					and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.projects.pepper.update;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.Job;

/**
 * A {@link Job} handling the update of Pepper modules. Internally delegates the update process to an instance of {@link PepperUpdateDelegate}.
 * <p>
 * This job is not blocking the UI Thread.
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateJob extends Job {

	private PepperUpdateDelegate delegate;

	/**
	 * Constructor creating a new {@link PepperUpdateDelegate} object.
	 * 
	 * @param name
	 */
	public PepperUpdateJob(String name) {
		super(name);
		this.setDelegate(new PepperUpdateDelegate());
	}

	/**
	 * Delegates the update logic to the run method of {@link PepperUpdateJob#delegate}.
	 * 
	 * @copydoc @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		return getDelegate().run(monitor);
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
