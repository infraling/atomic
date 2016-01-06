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
package org.corpus_tools.atomic.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * A dialog which lets the user pick his/her workspace. FIXME: Factor out logic, keep only GUI, externalize Strings TESTME
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class SelectWorkspaceDialog extends TitleAreaDialog {

	/**
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "SelectWorkspaceDialog".
	 */
	private static final Logger log = LogManager.getLogger(SelectWorkspaceDialog.class);

	private boolean isSwitchWorkspaceDialog;
	private String selectedWorkspaceLocationAsString;

	private Combo workspacePathCombo;

	private Button rememberWorkspaceButton;

	private ArrayList<String> lastWorkspacesList;
	private static final String ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME = "atomic-workspace";
	private static final String WORKSPACE_IDENTIFIER_FILE_NAME = ".atomic-workspace";

	/**
	 * Pre-IPreferenceStore preferences, as no access to IPreferenceStore is available at this point in the application lifecycle
	 */
	private static Preferences preferences = Preferences.userNodeForPackage(SelectWorkspaceDialog.class);
	private static final String PREF_KEY_REMEMBER_WORKSPACE = "Remember workspace?";
	private static final String PREF_KEY_WORKSPACE_ROOT = "Workspace root";
	private static final String PREF_KEY_LAST_WORKSPACES = "Last workspace in use";
	private static final String SPLIT_CHAR = "#";
	private static final String MESSAGE_DEFAULT = "Atomic stores your projects and settings in a folder called \"the workspace\".";
	private static final String MESSAGE_INFO = "Please select a workspace root directory.";
	private static final String MESSAGE_ERROR = "You must set a directory!";
	private static final int MAX_WORKSPACES_IN_HISTORY = 20;

	/**
	 * @param isSwitchWorkspaceDialog Toggle whether the dialog is used for switching workspaces during runtime
	 */
	public SelectWorkspaceDialog(boolean isSwitchWorkspaceDialog) {
		super(Display.getDefault().getActiveShell());
		this.isSwitchWorkspaceDialog = isSwitchWorkspaceDialog;
	}

	/**
	 * Configures the dialog shell with a text ("title"), depending on whether the dialog is used as a "Switch workspace" dialog or as (initial) "Select workspace" dialog.
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.window.Window#configureShell(org.eclipse.swt.widgets. Shell)
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
		return preferences.getBoolean(PREF_KEY_REMEMBER_WORKSPACE, false);
	}

	/**
	 * Returns the preference value for the last workspace that has been set by the user.
	 *
	 * @return String the preference value for last workspace or null if none
	 */
	public static String getLastWorkspace() {
		return preferences.get(PREF_KEY_WORKSPACE_ROOT, null);
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
	 * "Suggests" a workspace directory based on the user home directory (got from {@link java.lang.System#getProperty(String)}, property key "user.home", and appending {@link #ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME}.)
	 *
	 * @return String The suggested directory location
	 */
	private String suggestWorkspaceDirectory() {
		StringBuffer buffer = new StringBuffer();
		String userHome = System.getProperty("user.home");
		log.trace("Value for \"user.home\" system property is \"{}\".", userHome);
		buffer.append(userHome);
		buffer.append(File.separator);
		buffer.append(ATOMIC_WORKSPACE_DIRECTORY_DEFAULT_NAME);
		log.info("Suggesting workspace directory to be \"atomic-workspace\" in the user's home directory ({}).", userHome);
		return buffer.toString();
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle("Pick workspace");
		setMessage(MESSAGE_DEFAULT);

		createBrowseRow(composite);

		Composite rememberWSRow = new Composite(composite, SWT.NONE);
		rememberWSRow.setLayout(new RowLayout(SWT.RIGHT));
		rememberWorkspaceButton = new Button(rememberWSRow, SWT.CHECK);
		rememberWorkspaceButton.setText("Remember workspace");
		rememberWorkspaceButton.setSelection(preferences.getBoolean(PREF_KEY_REMEMBER_WORKSPACE, false));

		return composite;
	}

	/**
	 * Creates a composite with the elements needed for appointing a path for the workspace.
	 * 
	 * @param Composite composite The parent composite
	 */
	private void createBrowseRow(Composite composite) {
		Composite row = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		row.setLayout(layout);

		CLabel label = new CLabel(row, SWT.NONE);
		label.setText("Workspace path:");

		workspacePathCombo = new Combo(row, SWT.BORDER);
		String workspaceRoot = preferences.get(PREF_KEY_WORKSPACE_ROOT, "");
		if (workspaceRoot == null || workspaceRoot.length() == 0) {
			workspaceRoot = suggestWorkspaceDirectory();
		}
		workspacePathCombo.setText(workspaceRoot == null ? "" : workspaceRoot);

		String lastUsedWorkspace = preferences.get(PREF_KEY_LAST_WORKSPACES, "");
		lastWorkspacesList = new ArrayList<String>();
		if (lastUsedWorkspace != null) {
			String[] allWorkspaces = lastUsedWorkspace.split(SPLIT_CHAR);
			for (String oneOfAllWorkspace : allWorkspaces)
				lastWorkspacesList.add(oneOfAllWorkspace);
		}
		for (String lastOfAllWorkspace : lastWorkspacesList)
			workspacePathCombo.add(lastOfAllWorkspace);

		Button browseButton = new Button(row, SWT.PUSH);
		browseButton.setText("&Browse...");
		browseButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				DirectoryDialog directoryDialog = new DirectoryDialog(getParentShell());
				directoryDialog.setText("Select Workspace Root");
				directoryDialog.setMessage(MESSAGE_INFO);
				directoryDialog.setFilterPath(workspacePathCombo.getText());
				String pick = directoryDialog.open();
				if (pick == null && workspacePathCombo.getText().length() == 0) {
					setMessage(MESSAGE_ERROR, IMessageProvider.ERROR);
				}
				else {
					setMessage(MESSAGE_DEFAULT);
					if (pick != null) {
						workspacePathCombo.setText(pick);
					}
					else {
						log.trace("Variable \'pick\' is null.");
					}
				}
			}
		});
	}

	/*
	 * @see org.eclipse.jface.dialogs.Dialog#okPressed()
	 */
	@Override
	protected void okPressed() {
		String workspacePathString = workspacePathCombo.getText();

		if (workspacePathString.length() == 0) {
			setMessage(MESSAGE_ERROR, IMessageProvider.ERROR);
			return;
		}

		String workspaceCheckResult = checkWorkspaceDirectory(getParentShell(), workspacePathString, true, true);
		if (workspaceCheckResult != null) {
			setMessage(workspaceCheckResult, IMessageProvider.ERROR);
			return;
		}

		lastWorkspacesList.remove(workspacePathString);
		if (!lastWorkspacesList.contains(workspacePathString)) {
			lastWorkspacesList.add(0, workspacePathString);
		}

		if (lastWorkspacesList.size() > MAX_WORKSPACES_IN_HISTORY) {
			List<String> workspaceToBeRemovedFromList = new ArrayList<String>();
			for (int i = MAX_WORKSPACES_IN_HISTORY; i < lastWorkspacesList.size(); i++) {
				workspaceToBeRemovedFromList.add(lastWorkspacesList.get(i));
			}
			lastWorkspacesList.removeAll(workspaceToBeRemovedFromList);
		}

		// Build a list of previously used workspaces
		StringBuffer previouslyUsedWorkspaces = new StringBuffer();
		for (int i = 0; i < lastWorkspacesList.size(); i++) {
			previouslyUsedWorkspaces.append(lastWorkspacesList.get(i));
			if (i != lastWorkspacesList.size() - 1) {
				previouslyUsedWorkspaces.append(SPLIT_CHAR);
			}
		}

		// Store remember-workspace value & previous workspaces in preferences
		preferences.putBoolean(PREF_KEY_REMEMBER_WORKSPACE, rememberWorkspaceButton.getSelection());
		preferences.put(PREF_KEY_LAST_WORKSPACES, previouslyUsedWorkspaces.toString());

		// Create the workspace
		boolean workspaceCreated = checkAndCreateWorkspaceRoot(workspacePathString);
		if (!workspaceCreated) {
			setMessage("The workspace could not be created, please check the error log");
			return;
		}

		// Save the workspace path as String
		selectedWorkspaceLocationAsString = workspacePathString;
		preferences.put(PREF_KEY_WORKSPACE_ROOT, workspacePathString);
		super.okPressed();
	}

	/**
	 * Checks the workspace directory for reading/writing properties. Can be called externally to check a directory.
	 * 
	 * @param Shell parentShell The parent shell
	 * @param String workspaceLocation Directory the user selects
	 * @param boolean askForCreation Whether to ask if a directory should be created if it does not exist
	 * @param boolean calledFromWorkspaceDialog Whether this method was called from the {@link SelectWorkspaceDialog}
	 * @return String An error message or null
	 */
	public static String checkWorkspaceDirectory(Shell parentShell, String workspaceLocation, boolean askForCreation, boolean calledFromWorkspaceDialog) {
		File workspaceFile = new File(workspaceLocation + File.separator + WORKSPACE_IDENTIFIER_FILE_NAME);

		File workspaceDirectory = new File(workspaceLocation);
		if (!workspaceDirectory.exists()) {
			if (askForCreation) {
				boolean create = MessageDialog.openConfirm(parentShell, "New workspace directory", "The directory does not exist. Would you like to create it?");
				if (create) {
					try {
						log.info("Creating directories and workspace file in {}.", workspaceDirectory);
						workspaceDirectory.mkdirs();
						workspaceFile.createNewFile();
					}
					catch (Exception err) {
						return "Error creating directories, please check folder permissions!";
					}
				}

				if (!workspaceDirectory.exists()) {
					return "The selected directory does not exist!";
				}
			}
		}

		if (!workspaceDirectory.canRead()) {
			return "The selected directory is not readable!";
		}

		if (!workspaceDirectory.isDirectory()) {
			return "The selected path is not a directory!";
		}

		if (calledFromWorkspaceDialog) {
			if (!workspaceFile.exists()) {
				boolean create = MessageDialog.openConfirm(parentShell, "New workspace", "The directory '" + workspaceFile.getAbsolutePath() + "' will be transformed into a workspace. Workspace-specific files and directories will be created.\n\nWould you like to create a workspace in the selected location?");
				if (create) {
					try {
						log.info("Creating directories and workspace file in {}.", workspaceDirectory);
						workspaceDirectory.mkdirs();
						workspaceFile.createNewFile();
					}
					catch (Exception err) {
						return "Error creating directories, please check folder permissions";
					}
				}
				else {
					return "Please select a directory for your workspace";
				}

				if (!workspaceFile.exists()) {
					return "The selected directory does not exist";
				}

				return null;
			}
		}
		else {
			if (!workspaceFile.exists()) {
				return "The selected directory is not a workspace directory";
			}
		}

		return null;
	}

	/**
	 * Checks whether the directory with the passed-in String location exists. If it does not exist, the directory will be created, and the workspace file (@see {@link SelectWorkspaceDialog#WORKSPACE_IDENTIFIER_FILE_NAME}) will be created.
	 *
	 * @param workspaceDirectory The assumed workspace root directory
	 * @return boolean true if all checks passed and workspace has been successfully created, false if there was a problem
	 */
	public static boolean checkAndCreateWorkspaceRoot(String workspaceDirectory) {
		try {
			File workspaceDirectoryFile = new File(workspaceDirectory);
			if (!workspaceDirectoryFile.exists())
				return false;

			File workspaceIdentifierFile = new File(workspaceDirectory + File.separator + WORKSPACE_IDENTIFIER_FILE_NAME);
			if (!workspaceIdentifierFile.exists() && !workspaceIdentifierFile.createNewFile()) {
				return false;
			}
			return true;
		}
		catch (Exception e) {
			log.error("There was an error during checking and/or creating the Atomic workspace at given location.", e);
			return false;
		}
	}

}
