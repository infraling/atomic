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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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
	private Text text_2;
	private Text text_3;
	private Text text_4;
	private Text text_5;
	private Text text_6;
	
	/**
	 * Default constructor calling the constructor {@link #NewAtomicProjectWizardPageProjectStructure(String)}
	 * with the default page name. 
	 */
	public NewAtomicProjectWizardPageProjectStructure() {
		super("NONE"); // FIXME
		setTitle("NÖ");
		setDescription("Dec");
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
		container.setLayout(new FillLayout());
		
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLocation(0, 0);
		
		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new GridLayout(2, false));
		
		Button btnNewRootCorpus = new Button(leftComposite, SWT.NONE);
		btnNewRootCorpus.setText("New root corpus");
		
		Button btnRemoveElement = new Button(leftComposite, SWT.NONE);
		btnRemoveElement.setText("Remove element");
		
		Tree tree = new Tree(leftComposite, SWT.BORDER);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new FillLayout());
		
		SashForm sashForm_1 = new SashForm(rightComposite, SWT.VERTICAL);
		
		Composite corpusComposite = new Composite(sashForm_1, SWT.NONE);
		corpusComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpCorpus = new Group(corpusComposite, SWT.NONE);
		grpCorpus.setText("Corpus");
		grpCorpus.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite corpusGroupComposite = new Composite(grpCorpus, SWT.NONE);
		corpusGroupComposite.setLayout(new GridLayout(3, false));
		
		Label lblName = new Label(corpusGroupComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");
		
		text_2 = new Text(corpusGroupComposite, SWT.BORDER);
		text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSave = new Button(corpusGroupComposite, SWT.NONE);
		btnSave.setText("Save");
		
		Label lblAddSubcorpus = new Label(corpusGroupComposite, SWT.NONE);
		lblAddSubcorpus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddSubcorpus.setText("Add subcorpus:");
		
		text_3 = new Text(corpusGroupComposite, SWT.BORDER);
		text_3.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnAdd = new Button(corpusGroupComposite, SWT.NONE);
		btnAdd.setText("Add");
		
		Label lblAddDocument = new Label(corpusGroupComposite, SWT.NONE);
		lblAddDocument.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddDocument.setText("Add document:");
		
		text_4 = new Text(corpusGroupComposite, SWT.BORDER);
		text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnAdd_1 = new Button(corpusGroupComposite, SWT.NONE);
		btnAdd_1.setText("Add");
		
		Composite documentComposite = new Composite(sashForm_1, SWT.NONE);
		documentComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Group grpDocument = new Group(documentComposite, SWT.NONE);
		grpDocument.setText("Document");
		grpDocument.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		Composite documentGroupComposite = new Composite(grpDocument, SWT.NONE);
		documentGroupComposite.setLayout(new GridLayout(3, false));
		
		Label lblName_1 = new Label(documentGroupComposite, SWT.NONE);
		lblName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_1.setText("Name:");
		
		text_5 = new Text(documentGroupComposite, SWT.BORDER);
		text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button btnSave_1 = new Button(documentGroupComposite, SWT.NONE);
		btnSave_1.setText("Save");
		
		Label lblSourceText = new Label(documentGroupComposite, SWT.NONE);
		lblSourceText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblSourceText.setText("Source text:");
		
		text_6 = new Text(documentGroupComposite, SWT.BORDER | SWT.MULTI);
		text_6.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		new Label(documentGroupComposite, SWT.NONE);
		
		Button btnBrowse = new Button(documentGroupComposite, SWT.NONE);
		btnBrowse.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse");
		
		Button btnSave_2 = new Button(documentGroupComposite, SWT.NONE);
		btnSave_2.setText("Save");
		
		sashForm_1.setWeights(new int[] {1, 1});
		sashForm.setWeights(new int[] {1, 1});
	}
}
