/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects;

import java.net.MalformedURLException;
import java.net.URL;
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
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_jena.iaa.linktype.atomic.core.corpus.LocaleProvider;

/**
 * @author Stephan Druskat
 * 
 */
public class NewAtomicProjectWizardSentenceDetectionPage extends WizardPage {

	private static final Logger log = LoggerFactory.getLogger(NewAtomicProjectWizardSentenceDetectionPage.class);

	protected enum SentenceDetectorType {
		OPENNLP, OPENNLP_CUSTOM, BREAK_ITERATOR, THIRDPARTY
	}

	boolean hasSelection = false;
	private Text textUseOwnApache;
	private Text textUseBreakIterator;
	private Button btnPredefinedOpenNLP;
	private Combo predefinedOpenNLPCombo;
	private Button btnUseOwnApache;
	private Button btnUseBreakIterator;
	private Button btnUseThirdpartyDetector;
	private Combo thirdPartyCombo;
	private SentenceDetectorType sentenceDetectorTypeToUse;
	private Button btnLoadOwnApache;
	protected String ownApacheFileString;
	private Combo localeCombo;
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

		btnUseBreakIterator = new Button(container, SWT.RADIO);
		btnUseBreakIterator.setText("Use java.text.BreakIterator*");
		btnUseBreakIterator.addSelectionListener(btnSelectionAdapter);
		localeCombo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		localeCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCombo(localeCombo);
		localeCombo.addSelectionListener(btnSelectionAdapter);

		btnUseThirdpartyDetector = new Button(container, SWT.RADIO);
		btnUseThirdpartyDetector.setText("Use a third-party sentence detector");
		btnUseThirdpartyDetector.addSelectionListener(btnSelectionAdapter);
		thirdPartyCombo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		thirdPartyCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCombo(thirdPartyCombo);
		thirdPartyCombo.addSelectionListener(btnSelectionAdapter);

		Link link = new Link(container, SWT.NONE);
	    String message = "*Cf. the <a href=\"https://docs.oracle.com/javase/6/docs/api/java/text/BreakIterator.html\">BreakIterator API documentation</a>.";
	    link.setText(message);
	    link.setSize(400, 100);
	    link.addSelectionListener(new SelectionAdapter(){
	        @Override
	        public void widgetSelected(SelectionEvent e) {
	               System.out.println("You have selected: "+e.text);
	               try {
	                //  Open default external browser 
	                PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(new URL(e.text));
	              } 
	             catch (PartInitException ex) {
	                // TODO Auto-generated catch block
	                 ex.printStackTrace();
	            } 
	            catch (MalformedURLException ex) {
	                // TODO Auto-generated catch block
	                ex.printStackTrace();
	            }
	        }
	    });
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

		} else if (combo.equals(localeCombo)) {
			for (String localeDisplayName : LocaleProvider.getLocaleNames()) {
				combo.add(localeDisplayName);
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
			if (!getTextUseOwnApache().getText().isEmpty()) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("Please load your Apache OpenNLP language model for sentence detection.");
				setPageComplete(false);
			}
		} else if (btnUseBreakIterator.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectorType.BREAK_ITERATOR);
			if (!localeCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			} else {
				setErrorMessage("Please select the locale the BreakIterator should operate on.");
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
		for (Button c : new ArrayList<Button>(Arrays.asList(btnPredefinedOpenNLP, btnUseOwnApache, btnUseBreakIterator, btnUseThirdpartyDetector))) {
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

	/**
	 * @return the textUseOwnApache
	 */
	public Text getTextUseOwnApache() {
		return textUseOwnApache;
	}

	/**
	 * @return the textUseBreakIterator
	 */
	public Text getTextUseDelims() {
		return textUseBreakIterator;
	}

	/**
	 * @return the localeCombo
	 */
	public Combo getLocaleCombo() {
		return localeCombo;
	}

}
