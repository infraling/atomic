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
 *******************************************************************************/
package org.corpus_tools.atomic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;
import org.osgi.framework.FrameworkUtil;

/**
 * Provides setup machanisms for the Atomic workbench. @see {@link WorkbenchWindowAdvisor}.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "ApplicationWorkbenchWindowAdvisor".
	 */
	private static final Logger log = LogManager.getLogger(ApplicationWorkbenchWindowAdvisor.class);
	
	/**
	 * @param configurer The configurer object for the workbench window
	 */
	public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
		super(configurer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#createActionBarAdvisor(org.eclipse.ui.application.IActionBarConfigurer)
	 */
	@Override
	public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.application.WorkbenchWindowAdvisor#preWindowOpen()
	 */
	@Override
	public void preWindowOpen() {
		// Configure the main window
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		configurer.setInitialSize(new Point(400, 300));
		configurer.setShowCoolBar(true);
		configurer.setShowStatusLine(true);
		configurer.setShowProgressIndicator(true);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		String workspaceDirectory = workspace.getRoot().getLocation().toFile().getAbsoluteFile().getAbsolutePath();
		configurer.setTitle("Atomic " + FrameworkUtil.getBundle(this.getClass()).getVersion() + " - " + workspaceDirectory);

		// Get a reference to the preferences store
		IPreferenceStore prefStore = PlatformUI.getPreferenceStore();

		// Make sure the needed perspective buttons are shown for quick access.
		prefStore.setValue(IWorkbenchPreferenceConstants.PERSPECTIVE_BAR_EXTRAS, "org.corpus_tools.atomic.ui.navigation");

		configurer.setShowPerspectiveBar(true);
	}

	@Override
	public void postWindowOpen() {
		log.trace("Hiding basic generic wizards (New Project, Import, Export, etc.).");
		// Hide basic wizards, e.g., New Project, etc.
		// FIXME: Try to solve without internal classes!
		AbstractExtensionWizardRegistry wizardRegistry = (AbstractExtensionWizardRegistry) PlatformUI.getWorkbench().getNewWizardRegistry();
		IWizardCategory[] categories = PlatformUI.getWorkbench().getNewWizardRegistry().getRootCategory().getCategories();
		for (IWizardDescriptor wizard : getAllWizards(categories)) {
			if (wizard.getCategory().getId().matches("org.eclipse.ui.Basic")) {
				WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
				wizardRegistry.removeExtension(wizardElement.getConfigurationElement().getDeclaringExtension(), new Object[] { wizardElement });
			}
		}

		log.trace("Maximizing application window");
		getWindowConfigurer().getWindow().getShell().setMaximized(true);
	}

	private IWizardDescriptor[] getAllWizards(IWizardCategory[] categories) {
		List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
		for (IWizardCategory wizardCategory : categories) {
			results.addAll(Arrays.asList(wizardCategory.getWizards()));
			results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
		}
		return results.toArray(new IWizardDescriptor[0]);
	}

}
