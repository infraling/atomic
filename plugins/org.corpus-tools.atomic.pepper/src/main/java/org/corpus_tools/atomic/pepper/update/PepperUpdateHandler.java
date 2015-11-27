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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Handles selection of menu item "Help > Update > Update Pepper" by creating and scheduling a {@link PepperUpdateJob}.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateHandler extends AbstractHandler implements IHandler {
	
	/*
	 * TODO FIXME: Include boolean for sync/async, implement switch/case with Job call and tjis call:
	 * 		try {
			PepperUpdateRunnableWithProgress pepperUpdateRunnable = new PepperUpdateRunnableWithProgress();
			PlatformUI.getWorkbench().getProgressService().busyCursorWhile(pepperUpdateRunnable);
		}
		catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	 */

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "PepperUpdateHandler".
	 */
	private static final Logger log = LogManager.getLogger(PepperUpdateHandler.class);

	/**
	 * Executes the command: Creates and schedules a {@link PepperUpdateJob}.
	 * 
	 * @copydoc @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log.info("Starting Pepper update process, called from menu.");
		PepperUpdateJob job = new PepperUpdateJob("Pepper Update running ...");
		job.addJobChangeListener(new PepperUpdateJobChangeAdapter());
		job.schedule();
		return null;
	}

	/**
	 * Listens to a {@link PepperUpdateJob} and catches the point where the job is finished, then reacts to the job's resulting status by reporting it to the user via different types of {@link MessageDialog}.
	 * <p>
	 * 
	 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
	 */
	public class PepperUpdateJobChangeAdapter extends JobChangeAdapter {

		@Override
		public void done(IJobChangeEvent event) {
			log.info("The PepperUpdateJob {} has finished with result {}. Proceeding to report result to user.", event.getJob(), event.getResult());
			new PepperUpdateReporter(event.getResult(), ((PepperUpdateJob) event.getJob()).getDelegate().getResultText()).report();
			event.getJob().removeJobChangeListener(this);
		}
	}

}
