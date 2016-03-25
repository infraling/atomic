/*******************************************************************************
 * Copyright 2016 Stephan Druskat
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
package org.corpus_tools.atomic.projects.wizard;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Description
 *
 * <p>@author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewAtomicProjectWizardPageProcessingPipeline extends WizardPage {
	
	private NewAtomicProjectWizardPageProjectStructure projectStructurePage;

	/**
	 * @param projectStructurePage 
	 * 
	 */
	public NewAtomicProjectWizardPageProcessingPipeline(NewAtomicProjectWizardPageProjectStructure projectStructurePage) {
		super("Pre-process the corpus");
		this.setProjectStructurePage(projectStructurePage);
		setTitle("Pre-process the corpus");
		setDescription("Pick the pre-processing steps you want to perform on the corpus.");
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, true));
		
		Composite top = new Composite(container, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		top.setLayout(new FillLayout());
		
		IConfigurationElement[] tokenizers = Platform.getExtensionRegistry().getConfigurationElementsFor("org.corpus_tools.atomic.processingComponents.tokenizers");
		
		CTabFolder tabfolder = new CTabFolder(top, SWT.NONE);
		tabfolder.setSimple(true);
		tabfolder.setBorderVisible(true);
//		tabfolder.set
		CTabItem tokenizerTab = new CTabItem(tabfolder, SWT.NONE);
		tokenizerTab.setText("Tokenizers");
		Composite tokenizerControl = new Composite(tabfolder, SWT.NONE);
		tokenizerControl.setLayout(new RowLayout());
		for (IConfigurationElement tokenizer : tokenizers) {
			Button button = new Button(tokenizerControl, SWT.NONE);
			button.setText(tokenizer.getAttribute("name"));
		}
		tokenizerTab.setControl(tokenizerControl);
		
		CTabItem placeholder1 = new CTabItem(tabfolder, SWT.NONE);
		placeholder1.setText("(Segmenters)");
		CTabItem placeholder2 = new CTabItem(tabfolder, SWT.NONE);
		placeholder2.setText("Placeholders");
		CTabItem placeholder3 = new CTabItem(tabfolder, SWT.NONE);
		placeholder3.setText("More placeholders");
		
		Composite bottom = new Composite(container, SWT.NONE);
		bottom.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		bottom.setLayout(new FillLayout());
		Label label2 = new Label(bottom, SWT.NONE);
		label2.setText("Nothing to see here either!");
		
		setControl(container);
	}

	/**
	 * @return the projectStructurePage
	 */
	private NewAtomicProjectWizardPageProjectStructure getProjectStructurePage() {
		return projectStructurePage;
	}

	/**
	 * @param projectStructurePage the projectStructurePage to set
	 */
	private void setProjectStructurePage(NewAtomicProjectWizardPageProjectStructure projectStructurePage) {
		this.projectStructurePage = projectStructurePage;
	}

}
