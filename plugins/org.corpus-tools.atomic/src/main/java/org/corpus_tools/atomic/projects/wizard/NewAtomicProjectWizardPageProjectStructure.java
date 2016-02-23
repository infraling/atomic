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

import org.corpus_tools.atomic.internal.projects.DefaultProjectData; 
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;

/**
 * A wizard page for the user to construct the structure of a
 * project.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class NewAtomicProjectWizardPageProjectStructure extends WizardPage {
	private Text corpusNameText;
	private Text addSubCorpusNameText;
	private Text addDocumentNameText;
	private Text documentNameText;
	private Text sourceTextText;
	private DefaultProjectData model;
	private Text txtNewProject;
	
	/**
	 * Default constructor calling the constructor {@link #NewAtomicProjectWizardPageProjectStructure(String)}
	 * with the default page name. 
	 */
	public NewAtomicProjectWizardPageProjectStructure() {
		super("Create the project structure");
		setModel(new DefaultProjectData("New project"));
		setTitle("Create the project structure");
		setDescription("Create the structure of the new project by adding corpora, subcorpora, and documents.");
		/* 
		 * FIXME TODO: Add context-sensitive help to Atomic, the the "?" button will show in the wizard. Add the
		 * following description to a help "window" of sorts:
		 * Every corpus must have a name and can contain n (sub-) corpora and n documents. Every document must have a name and must contain one source text.
		 * Must include Eclipse Help plugin for this.
		 */
	}

	/* 
	 * @copydoc @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		// Calculate and set good size and position for dialog
		Monitor[] monitors = getShell().getDisplay().getMonitors();
		Monitor activeMonitor = null;

		Rectangle r = getShell().getBounds();
		for (int i = 0; i < monitors.length; i++) {
			if (monitors[i].getBounds().intersects(r)) {
				activeMonitor = monitors[i];
			}
		}
		Rectangle bounds = activeMonitor.getClientArea();
		int boundsWidth = bounds.width;
		int boundsHeight = bounds.height;
		Point size = getShell().computeSize((int) (boundsWidth * (80.0f / 100.0f)), (int) (boundsHeight * (80.0f / 100.0f)));
		
		int x = bounds.x + ((bounds.width - size.x) / 2);
	    getShell().setSize(size);
	    getShell().setLocation(x, 0);
	    
	    // Create controls
		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);
		
		// Project composite
		Group projectGroup = new Group(container, SWT.NONE);
		projectGroup.setText("Project");
		projectGroup.setLayout(new GridLayout(2, false));
		GridData gridDataProjectGroup = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		projectGroup.setLayoutData(gridDataProjectGroup);
		
		Label lblName_2 = new Label(projectGroup, SWT.NONE);
		lblName_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_2.setText("Name:");
		
		txtNewProject = new Text(projectGroup, SWT.BORDER);
		txtNewProject.setText("New project");
		txtNewProject.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLocation(0, 0);
		GridData gridDataSashForm = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridDataSashForm.horizontalAlignment = SWT.FILL;
		gridDataSashForm.verticalAlignment = SWT.FILL;
		sashForm.setLayoutData(gridDataSashForm);
		
		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new GridLayout(2, false));
		
		Button btnNewCorpus = new Button(leftComposite, SWT.NONE);
		btnNewCorpus.setText("New corpus");
		
		Button btnRemoveElement = new Button(leftComposite, SWT.NONE);
		btnRemoveElement.setText("Remove element");
		
		Tree projectStructureTree = new Tree(leftComposite, SWT.BORDER);
		projectStructureTree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new GridLayout(1, false));
		
		Group grpCorpus = new Group(rightComposite, SWT.NONE);
		grpCorpus.setText("Corpus");
		GridData grpCorpusGridData = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		grpCorpus.setLayoutData(grpCorpusGridData);
		grpCorpus.setLayout(new GridLayout(3, false));
		
		Label lblName = new Label(grpCorpus, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		corpusNameText = new Text(grpCorpus, SWT.BORDER);
		corpusNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button saveCorpusNameBtn = new Button(grpCorpus, SWT.NONE);
		saveCorpusNameBtn.setText("Save");
		
		Label lblAddSubcorpus = new Label(grpCorpus, SWT.NONE);
		lblAddSubcorpus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddSubcorpus.setText("Add subcorpus:");
		
		addSubCorpusNameText = new Text(grpCorpus, SWT.BORDER);
		addSubCorpusNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button addSubCorpusBtn = new Button(grpCorpus, SWT.NONE);
		addSubCorpusBtn.setText("Add");
		
		Label lblAddDocument = new Label(grpCorpus, SWT.NONE);
		lblAddDocument.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddDocument.setText("Add document:");
		
		addDocumentNameText = new Text(grpCorpus, SWT.BORDER);
		addDocumentNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button addDocumentBtn = new Button(grpCorpus, SWT.NONE);
		addDocumentBtn.setText("Add");
		
		Group grpDocument = new Group(rightComposite, SWT.NONE);
		GridData grpDocumentGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		grpDocument.setLayoutData(grpDocumentGridData);
		grpDocument.setLayout(new GridLayout(3, false));
		grpDocument.setText("Document");

		Label lblName_1 = new Label(grpDocument, SWT.NONE);
		lblName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_1.setText("Name:");
		
		documentNameText = new Text(grpDocument, SWT.BORDER);
		documentNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		
		Button saveDocumentNameBtn = new Button(grpDocument, SWT.NONE);
		saveDocumentNameBtn.setText("Save");
		
		Label lblSourceText = new Label(grpDocument, SWT.NONE);
		lblSourceText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblSourceText.setText("Source text:");
		
		sourceTextText = new Text(grpDocument, SWT.BORDER | SWT.MULTI);
		sourceTextText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(grpDocument, SWT.NONE);
		
		Button browseSourceTextBtn = new Button(grpDocument, SWT.NONE);
		browseSourceTextBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browseSourceTextBtn.setText("Browse");
		
		Button saveSourceTextBtn = new Button(grpDocument, SWT.NONE);
		saveSourceTextBtn.setText("Save");

		sashForm.setWeights(new int[] {1, 1});
	}
	
	@Override
	public void performHelp() 
	{
	    Shell shell = new Shell(getShell());
	    shell.setText("My Custom Help !!");
	    shell.setLayout(new GridLayout());
	    shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    Browser browser = new Browser(shell, SWT.NONE);
	    browser.setUrl("http://stackoverflow.com/questions/7322489/cant-put-content-behind-swt-wizard-help-button");
	    browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

	    shell.open();
	}

	/**
	 * @return the model
	 */
	public DefaultProjectData getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(DefaultProjectData model) {
		this.model = model;
	}
}
