/**
 * 
 */
package org.corpus_tools.atomic.projects.wizard;

import java.io.File;
import java.io.IOException;

import org.corpus_tools.atomic.projects.wizard.model.Corpus;
import org.corpus_tools.atomic.ui.validation.NotEmptyStringOrNullOrExistingProjectValidator;
import org.corpus_tools.atomic.ui.validation.NotEmptyStringOrNullValidator;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeanProperties;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewAtomicProjectWizardPage extends WizardPage {

	private Button browseButton;
	private Text projectName;
	private Corpus model = new Corpus();
	private Text currentCorpusPath;
	private Label corpusPathLabel;
	private Button browseDirButton;

	/**
	 * 
	 */
	protected NewAtomicProjectWizardPage() {
		super("Create new project");
		setTitle("Create new project");
		setDescription("Specify a name for the new project, and the corpus' .txt file(s).");	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);

		Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		nameLabel.setText("Project name:");

		projectName = new Text(container, SWT.BORDER);
		projectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		corpusPathLabel = new Label(container, SWT.RIGHT);
		corpusPathLabel.setAlignment(SWT.CENTER);
		corpusPathLabel.setText("Corpus path:");
		
		currentCorpusPath = new Text(container, SWT.BORDER);
		currentCorpusPath.setText("Click \"Browse\" to select corpus file(s)");
		currentCorpusPath.setEnabled(false);
		currentCorpusPath.setEditable(false);
		currentCorpusPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		new Label(container, SWT.NONE);
		
		browseButton = new Button(container, SWT.NONE);
		browseButton.setAlignment(SWT.LEFT);
		browseButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		browseButton.setText("Browse &file");
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setPath(getSourceTextFilesFromDialog(false));
			}
		});
		
		browseDirButton = new Button(container, SWT.NONE);
		browseDirButton.setAlignment(SWT.LEFT);
		browseDirButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		browseDirButton.setText("Browse &directory");
		browseDirButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				model.setPath(getSourceTextFilesFromDialog(true));
			}

		});
		initDataBindings();
		setControl(container);
	}
	
	private String getSourceTextFilesFromDialog(boolean isDirDialog) {
		String outputCanonicalPath = null;
		File file = null;
		if (isDirDialog) {
			DirectoryDialog dialog = new DirectoryDialog(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), SWT.OPEN);
			if (dialog.open() != null) {
				String fileName = dialog.getFilterPath();
				file = new File(fileName);
			}
		}
		else {
			FileDialog dialog = new FileDialog(Display.getCurrent() != null ? Display.getCurrent().getActiveShell() : Display.getDefault().getActiveShell(), SWT.OPEN | SWT.SINGLE);
			dialog.setFilterExtensions(new String[] { "*.txt" });
			if (dialog.open() != null) {
				String fileName = dialog.getFileName();
				String filterPath = dialog.getFilterPath();
				if (filterPath != null && filterPath.trim().length() > 0) {
					file = new File(filterPath, fileName);
				}
				else {
					file = new File(fileName);
				}
			}
		}
			try {
				outputCanonicalPath = file.getCanonicalPath();
			}
			catch (IOException ex) {
				outputCanonicalPath = file.getAbsolutePath();
			}
			return outputCanonicalPath;
	}

	@SuppressWarnings("unchecked")
	private void initDataBindings() {
		DataBindingContext dbc = new DataBindingContext(); 
		WizardPageSupport.create(this, dbc);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(projectName), BeanProperties.value(Corpus.class, Corpus.PROPERTY_NAME).observe(model), new UpdateValueStrategy().setBeforeSetValidator(new NotEmptyStringOrNullOrExistingProjectValidator("Project name")), null);
		dbc.bindValue(WidgetProperties.text(SWT.Modify).observe(currentCorpusPath), BeanProperties.value(Corpus.class, Corpus.PROPERTY_PATH).observe(model), new UpdateValueStrategy().setBeforeSetValidator(new NotEmptyStringOrNullValidator("Corpus path")), null);
	}

	/**
	 * @return the model
	 */
	public final Corpus getCorpus() {
		return model;
	}
}
