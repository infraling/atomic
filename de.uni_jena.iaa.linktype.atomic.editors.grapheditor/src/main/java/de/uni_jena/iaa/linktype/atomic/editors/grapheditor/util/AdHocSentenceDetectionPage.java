/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.corpus.LocaleProvider;
import de.uni_jena.iaa.linktype.atomic.core.corpus.SentenceDetectionService;

/**
 * @author Stephan Druskat
 * 
 */
public class AdHocSentenceDetectionPage extends WizardPage {

	boolean hasSelection = false;
	private Text textUseOwnApache;
	private Text textUseBreakIterator;
	private Button btnPredefinedOpenNLP;
	private Combo predefinedOpenNLPCombo;
	private Button btnUseOwnApache;
	private Button btnUseBreakIterator;
	private Button btnUseThirdpartyDetector;
	private Combo thirdPartyCombo;
	private SentenceDetectionService.SentenceDetectorType sentenceDetectorTypeToUse;
	private Button btnLoadOwnApache;
	protected String ownApacheFileString;
	private Combo localeCombo;
	private Button btnExistingSentenceLayer;
	private Combo layerCombo;
	private SDocumentGraph graph;
	private ArrayList<SLayer> layerList;
	public static final String NONE = "Please select ...";
	public static final String THIRDPARTY_DETECTOR_EXTENSION_NAME = "name";
	public static final String EXTENSION_ID = "de.uni_jena.iaa.linktype.atomic.sentenceDetectors";

	private static final Logger log = LoggerFactory.getLogger(AdHocSentenceDetectionPage.class);

	/**
	 * @param sDocumentGraph
	 * 
	 */
	public AdHocSentenceDetectionPage(String pageName, SDocumentGraph sDocumentGraph) {
		super(pageName);
		this.graph = sDocumentGraph;
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
				else if (e.widget.equals(layerCombo))
					setRadioSelection(btnExistingSentenceLayer);
				else if (e.widget.equals(localeCombo))
					setRadioSelection(btnUseBreakIterator);
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
					updatePageComplete();
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

		Label sep1 = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		sep1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		Label existingLayers = new Label(container, SWT.NONE);
		existingLayers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		existingLayers.setText("If your corpus has already been parsed for sentence boundaries,\nplease select the layer that includes the resulting sentence spans below.");
		
		btnExistingSentenceLayer = new Button(container, SWT.RADIO);
		btnExistingSentenceLayer.setText("Select an existing layer holding sentence spans only");
		btnExistingSentenceLayer.addSelectionListener(btnSelectionAdapter);
		layerCombo = new Combo(container, SWT.NONE | SWT.READ_ONLY);
		layerCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		fillCombo(layerCombo);
		layerCombo.addSelectionListener(btnSelectionAdapter);

		Label sep2 = new Label(container, SWT.HORIZONTAL | SWT.SEPARATOR);
		sep2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
		
		Link link = new Link(container, SWT.NONE);
		String message = "*Cf. the <a href=\"https://docs.oracle.com/javase/6/docs/api/java/text/BreakIterator.html\">BreakIterator API documentation</a>.";
		link.setText(message);
		link.setSize(400, 100);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("You have selected: " + e.text);
				try {
					// Open default external browser
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
			combo.add(SentenceDetectionService.DANISH, 1);
			combo.add(SentenceDetectionService.DUTCH, 2);
			combo.add(SentenceDetectionService.ENGLISH, 3);
			combo.add(SentenceDetectionService.FRENCH, 4);
			combo.add(SentenceDetectionService.GERMAN, 5);
			combo.add(SentenceDetectionService.ITALIAN, 6);
			combo.add(SentenceDetectionService.PORTUGUESE, 7);
			combo.add(SentenceDetectionService.SWEDISH, 8);
		}
		else if (combo.equals(thirdPartyCombo)) {
			try {
				IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_ID);
				if (config.length == 0) {
					combo.setEnabled(false);
					btnUseThirdpartyDetector.setEnabled(false);
					combo.add("Not available", 0);
				}
				else {
					for (IConfigurationElement e : config) {
						combo.add(e.getAttribute(THIRDPARTY_DETECTOR_EXTENSION_NAME));
					}
				}
			}
			catch (Exception e) {
				log.error("Error getting extensions for extension point " + EXTENSION_ID, e);
			}

		}
		else if (combo.equals(localeCombo)) {
			for (String localeDisplayName : LocaleProvider.getLocaleNames()) {
				combo.add(localeDisplayName);
			}
		}
		else if (combo.equals(layerCombo)) {
			layerList = new ArrayList<SLayer>();
			for (int i = 0; i < graph.getSLayers().size(); i++) {
				SLayer layer = graph.getSLayers().get(i);
				getLayerList().add(i, layer);
				combo.add(layer.getSName());
			}
		}
		combo.select(0);
	}

	protected void updatePageComplete() {
		if (btnPredefinedOpenNLP.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType.OPENNLP);
			if (!predefinedOpenNLPCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			}
			else {
				setErrorMessage("Please select an Apache OpenNLP language model for sentence detection.");
				setPageComplete(false);
			}
		}
		else if (btnUseOwnApache.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType.OPENNLP_CUSTOM);
			if (!getTextUseOwnApache().getText().isEmpty()) {
				setErrorMessage(null);
				setPageComplete(true);
			}
			else {
				setErrorMessage("Please load your Apache OpenNLP language model for sentence detection.");
				setPageComplete(false);
			}
		}
		else if (btnUseBreakIterator.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType.BREAK_ITERATOR);
			if (!localeCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			}
			else {
				setErrorMessage("Please select the locale the BreakIterator should operate on.");
				setPageComplete(false);
			}
		}
		else if (btnUseThirdpartyDetector.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType.THIRDPARTY);
			setErrorMessage(null);
			setPageComplete(true);
		}
		else if (btnExistingSentenceLayer.getSelection()) {
			setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType.EXISTING_LAYER);
			if (!layerCombo.getText().equals(NONE)) {
				setErrorMessage(null);
				setPageComplete(true);
			}
			else {
				setErrorMessage("Please select the layer that holds the sentence span.");
				setPageComplete(false);
			}
		}
		else {
			setPageComplete(false);
		}
	}

	public void setRadioSelection(Button selectComposite) {
		for (Button c : new ArrayList<Button>(Arrays.asList(btnPredefinedOpenNLP, btnUseOwnApache, btnUseBreakIterator, btnUseThirdpartyDetector, btnExistingSentenceLayer))) {
			if (c.equals(selectComposite)) {
				c.setSelection(true);
				updatePageComplete();
			}
			else {
				c.setSelection(false);
				updatePageComplete();
			}
		}
	}

	/**
	 * @return the sentenceDetectorTypeToUse
	 */
	public SentenceDetectionService.SentenceDetectorType getSentenceDetectorTypeToUse() {
		return sentenceDetectorTypeToUse;
	}

	/**
	 * @param sentenceDetectorTypeToUse
	 *            the sentenceDetectorTypeToUse to set
	 */
	public void setSentenceDetectorTypeToUse(SentenceDetectionService.SentenceDetectorType sentenceDetectorTypeToUse) {
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

	/**
	 * @return the thirdPartyCombo
	 */
	public Combo getThirdPartyCombo() {
		return thirdPartyCombo;
	}

	/**
	 * @return the layerCombo
	 */
	public Combo getLayerCombo() {
		return layerCombo;
	}

	/**
	 * @return the layerList
	 */
	public ArrayList<SLayer> getLayerList() {
		return layerList;
	}

}
