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
import org.corpus_tools.atomic.pepper.update.PepperUpdateDelegate.PepperUpdateErrorStatus;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Handles selection of menu item "Help > Update > Update Pepper" by creating and scheduling a {@link PepperUpdateJob}.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateHandler extends AbstractHandler implements IHandler {

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
	 * Listens to a {@link PepperUpdateJob} and catches the point where
	 * the job is finished, then reacts to the job's resulting status
	 * by reporting it to the user via different types of {@link MessageDialog}.
	 * <p>
	 * 
	 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
	 */
	public class PepperUpdateJobChangeAdapter extends JobChangeAdapter {

		private IStatus result;
		private String resultText;

		@Override
		public void done(IJobChangeEvent event) {
			log.info("The PepperUpdateJob {} has finished with result {}. Proceeding to report result to user.", event.getJob(), event.getResult());
			result = event.getResult();
			resultText = ((PepperUpdateJob) event.getJob()).getDelegate().getResultText();
			Display display = PlatformUI.getWorkbench().getDisplay();
			if (result == Status.CANCEL_STATUS) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Pepper update cancelled!", "The Pepper update process has been cancelled.\nThe following Pepper modules have been processed.\n" + resultText);
					}
				});
				log.warn("Pepper update cancelled! Results:\n" + resultText);
			}
			else if (result == Status.OK_STATUS) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Pepper update finished!", "The Pepper update process has successfully finished with the following results.\n" + resultText);
					}
				});
				log.info("Pepper update finished, results:\n" + resultText);
			}
			else if (result instanceof PepperUpdateErrorStatus) {
				display.asyncExec(new Runnable() {
					@Override
					public void run() {
						MessageDialog.openError(Display.getCurrent().getActiveShell(), "Pepper update could not finish!", "The Pepper update process did not finish successfully " + result.getMessage() + ".");
					}
				});
				log.error("Error in Pepper update process due to the following exception: " + result.getException());
			}

		}

	}

}
