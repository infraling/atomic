/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.salt.SaltProjectCompiler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;

/**
 * A wizard for creating new Atomic (i.e., Salt) projects.
 * <p>
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class NewAtomicProjectWizard extends Wizard implements INewWizard {
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizard".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizard.class);
	
	private NewAtomicProjectWizardPageProjectStructure structurePage = new NewAtomicProjectWizardPageProjectStructure();
	private NewAtomicProjectWizardPageTokenization tokenizationPage = null;
	
	/*
	 * @copydoc @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench, org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addPages() {
		addPage(getStructurePage());
		setTokenizationPage(new NewAtomicProjectWizardPageTokenization(getStructurePage()));	
		addPage(getTokenizationPage());
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#canFinish()
	 */
	@Override
	public boolean canFinish() {
		// TODO Auto-generated method stub
		return (false && super.canFinish());
	}
	
	/*
	 * @copydoc @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		Corpus projectData = getStructurePage().getModel();
		// TODO: Compile SaltProject form projectData
		SaltProjectCompiler compiler = new SaltProjectCompiler(projectData);
		SaltProject project = compiler.run();
		for (SDocument document : project.getSCorpusGraphs().get(0).getSDocuments()) {
			System.err.println("DOCUMENT: " + document.getSDocumentGraph().getSTextualDSs().get(0).getSText());
		}
		return false; // TODO FIXME
	}
	
	@Override
	public String getWindowTitle() {
		return "New Atomic Project";
	}

	/**
	 * @return the structurePage
	 */
	private NewAtomicProjectWizardPageProjectStructure getStructurePage() {
		return structurePage;
	}

	/**
	 * @return the tokenizationPage
	 */
	public NewAtomicProjectWizardPageTokenization getTokenizationPage() {
		return tokenizationPage;
	}

	/**
	 * @param tokenizationPage the tokenizationPage to set
	 */
	public void setTokenizationPage(NewAtomicProjectWizardPageTokenization tokenizationPage) {
		this.tokenizationPage = tokenizationPage;
	}

}
