/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Stephan Druskat
 * 
 */
public class NewAtomicProjectWizardSentenceDetectionPage extends WizardPage {

	private static final Logger log = LoggerFactory.getLogger(NewAtomicProjectWizardSentenceDetectionPage.class);

	protected enum SentenceDetectorType {
		OPENNLP, OPENNLP_CUSTOM, REGEX, THIRDPARTY
	}

	boolean hasSelection = false;
	private Text textUseOwnApache;
	private Text textUseRegex;
	private Button btnPredefinedOpenNLP;
	private Combo predefinedOpenNLPCombo;
	private Button btnUseOwnApache;
	private Button btnUseRegex;
	private Button btnUseThirdpartyDetector;
	private Combo thirdPartyCombo;
	private SentenceDetectorType sentenceDetectorTypeToUse;
	private Button btnLoadOwnApache;
	protected String ownApacheFileString;
	public static final String DANISH = "Danish", GERMAN = "German",
			ENGLISH = "English", FRENCH = "French", ITALIAN = "Italian",
			DUTCH = "Dutch", PORTUGUESE = "Portuguese", SWEDISH = "Swedish";
	public static final String NONE = "Please select ...";
	private static final String THIRDPARTY_DETECTOR_EXTENSION_NAME = "name";
	private static final String EXTENSION_ID = "de.uni_jena.iaa.linktype.atomic.sentenceDetectors";

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

		SelectionAdapter btnSelectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updatePageComplete();
				if (e.widget.equals(predefinedOpenNLPCombo))
					setRadioSelection(btnPredefinedOpenNLP);
				else if (e.widget.equals(thirdPartyCombo))
					setRadioSelection(btnUseThirdpartyDetector);
				else if (e.widget.equals(btnLoadOwnApache))
					setRadioSelection(btnUseOwnApache);
			}
		};

		btnPredefinedOpenNLP = new Button(container, SWT.RADIO);
		btnPredefinedOpenNLP.setText("Use a predefined Apache OpenNLP model");
		btnPredefinedOpenNLP.addSelectionListener(btnSelectionAdapter);
		predefinedOpenNLPCombo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		predefinedOpenNLPCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCombo(predefinedOpenNLPCombo);
		predefinedOpenNLPCombo.addSelectionListener(btnSelectionAdapter);

		btnUseOwnApache = new Button(container, SWT.RADIO);
		btnUseOwnApache.setText("Use own Apache OpenNLP model");
		btnUseOwnApache.addSelectionListener(btnSelectionAdapter);
		textUseOwnApache = new Text(container, SWT.BORDER | SWT.READ_ONLY);
		textUseOwnApache.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textUseOwnApache.setEnabled(false);
		btnLoadOwnApache = new Button(container, SWT.NONE);
		btnLoadOwnApache.setText("Load");
		btnLoadOwnApache.addSelectionListener(btnSelectionAdapter);
		btnLoadOwnApache.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.bin" });
				String result = dialog.open();
				if (!(result == null)) {
					ownApacheFileString = result;
					textUseOwnApache.setText(result);
				}
			}
		});

		btnUseRegex = new Button(container, SWT.RADIO);
		btnUseRegex.setText("Use a regular expression*");
		btnUseRegex.addSelectionListener(btnSelectionAdapter);
		textUseRegex = new Text(container, SWT.BORDER);
		textUseRegex.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		textUseRegex.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				setRadioSelection(btnUseRegex);
			}
		});
		textUseRegex.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				updatePageComplete();
				// FIXME TODO: Check whether input is a valid regex.
			}
		});

		btnUseThirdpartyDetector = new Button(container, SWT.RADIO);
		btnUseThirdpartyDetector.setText("Use a third-party sentence detector");
		btnUseThirdpartyDetector.addSelectionListener(btnSelectionAdapter);
		thirdPartyCombo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		thirdPartyCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCombo(thirdPartyCombo);
		thirdPartyCombo.addSelectionListener(btnSelectionAdapter);

		Label lbltheRegularExpression = new Label(container, SWT.NONE);
		lbltheRegularExpression.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1));
		lbltheRegularExpression.setText("*The regular expression will be used as parameter for a split operation on the corpus text.");
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);

	}

	/**
	 * @param predefinedOpenNLPCombo2
	 */
	private void fillCombo(Combo combo) {
		combo.add(NONE, 0);
		if (combo.equals(predefinedOpenNLPCombo)) {
			combo.add(DANISH, 1);
			combo.add(DUTCH, 2);
			combo.add(ENGLISH, 3);
			combo.add(FRENCH, 4);
			combo.add(GERMAN, 5);
			combo.add(ITALIAN, 6);
			combo.add(PORTUGUESE, 7);
			combo.add(SWEDISH, 8);
		} else if (combo.equals(thirdPartyCombo)) {
			try {
				IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
				if (config.length == 0) {
					combo.setEnabled(false);
					btnUseThirdpartyDetector.setEnabled(false);
					combo.add("Not available", 0);
				} else {
					for (IConfigurationElement e : config) {
						combo.add(e.getAttribute(THIRDPARTY_DETECTOR_EXTENSION_NAME));
					}
				}
			} catch (Exception e) {
				log.error("Error getting extensions for extension point " + EXTENSION_ID, e);
			}

		}
		combo.select(0);
	}

	protected void updatePageComplete() {
		if (btnPredefinedOpenNLP.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectorType.OPENNLP);
			if (!predefinedOpenNLPCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("Please select an Apache OpenNLP language model for sentence detection.");
				setPageComplete(false);
			}
		} else if (btnUseOwnApache.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectorType.OPENNLP_CUSTOM);
			if (!predefinedOpenNLPCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("Please load your Apache OpenNLP language model for sentence detection.");
				setPageComplete(false);
			}
		} else if (btnUseRegex.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectorType.REGEX);
			if (!textUseRegex.getText().isEmpty()) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("Please enter a regular expression");
				setPageComplete(false);
			}
		} else if (btnUseThirdpartyDetector.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectorType.THIRDPARTY);
			setErrorMessage(null);
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	public void setRadioSelection(Button selectComposite) {
		for (Button c : new ArrayList<Button>(Arrays.asList(btnPredefinedOpenNLP, btnUseOwnApache, btnUseRegex, btnUseThirdpartyDetector))) {
			if (c.equals(selectComposite))
				c.setSelection(true);
			else
				c.setSelection(false);
		}
	}

	/**
	 * @return the sentenceDetectorTypeToUse
	 */
	public SentenceDetectorType getSentenceDetectorTypeToUse() {
		return sentenceDetectorTypeToUse;
	}

	/**
	 * @param sentenceDetectorTypeToUse
	 *            the sentenceDetectorTypeToUse to set
	 */
	public void setSentenceDetectorTypeToUse(SentenceDetectorType sentenceDetectorTypeToUse) {
		this.sentenceDetectorTypeToUse = sentenceDetectorTypeToUse;
	}

	/**
	 * @return the predefinedOpenNLPCombo
	 */
	public Combo getPredefinedOpenNLPCombo() {
		return predefinedOpenNLPCombo;
	}

}
