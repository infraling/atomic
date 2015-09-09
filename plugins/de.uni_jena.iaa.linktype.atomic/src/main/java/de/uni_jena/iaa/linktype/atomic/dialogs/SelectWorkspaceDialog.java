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
package de.uni_jena.iaa.linktype.atomic.dialogs;

import java.io.File;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog which lets the user pick his/her workspace.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class SelectWorkspaceDialog extends TitleAreaDialog {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "SelectWorkspaceDialog".
	 */
	private static final Logger log = LogManager.getLogger(SelectWorkspaceDialog.class);
	
	private boolean isSwitchWorkspaceDialog;
	private String selectedWorkspaceLocationAsString;
	private static final String ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME = "atomic-workspace";
	private static final String WORKSPACE_IDENTIFIER_FILE_NAME = ".atomic-workspace";
	
	/**
	 * Pre-IPreferenceStore preferences
	 */
	private static Preferences preferences = Preferences.userNodeForPackage(SelectWorkspaceDialog.class);
	private static final String keyRememberWorkspace = "Remember workspace?";
	private static final String keyWorkspaceRoot = "Last workspace set";

	/**
	 * @param isSwitchWorkspaceDialog Toggle whether the dialog is used for switching workspaces during runtime
	 */
	public SelectWorkspaceDialog(boolean isSwitchWorkspaceDialog) {
		super(Display.getDefault().getActiveShell());
		this.isSwitchWorkspaceDialog = isSwitchWorkspaceDialog;
	}
	
	/**
	 * Configures the dialog shell with a text ("title"), depending on whether the dialog is used as a
	 * "Switch workspace" dialog or as (initial) "Select workspace" dialog.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets.Shell)
	 */
	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(isSwitchWorkspaceDialog ? "Switch workspace" : "Select workspace");
	}
	
	/**
	 * Returns the preference value for whether the workspace setting should be remembered.
	 *
	 * @return boolean Boolean value of preference value for remember workspace
	 */
	public static boolean isRememberWorkspace() {
		return preferences.getBoolean(keyRememberWorkspace, false);
	}
	
	/**
	 * Returns the preference value for the last workspace that has been set by the user.
	 *
	 * @return String the preference value for last workspace or null if none
	 */
	public static String getLastWorkspace() {
		return preferences.get(keyWorkspaceRoot, null);
	}
	
	/**
	 * Returns the workspace location the user has selected in the dialog as String.
	 *
	 * @return String The selected workspace location
	 */
	public String getSelectedWorkspaceLocationAsString() {
		return selectedWorkspaceLocationAsString;
	}
	
	/**
	 * "Suggests" a workspace directory based on the user home directory (got from {@link java.lang.System#getProperty(String)},
	 * property key "user.home", and appending {@link #ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME}.)
	 *
	 * @return String The suggested directory location
	 */
	private String suggestWorkspaceDirectory() {
		StringBuffer buffer = new StringBuffer();
		String userHome = System.getProperty("user.home");
		log.trace("Value for \"user.home\" system property is \"%s\".", userHome);
		buffer.append(userHome);
		buffer.append(File.pathSeparator);
		buffer.append(ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME);
		log.info("Suggesting workspace directory to be \"atomic-workspace\" in the user's home directory (%s).", userHome);
		return buffer.toString();
	}
	
}
