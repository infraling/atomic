package de.uni_jena.iaa.linktype.atomic.core.wizards.newproject;

import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

import de.uni_jena.iaa.linktype.atomic.core.Messages;
import de.uni_jena.iaa.linktype.atomic.core.utils.AtomicCoreUtils;
import de.uni_jena.iaa.linktype.atomic.core.utils.AtomicTokenizerUtils;

public class AtomicProjectBasicsWizardPage extends WizardPage {
	
	private Text txtProjectName;
	private ArrayList<String> projectNames;
	private Text txtCorpusTextDefault;
	private String corpusText;
	private Button btnBrowse, btnEnterText;
	private SelectionAdapter buttonAdapter = createBrowseButtonListener();
	private Combo comboTokenizer;
	
	/**
	 * Create the wizard.
	 */
	public AtomicProjectBasicsWizardPage() {
		super("wizardPage"); //$NON-NLS-1$
		setPageComplete(false);
		setTitle("Create a new Atomic project"); //$NON-NLS-1$
		setDescription("This wizard helps you create an Atomic project from a raw corpus text."); //$NON-NLS-1$
		writeExistingProjectNamesToArray(ResourcesPlugin.getWorkspace().getRoot().getProjects());
	}

	private void writeExistingProjectNamesToArray(IProject[] projects) {
		int numberOfProjects = projects.length;
		projectNames = new ArrayList<String>(numberOfProjects);
		for (int i = 0; i < numberOfProjects; i++) {
			projectNames.add(projects[i].getName());
		}
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(2, false));
		
