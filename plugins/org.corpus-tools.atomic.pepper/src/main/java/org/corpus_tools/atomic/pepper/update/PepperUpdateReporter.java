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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Reports the result of a {@link PepperUpdateJob}.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateReporter {
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "PepperUpdateReporter".
	 */
	private static final Logger log = LogManager.getLogger(PepperUpdateReporter.class);
	
	/**
	 * 
	 */
	private IStatus result;
	/**
	 * 
	 */
	private String resultText;

	/**
	 * 
	 */
	public PepperUpdateReporter(IStatus result, String resultText) {
		setResult(result);
		setResultText(resultText);
	}

	/**
	 * @return the result
	 */
	public IStatus getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(IStatus result) {
		this.result = result;
	}

	/**
	 * @return the resultText
	 */
	public String getResultText() {
		return resultText;
	}

	/**
	 * @param resultText the resultText to set
	 */
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	/**
	 * TODO: Description
	 */
	public void report() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (getResult() == Status.CANCEL_STATUS) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Pepper update cancelled!", "The Pepper update process has been cancelled.\nThe following Pepper modules have been processed.\n" + getResultText());
				}
			});
			log.warn("Pepper update cancelled! Results:\n" + getResultText());
		}
		else if (getResult() == Status.OK_STATUS) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Pepper update finished!", "The Pepper update process has successfully finished with the following results.\n" + getResultText());
				}
			});
			log.info("Pepper update finished, results:\n" + getResultText());
		}
		else if (getResult() instanceof PepperUpdateErrorStatus) {
			display.asyncExec(new Runnable() {
				@Override
				public void run() {
					MessageDialog.openError(Display.getCurrent().getActiveShell(), "Pepper update could not finish!", "The Pepper update process did not finish successfully " + getResult().getMessage() + ".");
				}
			});
			log.error("Error in Pepper update process due to the following exception: " + getResult().getException());
		}

	}
}