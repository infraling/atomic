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
 * 		initial API and implementation: Stephan Druskat
 *******************************************************************************/
package org.corpus_tools.atomic;

import java.io.IOException;
import java.net.URL;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.workspace.SelectWorkspaceDialog;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.service.datalocation.Location;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * This class controls all aspects of the application's execution
 */
public class Atomic implements IApplication {

	/**
	 * Defines a static logger variable so that it references the
	 * {@link org.apache.logging.log4j.Logger} instance named "Atomic".
	 */
	private static final Logger log = LogManager.getLogger(Atomic.class);
	private Location instanceLocation;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	public Object start(IApplicationContext context) throws Exception {
		log.trace("Start Atomic");
		Display display = PlatformUI.createDisplay();
		instanceLocation = Platform.getInstanceLocation();
		boolean exit = checkWorkspaceSetup(display);
		if (exit) {
			return IApplication.EXIT_OK;
		}
		else {
			try {
				int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
				if (returnCode == PlatformUI.RETURN_RESTART)
					return IApplication.EXIT_RESTART;
				else
					return IApplication.EXIT_OK;
			}
			finally {
				display.dispose();
			}
		}

	}

	/**
	 * Checks the workspace setup. If the workspace has been remembered, the
	 * current instance of Atomic will use this workspace. If the workspace
	 * hasn't been remembered, a dialog will be opened for the user to pick a
	 * workspace.
	 * 
	 * @see {@link org.corpus_tools.atomic.workspace.SelectWorkspaceDialog}
	 * 
	 * @param display
	 * @return boolean Whether the application should be exited
	 */
	private boolean checkWorkspaceSetup(Display display) {
		boolean isWorkspaceRemembered = SelectWorkspaceDialog.isRememberWorkspace();
		String lastUsedWorkspace = SelectWorkspaceDialog.getLastWorkspace();
		if (isWorkspaceRemembered && (lastUsedWorkspace == null || lastUsedWorkspace.length() == 0)) {
			isWorkspaceRemembered = false;
		}
		if (isWorkspaceRemembered) {
			String ret = SelectWorkspaceDialog.checkWorkspaceDirectory(Display.getDefault().getActiveShell(), lastUsedWorkspace, false, false);
			if (ret != null) {
				isWorkspaceRemembered = false;
			}
		}
		log.trace("Remembered workspace: {}", isWorkspaceRemembered);
		if (!isWorkspaceRemembered) {
			SelectWorkspaceDialog workspaceDialog = new SelectWorkspaceDialog(false);
			int retVal = workspaceDialog.open();
			if (retVal == Window.CANCEL) {
				if (workspaceDialog.getSelectedWorkspaceLocationAsString() == null) {
					MessageDialog.openError(display.getActiveShell(), "Error", "Atomic can not start without a workspace and will now exit.");
					log.info("No workspace set, exit application.");
					return true;
				}
			}
			else {
				try {
					instanceLocation.set(new URL("file", null, workspaceDialog.getSelectedWorkspaceLocationAsString()), false);
				}
				catch (IllegalStateException | IOException e) {
					log.error("Setting the instance location didn't succeed!", e);
				}
			}
		}
		else {
			// set the last used location and continue
			try {
				instanceLocation.set(new URL("file", null, lastUsedWorkspace), false);
			}
			catch (IllegalStateException | IOException e) {
				log.error("Setting the instance location to the workspace last in use failed!", e);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning()) {
			log.trace("Workbench is not running anymore, returning from stop().");
			return;
		}
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable() {
			public void run() {
				if (!display.isDisposed())
					log.trace("Closing workbench.");
					workbench.close();
			}
		});
	}
}