		Label lblNewLabel_1 = new Label(container, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		lblNewLabel_1.setText("Enter the name for the new project, and choose the text file containing the raw corpus text.\r\nAlternatively, you can enter or paste the corpus text into the text field."); //$NON-NLS-1$ // FIXME: Put back in once Import is completely implemented: \r\n\r\nNOTE: If you want to import a pre-formatted existing corpus into Atomic, you can do so by\r\nstarting the Corpus Import Wizard: Right-click into the navigation window, and choose\r\n\"Import\" > \"Corpus Import\".
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Label lblNewLabel = new Label(container, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Project name:"); //$NON-NLS-1$
		
		setTxtProjectName(new Text(container, SWT.BORDER));
		getTxtProjectName().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getTxtProjectName().addKeyListener(createKeyListener());
		getTxtProjectName().setFocus();
		
		Label lblCorpusTextFile = new Label(container, SWT.NONE);
		lblCorpusTextFile.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblCorpusTextFile.setText("Choose corpus text file:"); //$NON-NLS-1$
		
		btnBrowse = new Button(container, SWT.NONE);
		btnBrowse.setText("Browse..."); //$NON-NLS-1$
		btnBrowse.addSelectionListener(buttonAdapter);
		
		Label lblEnterCorpusText = new Label(container, SWT.NONE);
		lblEnterCorpusText.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblEnterCorpusText.setText("Enter corpus text:"); //$NON-NLS-1$
		
		btnEnterText = new Button(container, SWT.NONE);
		btnEnterText.setText("..."); //$NON-NLS-1$
		btnEnterText.addSelectionListener(buttonAdapter);
		
		Label lblRawCorpusText = new Label(container, SWT.NONE);
		lblRawCorpusText.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		lblRawCorpusText.setText("Raw corpus text:"); //$NON-NLS-1$
		
		txtCorpusTextDefault = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtCorpusTextDefault.setText(Messages.AtomicProjectBasicsWizardPage_CORPUS_TEXTFIELD_DEFAULT);
		GridData gd_txtclickTo = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		gd_txtclickTo.heightHint = 70;
		txtCorpusTextDefault.setLayoutData(gd_txtclickTo);
		txtCorpusTextDefault.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (e.data != null && e.data instanceof String[] && ((String[]) e.data)[0].equals("BROWSE")) //$NON-NLS-1$
					((Text) e.widget).setText(((String[]) e.data)[1].toString());
			}
		});
		
		Label lblChooseTokenizer = new Label(container, SWT.NONE);
		lblChooseTokenizer.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblChooseTokenizer.setText("Choose tokenizer:"); //$NON-NLS-1$
		
		setComboTokenizer(new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY));
		getComboTokenizer().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		getComboTokenizer().setItems(AtomicTokenizerUtils.getTokenizerNames());
		getComboTokenizer().select(0);
	}

	private SelectionAdapter createBrowseButtonListener() {
		return new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				String eventKeyString = ((Button) e.getSource()).getText();
				if (eventKeyString.equals("Browse...")) //$NON-NLS-1$
					try {
						getCorpusTextFromFileDialog();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				else getCorpusTextFromInputDialog(); // for eventKeyString.equals("...");
			}

			/**
			 * 
			 */
			private void getCorpusTextFromInputDialog() {
				InputDialogWithConfirmation inputDialog = new InputDialogWithConfirmation(Display.getCurrent().getActiveShell(), "Title", "Message", "[Enter or paste the raw corpus text here. Default internal text encoding: UTF-16]", null); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if (inputDialog.open() == InputDialog.OK) {
					setCorpusText(inputDialog.getValue());
				}
				else {
					return;
				}
				fireCorpusTextChanged();
			}

			/**
			 * @throws IOException 
			 * 
			 */
			private void getCorpusTextFromFileDialog() throws IOException {
				FileDialogWithConfirmation corpusTextFileDialog = new FileDialogWithConfirmation(Display.getCurrent().getActiveShell(), SWT.OPEN);
				corpusTextFileDialog.setFilterExtensions(new String[] {"*.txt"}); //$NON-NLS-1$
				corpusTextFileDialog.setText("Select .txt file containing the corpus text."); //$NON-NLS-1$
				setCorpusText(AtomicCoreUtils.extractStringFromFile(corpusTextFileDialog.open()));
				fireCorpusTextChanged();
			}
			
			/**
			 * 
			 */
			private void fireCorpusTextChanged() {
				Event event = new Event();
				String[] data = {"BROWSE", getCorpusText()}; //$NON-NLS-1$
				event.data = data;
				if (getCorpusText() != null)
					txtCorpusTextDefault.notifyListeners(SWT.Modify, event);
			}

		};
	}

	private KeyListener createKeyListener() {
		return new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				setErrorMessage(null);
				String newText = txtProjectName.getText();
				if (newText.equals("") || newText.isEmpty()) { //$NON-NLS-1$
					setErrorMessage("Project name must not be empty."); //$NON-NLS-1$
					setPageComplete(false);
				}
				else if (projectNames.contains(newText)) {
					setErrorMessage("A project with the name " + newText + " already exists."); //$NON-NLS-1$ //$NON-NLS-2$
					setPageComplete(false);
				}
				else {
					setErrorMessage(null);
					setPageComplete(true);
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				// Do nothing.
			}
		};
	}
	
	/**
	 * @return the corpusText
	 */
	public String getCorpusText() {
		return corpusText;
	}

	/**
	 * @param corpusText the corpusText to set
	 */
	public void setCorpusText(String corpusText) {
		this.corpusText = corpusText;
	}
	
	/**
	 * @return the comboTokenizer
	 */
	public Combo getComboTokenizer() {
		return comboTokenizer;
	}

	/**
	 * @param comboTokenizer the comboTokenizer to set
	 */
	public void setComboTokenizer(Combo comboTokenizer) {
		this.comboTokenizer = comboTokenizer;
	}

	/**
	 * @return the txtProjectName
	 */
	public Text getTxtProjectName() {
		return txtProjectName;
	}

	/**
	 * @param txtProjectName the txtProjectName to set
	 */
	public void setTxtProjectName(Text txtProjectName) {
		this.txtProjectName = txtProjectName;
	}

	/**
	 * @author Stephan Druskat
	 *
	 */
	public class FileDialogWithConfirmation {
		
		private FileDialog dialog;

		public FileDialogWithConfirmation(Shell activeShell, int open) {
			dialog = new FileDialog(activeShell, open);
		}

		public void setText(String string) {
			dialog.setText(string);
		}

		public void setFilterExtensions(String[] strings) {
			dialog.setFilterExtensions(strings);
		}

		public String open() {
			String fileName = null;

		    boolean done = false;

		    while (!done) {
		    	fileName = dialog.open();
		    	
		    	// User clicks Cancel on FileDialog
		    	if (fileName == null) {
		    		done = true;
		    	}
		    	// User clicks "OK" on FileDialog
		    	else {
		    		MessageBox confirmationBox = new MessageBox(dialog.getParent(), SWT.ICON_WARNING | SWT.YES | SWT.NO);
		    		confirmationBox.setMessage("WARNING!\n" //$NON-NLS-1$
		    				+ "This will overwrite the current corpus text with the contents of " + fileName + "!\n" //$NON-NLS-1$ //$NON-NLS-2$
		    				+ "Are you sure?"); //$NON-NLS-1$
		    		done = (confirmationBox.open() == SWT.YES);
		    	} 
		    }
		    return fileName;
		}

	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class InputDialogWithConfirmation extends InputDialog {
		
		private Shell shell; 
		
		public InputDialogWithConfirmation(Shell activeShell, String title, String message, String initialContent, Object object) {
			super(activeShell, title, message, initialContent, null);
			this.shell = activeShell;
		}

		@Override
		protected int getInputTextStyle() {
			return SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL;
		}

		// This hack is needed to actually get the InputDialog to display a multi-line text field
		@Override
		protected Control createDialogArea(Composite parent) {
			Control res = super.createDialogArea(parent);
			((GridData) this.getText().getLayoutData()).heightHint = 200;
			return res;
		}

		@Override
		public int open() {
			int returnInt = -1;

		    boolean done = false;

		    while (!done) {
		    	returnInt = super.open();
		    	
		    	// User clicks Cancel on InputDialog
		    	if (returnInt == InputDialog.CANCEL) {
		    		done = true;
		    	}
		    	// User clicks "OK" on InputDialog
		    	else {
		    		MessageBox confirmationBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES | SWT.NO);
		    		confirmationBox.setMessage("WARNING!\n" //$NON-NLS-1$
		    				+ "This will overwrite the current corpus text with the text you have entered!\n" //$NON-NLS-1$
		    				+ "Are you sure?"); //$NON-NLS-1$
		    		done = (confirmationBox.open() == SWT.YES);
		    	} 
		    }
		    return returnInt;
		}

	}



}
