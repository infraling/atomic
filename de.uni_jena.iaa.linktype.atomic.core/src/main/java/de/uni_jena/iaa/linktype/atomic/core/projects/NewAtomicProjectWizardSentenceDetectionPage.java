/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Stephan Druskat
 * 
 */
public class NewAtomicProjectWizardSentenceDetectionPage extends WizardPage {
	private Text txtFile;
	private Text text;
	private Text text_1;
	private Text text_2;

	/**
	 * 
	 */
	public NewAtomicProjectWizardSentenceDetectionPage() {
		super("Sentence detection");
		setPageComplete(false);
		setTitle("Sentence detection");
		setDescription("Some Atomic editors work on sentences. Please choose how to detect sentences in the corpus text.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
	    setControl(container);
	    container.setLayout(new GridLayout(3, false));
	    
	    Button btnUseAPredefined = new Button(container, SWT.RADIO);
	    btnUseAPredefined.setText("Use a predefined Apache OpenNLP model");
	    btnUseAPredefined.setSelection(true);
	    
	    Combo combo = new Combo(container, SWT.NONE);
	    combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    
	    Button btnUseOwnApache = new Button(container, SWT.RADIO);
	    btnUseOwnApache.setText("Use own Apache OpenNLP model");
	    
	    text_1 = new Text(container, SWT.BORDER);
	    text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
	    
	    Button btnUpload = new Button(container, SWT.NONE);
	    btnUpload.setText("Upload");
	    
	    Button btnUseARegular = new Button(container, SWT.RADIO);
	    btnUseARegular.setText("Use a regular expression*");
	    
	    text_2 = new Text(container, SWT.BORDER);
	    text_2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
	    
	    Label lbltheRegularExpression = new Label(container, SWT.NONE);
	    lbltheRegularExpression.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
	    lbltheRegularExpression.setText("*The regular expression will be used as parameter for a split operation on the corpus text.");
	    new Label(container, SWT.NONE);
	    new Label(container, SWT.NONE);
	    
	}

	protected void updatePageComplete() {
//		String projectName = getProjectName();
//		if (projectName != null) {
//			if (AtomicProjectService.getInstance().isProjectExisting(projectName)) {
//				setMessage(null);
//				setErrorMessage("Project does already exist - choose another name.");
//				setPageComplete(false);
//			} else {
//				setMessage(null);
//				setErrorMessage(null);
//				setPageComplete(true);
//			}
//		} else {
//			setMessage(null);
//			setErrorMessage(null);
//			setPageComplete(false);
//		}
	}
}
