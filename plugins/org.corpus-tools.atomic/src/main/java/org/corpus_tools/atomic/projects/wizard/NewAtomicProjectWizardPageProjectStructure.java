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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Group;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.property.list.IListProperty;
import org.eclipse.core.databinding.property.list.MultiListProperty;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.ViewerSupport;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;

/**
 * A wizard page for the user to construct the structure of a project.
 * <p>
 * FIXME: SWTBot test this class!
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class NewAtomicProjectWizardPageProjectStructure extends WizardPage {
	private DataBindingContext bindingContext;
	private Text corpusNameText;
	private Text addSubCorpusNameText;
	private Text addDocumentNameText;
	private Text documentNameText;
	private Text sourceTextText;
	private Corpus model = createNewProject();
	private Text projectNameText;
	private Set<Control> corpusConstrols = new HashSet<>(), documentControls = new HashSet<>();
	private TreeViewer projectTreeViewer;
	private Button btnRemoveElement;

	/**
	 * Default constructor calling the constructor {@link #NewAtomicProjectWizardPageProjectStructure(String)} with the default page name.
	 */
	public NewAtomicProjectWizardPageProjectStructure() {
		super("Create the project structure");
		setTitle("Create the project structure");
		setDescription("Create the structure of the new project by adding corpora, subcorpora, and documents.");
		/*
		 * FIXME TODO: Add context-sensitive help to Atomic, the the "?" button will show in the wizard. Add the following description to a help "window" of sorts: Every corpus must have a name and can contain n (sub-) corpora and n
		 * documents. Every document must have a name and must contain one source text. Must include Eclipse Help plugin for this.
		 */
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private Corpus createNewProject() {
		Corpus project = new Corpus();
		project.setName("Project");
		Corpus root = new Corpus();
		root.setName("Root corpus");
		project.addChild(root);
		return project;
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

		// Project name
		Group projectGroup = new Group(container, SWT.NONE);
		projectGroup.setText("Project");
		projectGroup.setLayout(new GridLayout(2, false));
		projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblName_2 = new Label(projectGroup, SWT.NONE);
		lblName_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_2.setText("Name:");

		projectNameText = new Text(projectGroup, SWT.BORDER);
		projectNameText.setText("New project");
		projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		// Project contents
		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLocation(0, 0);
		GridData gridDataSashForm = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridDataSashForm.horizontalAlignment = SWT.FILL;
		gridDataSashForm.verticalAlignment = SWT.FILL;
		sashForm.setLayoutData(gridDataSashForm);

		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new GridLayout(2, false));

		final Button btnNewCorpus = new Button(leftComposite, SWT.NONE);
		btnNewCorpus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<ProjectNode> corpusList = getModel().getChildren();
				String numberOfExistingRootCorpora = (getModel().getChildren().size() > 0) ? " " + String.valueOf(getModel().getChildren().size() + 1) : "";
				Corpus newRootCorpus = new Corpus();
				newRootCorpus.setName("Root corpus" + numberOfExistingRootCorpora);
				corpusList.add(newRootCorpus);
				getModel().setChildren(corpusList);
				corpusNameText.selectAll();
				corpusNameText.setFocus();
				projectTreeViewer.refresh();
				projectTreeViewer.setSelection(new StructuredSelection(newRootCorpus));
			}
		});
		btnNewCorpus.setText("New root corpus");
		
		btnRemoveElement = new Button(leftComposite, SWT.NONE);
		btnRemoveElement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem selectedItem = projectTreeViewer.getTree().getSelection()[0];
				TreeItem parentItem = selectedItem.getParentItem();
				Corpus parent;
				int index;
				if (parentItem == null) {
					parent = getModel();
					index = projectTreeViewer.getTree().indexOf(selectedItem);
				}
				else {
					parent = (Corpus) parentItem.getData();
					index = parentItem.indexOf(selectedItem);
				}

				List<ProjectNode> list = new ArrayList<ProjectNode>(parent.getChildren());
				list.remove(index);
				parent.setChildren(list);
			}
		});
		btnRemoveElement.setText("Remove element");

		projectTreeViewer = new TreeViewer(leftComposite, SWT.SINGLE);
		new Label(leftComposite, SWT.NONE);
		projectTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		projectTreeViewer.expandAll();

		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new GridLayout(1, false));

		Group grpCorpus = new Group(rightComposite, SWT.NONE);
		grpCorpus.setText("Corpus");
		grpCorpus.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		grpCorpus.setLayout(new GridLayout(3, false));

		Label lblName = new Label(grpCorpus, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Name:");

		corpusNameText = new Text(grpCorpus, SWT.BORDER);
		corpusNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getCorpusControls().add(corpusNameText);

		Button saveCorpusNameBtn = new Button(grpCorpus, SWT.NONE);
		saveCorpusNameBtn.setText("Save");
		getCorpusControls().add(saveCorpusNameBtn);
		saveCorpusNameBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
//				ProjectNode selectionParent = getCurrentSelectionParent();
			}
		});

		Label lblAddSubcorpus = new Label(grpCorpus, SWT.NONE);
		lblAddSubcorpus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddSubcorpus.setText("Add subcorpus:");

		addSubCorpusNameText = new Text(grpCorpus, SWT.BORDER);
		addSubCorpusNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getCorpusControls().add(addSubCorpusNameText);

		Button addSubCorpusBtn = new Button(grpCorpus, SWT.NONE);
		addSubCorpusBtn.setText("Add");
		getCorpusControls().add(addSubCorpusBtn);
		addSubCorpusBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		Label lblAddDocument = new Label(grpCorpus, SWT.NONE);
		lblAddDocument.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblAddDocument.setText("Add document:");

		addDocumentNameText = new Text(grpCorpus, SWT.BORDER);
		addDocumentNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getCorpusControls().add(addDocumentNameText);

		Button addDocumentBtn = new Button(grpCorpus, SWT.NONE);
		addDocumentBtn.setText("Add");
		getCorpusControls().add(addDocumentBtn);
		addDocumentBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		Group grpDocument = new Group(rightComposite, SWT.NONE);
		grpDocument.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpDocument.setLayout(new GridLayout(3, false));
		grpDocument.setText("Document");

		Label lblName_1 = new Label(grpDocument, SWT.NONE);
		lblName_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_1.setText("Name:");

		documentNameText = new Text(grpDocument, SWT.BORDER);
		documentNameText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		getDocumentControls().add(documentNameText);

		Button saveDocumentNameBtn = new Button(grpDocument, SWT.NONE);
		saveDocumentNameBtn.setText("Save");
		getDocumentControls().add(saveDocumentNameBtn);
		saveDocumentNameBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		Label lblSourceText = new Label(grpDocument, SWT.NONE);
		lblSourceText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblSourceText.setText("Source text:");

		sourceTextText = new Text(grpDocument, SWT.BORDER | SWT.MULTI);
		sourceTextText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		getDocumentControls().add(sourceTextText);
		new Label(grpDocument, SWT.NONE);

		Button browseSourceTextBtn = new Button(grpDocument, SWT.NONE);
		browseSourceTextBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browseSourceTextBtn.setText("Browse");
		getDocumentControls().add(browseSourceTextBtn);

		Button saveSourceTextBtn = new Button(grpDocument, SWT.NONE);
		saveSourceTextBtn.setText("Save");
		getDocumentControls().add(saveSourceTextBtn);
		saveSourceTextBtn.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		sashForm.setWeights(new int[] { 1, 1 });
		bindingContext = initDataBindings();
		initExtraBindings(bindingContext);
	}

	@Override
	public void performHelp() {
		Shell shell = new Shell(getShell());
		shell.setText("My Custom Help !!");
		shell.setLayout(new GridLayout());
		shell.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Browser browser = new Browser(shell, SWT.NONE);
		browser.setUrl("http://stackoverflow.com/questions/7322489/cant-put-content-behind-swt-wizard-help-button");
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		shell.open();
	}
	
	private ProjectNode getCurrentSelectionParent() {
		ISelection selection = projectTreeViewer.getSelection();
		if (selection instanceof ITreeSelection) {
			ITreeSelection treeSelection = (ITreeSelection) selection;
			Object selectionParent = treeSelection.getPaths()[0].getParentPath().getLastSegment();
			if (selectionParent instanceof ProjectNode) {
				return (ProjectNode) selectionParent;
			}
			else {
				return null;
			}
		}
		return null;
	}

	/**
	 * @return the model
	 */
	public Corpus getModel() {
		return model;
	}

	/**
	 * @return the corpusConstrols
	 */
	private Set<Control> getCorpusControls() {
		return corpusConstrols;
	}

	/**
	 * @return the documentControls
	 */
	private Set<Control> getDocumentControls() {
		return documentControls;
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		IObservableValue treeViewerSelectionObserveSelection = ViewersObservables.observeSingleSelection(projectTreeViewer);
		IObservableValue textTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(corpusNameText);//SWTObservables.observeText(beanText, SWT.Modify);
		IObservableValue treeViewerValueObserveDetailValue = BeanProperties.value("name").observeDetail(treeViewerSelectionObserveSelection);
		bindingContext.bindValue(textTextObserveWidget, treeViewerValueObserveDetailValue);
		
		return bindingContext;
	}
	
	/**
	 * TODO: Description
	 *
	 * @param bindingContext2
	 */
	private void initExtraBindings(DataBindingContext dbc) {
		final IObservableValue projectTreeViewerSelection = ViewersObservables.observeSingleSelection(projectTreeViewer);
		IObservableValue projectElementSelected = new ComputedValue(Boolean.TYPE) {
			protected Object calculate() {
				return Boolean.valueOf(projectTreeViewerSelection.getValue() != null);
			}
		};
//		dbc.bindValue(WidgetProperties.enabled().observe(btnNewCorpus), projectElementSelected);
		
		ViewerSupport.bind(projectTreeViewer, getModel(), BeanProperties.list("children", Corpus.class), BeanProperties.value(ProjectNode.class, "name"));
	}


}
