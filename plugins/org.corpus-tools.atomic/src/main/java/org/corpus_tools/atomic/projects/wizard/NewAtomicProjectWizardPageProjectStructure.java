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

import java.io.File;
import java.io.IOException; 
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.projects.Corpus;
import org.corpus_tools.atomic.projects.Document;
import org.corpus_tools.atomic.projects.ProjectNode;
import org.corpus_tools.atomic.ui.api.ExtendedViewerSupport;
import org.corpus_tools.atomic.ui.api.validation.NotEmptyStringOrNullValidator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeItem;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

import org.eclipse.swt.widgets.Group;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Path;

/**
 * A wizard page for the user to construct the structure of a project.
 * <p>
 * FIXME: SWTBot test this class!
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewAtomicProjectWizardPageProjectStructure extends WizardPage {
	private DataBindingContext bindingContext;
	private Text nameText;
	private Text sourceTextText;
	private Corpus model = createSkeleton();
	private Text projectNameText;
	private TreeViewer projectTreeViewer;
	private Button btnRemoveElement;
	private Button btnNewDocument;
	private Button btnNewSubCorpus;
	private Group grpDocument;
	private Label lblSourceText;
	private Button browseSourceTextBtn;
	private Label lblName;
	private boolean doAllDocumentsHaveSourceTexts;
	private Button btnBulkUploadDocument;
	
	/** 
	 * Defines a static logger variable so that it references the {@link org.apache.logging.log4j.Logger} instance named "NewAtomicProjectWizardPageProjectStructure".
	 */
	private static final Logger log = LogManager.getLogger(NewAtomicProjectWizardPageProjectStructure.class);
	
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
	 * Creates a skeleton project structure to be used as a kick off point.
	 *
	 * @return the project data object Corpus
	 */
	private Corpus createSkeleton() {
		Corpus project = new Corpus();
		project.setProjectDataObject(true);
		Corpus rootCorpus = new Corpus();
		rootCorpus.setName("Root corpus");
		project.addChild(rootCorpus);
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

		Composite container = new Composite(parent, SWT.NULL);
		setControl(container);
		GridLayout layout = new GridLayout(1, false);
		container.setLayout(layout);

		Group projectGroup = new Group(container, SWT.NONE);
		projectGroup.setText("Project");
		projectGroup.setLayout(new GridLayout(2, false));
		projectGroup.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

		Label lblName_2 = new Label(projectGroup, SWT.NONE);
		lblName_2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName_2.setText("Name:");

		projectNameText = new Text(projectGroup, SWT.BORDER);
		projectNameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		createButtonComposite(container);

		SashForm sashForm = new SashForm(container, SWT.HORIZONTAL);
		sashForm.setLocation(0, 0);
		GridData gridDataSashForm = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		gridDataSashForm.horizontalAlignment = SWT.FILL;
		gridDataSashForm.verticalAlignment = SWT.FILL;
		sashForm.setLayoutData(gridDataSashForm);

		Composite leftComposite = new Composite(sashForm, SWT.NONE);
		leftComposite.setLayout(new GridLayout(4, false));

		projectTreeViewer = new TreeViewer(leftComposite, SWT.SINGLE | SWT.BORDER);
		new Label(leftComposite, SWT.NONE);
		projectTreeViewer.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		projectTreeViewer.expandAll();

		Composite rightComposite = new Composite(sashForm, SWT.NONE);
		rightComposite.setLayout(new GridLayout(2, false));
		
		lblName = new Label(rightComposite, SWT.NONE);
		lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblName.setText("Element name:");

		nameText = new Text(rightComposite, SWT.BORDER);
		nameText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		grpDocument = new Group(rightComposite, SWT.NONE);
		grpDocument.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		grpDocument.setLayout(new GridLayout(2, false));
		grpDocument.setText("Document");

		lblSourceText = new Label(grpDocument, SWT.NONE);
		lblSourceText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblSourceText.setText("Source text:");

		sourceTextText = new Text(grpDocument, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		sourceTextText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		new Label(grpDocument, SWT.NONE);

		browseSourceTextBtn = new Button(grpDocument, SWT.NONE);
		browseSourceTextBtn.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browseSourceTextBtn.setText("Bro&wse");
		browseSourceTextBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Open FileBrowser and set returned text from file to sourceText text field
				List<String> sourceTextFilePaths = getSourceTextFilesFromDialog(false);
				if (sourceTextFilePaths != null && sourceTextFilePaths.size() == 1) {
					String sourceTextFilePath = sourceTextFilePaths.get(0);
				String sourceText = null;
				try {
					sourceText = readSourceTextFileFromPath(sourceTextFilePath);
				}
				catch (IOException e1) {
					log.error("Could not read file \"{}\".", sourceTextFilePath, e1);
					sourceTextText.setText("");
				}
				sourceTextText.setText(sourceText);
				}
				else {
					log.info("No document source file was selected. Operation canceled.");
					return;
				}
			}

		});

		sashForm.setWeights(new int[] { 1, 1 });
		bindingContext = initDataBindings();
		WizardPageSupport.create(this, bindingContext);
		initExtraBindings(bindingContext);
		
		// If selection is emtpy, element name validator is triggered. Therefore, select first element
		Assert.isNotNull(getModel().getChildren().iterator().next());
		projectTreeViewer.setSelection(new StructuredSelection(getModel().getChildren().iterator().next()));
	}

	/**
	 * Creates and returns a {@link Composite} containing the four element control buttons.
	 *
	 * @param container
	 */
	private void createButtonComposite(Composite container) {
		Composite buttonComposite = new Composite(container, SWT.NONE);
		buttonComposite.setLayout(new RowLayout());
		
		final Button btnNewRootCorpus = new Button(buttonComposite, SWT.NONE);
		btnNewRootCorpus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String numberOfExistingRootCorpora = (getModel().getChildren().size() > 0) ? " " + String.valueOf(getModel().getChildren().size() + 1) : "";
				Corpus newRootCorpus = new Corpus();
				newRootCorpus.setName("Root corpus" + numberOfExistingRootCorpora);
				getModel().addChild(newRootCorpus);
				projectTreeViewer.refresh();
				projectTreeViewer.setSelection(new StructuredSelection(newRootCorpus));
				nameText.selectAll();
				nameText.setFocus();
			}
		});
		btnNewRootCorpus.setText("Add root &corpus");
		
		btnNewSubCorpus = new Button(buttonComposite, SWT.NONE);
		btnNewSubCorpus.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Can only be corpus, otherwise button will be disabled due to extra binding
				Corpus parent = (Corpus) getSelectedElement();
				Corpus newSubCorpus = new Corpus();
				newSubCorpus.setName("New subcorpus");
				parent.addChild(newSubCorpus);
				projectTreeViewer.setSelection(new StructuredSelection(newSubCorpus));
				projectTreeViewer.refresh();
				projectTreeViewer.expandAll();
				projectTreeViewer.setSelection(new StructuredSelection(newSubCorpus));
				nameText.selectAll();
				nameText.setFocus();
			}
		});
		btnNewSubCorpus.setText("Add &subcorpus");

		
		btnNewDocument = new Button(buttonComposite, SWT.NONE);
		btnNewDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Can only be corpus, otherwise button will be disabled due to extra binding
				Corpus parent = (Corpus) getSelectedElement();
				Document newDocument = new Document();
				newDocument.setName("New document");
				parent.addChild(newDocument);
				projectTreeViewer.refresh();
				projectTreeViewer.expandAll();
				projectTreeViewer.setSelection(new StructuredSelection(newDocument));
				nameText.selectAll();
				nameText.setFocus();
			}
		});
		btnNewDocument.setText("Add &document");
		
		btnBulkUploadDocument = new Button(buttonComposite, SWT.NONE);
		btnBulkUploadDocument.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem selectedItem = projectTreeViewer.getTree().getSelection()[0];
				TreeItem parentItem = selectedItem.getParentItem();
				Corpus parent;
				if (parentItem == null) {
					parent = getModel();
				}
				else {
					parent = (Corpus) parentItem.getData();
				}
				Set<ProjectNode> childrenSet = new LinkedHashSet<ProjectNode>(parent.getChildren());
				List<String> sourceTextFilePaths = getSourceTextFilesFromDialog(true);
				if (sourceTextFilePaths != null) {
					for (String filePath : sourceTextFilePaths) {
						try {
							String sourceText = readSourceTextFileFromPath(filePath);
							String documentName = extractDocumentNameFromFilePathString(filePath);
							// For all file paths, write one new document and add to parent
							Document document = new Document();
							document.setName(documentName);
							document.setSourceText(sourceText);
							// Has to be done this way otherwise the binding doesn't pick up the changes
							childrenSet.add(document);
							parent.setChildren(childrenSet);
							childrenSet = new LinkedHashSet<ProjectNode>(parent.getChildren());
						}
						catch (IOException e1) {
							log.error("Could not read file \"{}\".", filePath, e1);
						}
					}
				}
				else {
					log.info("No document source file (or source files) was selected. Operation canceled.");
					return;
				}
			}

			private String extractDocumentNameFromFilePathString(String filePath) {
				Path path = new Path(filePath);
				return path.lastSegment().replaceAll("." + path.getFileExtension(), "");
			}
		});
		btnBulkUploadDocument.setText("Bulk &upload documents");
		
		btnRemoveElement = new Button(buttonComposite, SWT.NONE);
		btnRemoveElement.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TreeItem selectedItem = projectTreeViewer.getTree().getSelection()[0];
				TreeItem parentItem = selectedItem.getParentItem();
				Corpus parent;
				if (parentItem == null) {
					parent = getModel();
				}
				else {
					parent = (Corpus) parentItem.getData();
				}
				Set<ProjectNode> childrenSet = new LinkedHashSet<ProjectNode>(parent.getChildren());
				childrenSet.remove(selectedItem.getData());
				parent.setChildren(childrenSet);
			}
		});
		btnRemoveElement.setText("&Remove element");
	}

	/**
	 * Returns the selected element in the project tree viewer.
	 *
	 * @return the selected element in the project tree viewer
	 */
	protected ProjectNode getSelectedElement() {
		IStructuredSelection selection = (IStructuredSelection) projectTreeViewer.getSelection();
		if (selection.isEmpty())
			return null;
		return (ProjectNode) selection.getFirstElement();
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
	
	/**
	 * Initializes the data bindings from model to widgets and returns the binding context.
	 *
	 * @return bindingContext the binding context
	 */
	private DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		
		// ObservableValue for the current tree viewer selection
		final IObservableValue projectTreeViewerSelection = ViewersObservables.observeSingleSelection(projectTreeViewer);

		// Binds the "Element name" text field to the 'name' property of the selected element. 
		IObservableValue nameTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(nameText);
		IObservableValue nameObserveTreeElement = BeanProperties.value("name").observeDetail(projectTreeViewerSelection);
		bindingContext.bindValue(nameTextObserveWidget, nameObserveTreeElement, new UpdateValueStrategy().setBeforeSetValidator(new NotEmptyStringOrNullValidator("Element name")), null);
		
		// Binds the "Source text" text field to the 'sourceText' property of the selected element, iff the latter is of type Document.
		// NOTE: This binding has no validator. Source texts must be validated before can flip to net page.
		IObservableValue sourceTextTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(sourceTextText);
		IObservableValue sourceTextObserveTreeElement = BeanProperties.value("sourceText").observeDetail(projectTreeViewerSelection);
		bindingContext.bindValue(sourceTextTextObserveWidget, sourceTextObserveTreeElement);
		
		// Binds the "Project name text" text field to the 'name' property of the project.
		IObservableValue projectNameTextObserveWidget = WidgetProperties.text(SWT.Modify).observe(projectNameText);
		IObservableValue projectNameObserveProjectNameText = BeanProperties.value("name").observe(getModel());
		bindingContext.bindValue(projectNameTextObserveWidget, projectNameObserveProjectNameText, new UpdateValueStrategy().setBeforeSetValidator(new NotEmptyStringOrNullValidator("Project name")), null);
		
		return bindingContext;
	}
	
	/**
	 * Adds extra bindings that are not "real" data bindings (i.e., enabled/disabled buttons, etc.)
	 *
	 * @param bindingContext2
	 */
	private void initExtraBindings(DataBindingContext dbc) {
		// Observable value for selected element in tree viewer
		final IObservableValue projectTreeViewerSelection = ViewersObservables.observeSingleSelection(projectTreeViewer);
		
		// Enable the "Add document" and "Add subcorpus" buttons only if the currently selected element in the tree is a Corpus
		IObservableValue corpusSelected = new ComputedValue(Boolean.TYPE) {
			protected Object calculate() {
				return Boolean.valueOf(projectTreeViewerSelection.getValue() != null && projectTreeViewerSelection.getValue() instanceof Corpus);
			}
		};
		dbc.bindValue(WidgetProperties.enabled().observe(btnNewDocument), corpusSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(btnNewSubCorpus), corpusSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(btnBulkUploadDocument), corpusSelected);
		
		// Enable the "Remove element" button only if the currently selected element in the tree is not null
		IObservableValue anythingSelected = new ComputedValue(Boolean.TYPE) {
			protected Object calculate() {
				return Boolean.valueOf(projectTreeViewerSelection.getValue() != null);
			}
		};
		dbc.bindValue(WidgetProperties.enabled().observe(btnRemoveElement), anythingSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(lblName), anythingSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(nameText), anythingSelected);
		
		// Disable all document-relevant widgets when selected element is not of type Document
		IObservableValue documentSelected = new ComputedValue(Boolean.TYPE) {
			protected Object calculate() {
				return Boolean.valueOf(projectTreeViewerSelection.getValue() != null && projectTreeViewerSelection.getValue() instanceof Document);
			}
		};
		dbc.bindValue(WidgetProperties.enabled().observe(grpDocument), documentSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(sourceTextText), documentSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(lblSourceText), documentSelected);
		dbc.bindValue(WidgetProperties.enabled().observe(browseSourceTextBtn), documentSelected);
		
		// Bind model to the project tree viewer
		ExtendedViewerSupport.bind(projectTreeViewer, getModel(), BeanProperties.set("children", Corpus.class), BeanProperties.value(ProjectNode.class, "name"), ProjectTreeWizardLabelProvider.class);
	}
	
	/* 
	 * @copydoc @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		doAllDocumentsHaveSourceTexts = true;
		checkDoAllDocumentsHaveSourceTexts(getModel());
		return getErrorMessage() == null && doAllDocumentsHaveSourceTexts;
		// FIXME TODO: Idea: create a binding for something that always runs, add validator to check if all documents have source texts
	}
	
	/**
	 * Iterates over all children of a {@link Corpus} and, sets the
	 * boolean field to false should a child <code>instanceof</code>
	 * {@link Document} have an empty value or <code>null</code> for
	 * its parameter {@link Document#getSourceText()}.
	 *
	 * @param The parent corpus
	 */
	private void checkDoAllDocumentsHaveSourceTexts(Corpus parentCorpus) {
		for (ProjectNode child : parentCorpus.getChildren()) {
			if (child instanceof Document) {
				String text = ((Document) child).getSourceText();
				if (text == null || text.isEmpty()) {
					setErrorMessage("Document " + child.getName() + " has no source text!");
					doAllDocumentsHaveSourceTexts = false;
				}
			}
			else {
				checkDoAllDocumentsHaveSourceTexts((Corpus) child);
			}
		}
	}

	/* 
	 * @copydoc @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return getErrorMessage() == null;
	}

	/**
	 * @return the model
	 */
	public Corpus getModel() {
		return model;
	}

	/**
	 * Reads the contents of a file, given a path and tries to detect the {@link Charset}
	 * with the help of a {@link CharsetDetector}. Creates a new {@link String} from
	 * the contents, with the detected or the default {@link Charset}, depending on
	 * the confidence rate of the {@link CharsetMatch}. 
	 *
	 * @param sourceTextFilePath The path to the text file
	 * @return a {@link String} with the {@link Charset}-encoded contents of the text file
	 * @throws IOException when a file cannot be read
	 */
	private String readSourceTextFileFromPath(String sourceTextFilePath) throws IOException {
		String sourceText;
		Charset charset = null;
		byte[] allBytesFromSourceTextFile = Files.readAllBytes(Paths.get(sourceTextFilePath));
		CharsetDetector detector = new CharsetDetector();
		detector.setText(allBytesFromSourceTextFile);
		CharsetMatch match = detector.detect();
		int confidence;
		confidence = match.getConfidence();
		log.info("Detected source file \"{}\" with charset {} with a confidence of {}.", sourceTextFilePath, match.getName(), match.getConfidence());
		if (confidence > 50) {
			charset = Charset.availableCharsets().get(match.getName());
			if (charset != null && charset instanceof Charset) {
				log.info("Detected charset is in the list of available charset and will be used for reading the file: {}.", charset);
				sourceText = new String(allBytesFromSourceTextFile, charset);
			}
			else {
				log.info("Detected charset cannot be used, defaulting to {}.", Charset.defaultCharset().displayName());
				sourceText = new String(allBytesFromSourceTextFile, Charset.defaultCharset());
			}
		}
		else {
			log.info("Confidence in detected charset for \"{}\" < 50, defaulting to {}.", sourceTextFilePath, Charset.defaultCharset().displayName());
			sourceText = new String(allBytesFromSourceTextFile, Charset.defaultCharset());
		}
		return sourceText;
	}
	
	/**
	 * Gets a {@link String} array {@link String}s representing file paths from a {@link FileDialog}. 
	 * The dialog filters for files with the file extension "txt". The returned list can
	 * <ul>
	 * <li>contain one element, given that the parameter is false</li>
	 * <li>contain n elements, given that the parameter is true</li>
	 * <li>be null, when no files are selected of the {@link FileDialog} was cancelled.
	 * </ul>
	 *
	 * @param allowMultipleFileSelection Whether the {@link FileDialog} should allow for the selection of more than one file
	 * @return A {@link List} of {@link String}s which are paths to text files selected in the {@link FileDialog}
	 */
	private List<String> getSourceTextFilesFromDialog(boolean allowMultipleFileSelection) {
		FileDialog dialog = allowMultipleFileSelection ? new FileDialog(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), SWT.OPEN | SWT.MULTI) : 
			new FileDialog(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), SWT.OPEN | SWT.SINGLE);
		dialog.setFilterExtensions(new String[] { "*.txt" });
		List<String> files = new ArrayList<>();
		if (dialog.open() != null) {
			String[] names = dialog.getFileNames();
			for (int i = 0, n = names.length; i < n; i++) {
				StringBuffer buf = new StringBuffer(dialog.getFilterPath());
				if (buf.charAt(buf.length() - 1) != File.separatorChar)
					buf.append(File.separatorChar);
				buf.append(names[i]);
				files.add(buf.toString());
				for (String file : files) {
					System.err.println(file);
				}
			}
			return files;
		}
		return null;
	}

}
