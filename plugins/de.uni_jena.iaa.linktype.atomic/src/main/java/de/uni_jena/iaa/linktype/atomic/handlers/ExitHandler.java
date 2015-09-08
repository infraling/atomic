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
package de.uni_jena.iaa.linktype.atomic.handlers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * Provides an {@link #execute(ExecutionEvent)} method that closes the Atomic workbench.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class ExitHandler extends AbstractHandler {
	
	/** 
	 * Defines a static logger variable so that it references the @see{Logger} instance named "ExitHandler".
	 */
	private static final Logger log = LogManager.getLogger(ExitHandler.class);
	
	/**
	 * Closes the active Atomic workbench with the help of
	 * {@link org.eclipse.ui.handlers.HandlerUtil#getActiveWorkbenchWindow(ExecutionEvent)} and 
	 * {@link org.eclipse.ui.IWorkbenchWindow#close()}.
	 *
	 * @param event The event to be handled
	 * @return The result of the {@link IWorkbenchWindow#close()} operation.
	 * @throws ExecutionException
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		log.trace("Attempting to close the Atomic workbench.");
		boolean closeResult = HandlerUtil.getActiveWorkbenchWindow(event).close();
		if (!closeResult) {
			log.error("Attempt to close the Atomic workbench failed!");
		}
		else {
			log.trace("Attempt to close the Atomic workbench succeeded!");
		}
		return closeResult;
	}

}
