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

/**
 * Handles selection of menu item "Help > Update > Update Pepper" by
 * creating and scheduling a {@link PepperUpdateJob}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
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
		PepperUpdateJob job = new PepperUpdateJob("org.corpus_tools.atomic.pepper.update.PepperUpdateHandler.PepperUpdateJob");
		job.schedule();
		return null;
	}

}
