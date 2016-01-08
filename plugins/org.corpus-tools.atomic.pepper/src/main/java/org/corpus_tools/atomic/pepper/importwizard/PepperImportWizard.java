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
package org.corpus_tools.atomic.pepper.importwizard;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class PepperImportWizard implements IImportWizard {

	/**
	 * 
	 */
	public PepperImportWizard() {
		// TODO Auto-generated constructor stub
	}

	/* 
	 * @copydoc @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#addPages()
	 */
	@Override
	public void addPages() {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#createPageControls(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPageControls(Composite pageContainer) {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getContainer()
	 */
	@Override
	public IWizardContainer getContainer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getDefaultPageImage()
	 */
	@Override
	public Image getDefaultPageImage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getDialogSettings()
	 */
	@Override
	public IDialogSettings getDialogSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getPage(java.lang.String)
	 */
	@Override
	public IWizardPage getPage(String pageName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getPageCount()
	 */
	@Override
	public int getPageCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getPages()
	 */
	@Override
	public IWizardPage[] getPages() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getPreviousPage(org.eclipse.jface.wizard.IWizardPage)
	 */
	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getStartingPage()
	 */
	@Override
	public IWizardPage getStartingPage() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getTitleBarColor()
	 */
	@Override
	public RGB getTitleBarColor() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#getWindowTitle()
	 */
	@Override
	public String getWindowTitle() {
		// TODO Auto-generated method stub
		return null;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#isHelpAvailable()
	 */
	@Override
	public boolean isHelpAvailable() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#needsPreviousAndNextButtons()
	 */
	@Override
	public boolean needsPreviousAndNextButtons() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#needsProgressMonitor()
	 */
	@Override
	public boolean needsProgressMonitor() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#performCancel()
	 */
	@Override
	public boolean performCancel() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.IWizard#setContainer(org.eclipse.jface.wizard.IWizardContainer)
	 */
	@Override
	public void setContainer(IWizardContainer wizardContainer) {
		// TODO Auto-generated method stub

	}

}
