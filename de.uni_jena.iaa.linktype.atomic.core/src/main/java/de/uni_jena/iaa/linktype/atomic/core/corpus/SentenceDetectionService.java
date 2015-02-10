/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.BreakIterator;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;
import de.uni_jena.iaa.linktype.atomic.core.projects.NewAtomicProjectWizardSentenceDetectionPage;

/**
 * @author Stephan Druskat
 *
 */
public class SentenceDetectionService {

	private static final String EXTENSION_PROPERTY_CLASS = "class";

	public enum SentenceDetectorType {
		OPENNLP, OPENNLP_CUSTOM, BREAK_ITERATOR, THIRDPARTY, EXISTING_LAYER
	}

	public static final String DANISH = "Danish", GERMAN = "German",
			ENGLISH = "English", FRENCH = "French", ITALIAN = "Italian",
			DUTCH = "Dutch", PORTUGUESE = "Portuguese", SWEDISH = "Swedish";
	
	public static final String RENAME = "Rename and use existing layer", COPY = "Copy existing layer";
	
	private static final Map<String, String> openNLPModels;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put(DANISH, "/da-sent.bin");
		aMap.put(GERMAN, "/de-sent.bin");
		aMap.put(ENGLISH, "/en-sent.bin");
		aMap.put(FRENCH, "/fr-sent.bin");
		aMap.put(ITALIAN, "/it-sent.bin");
		aMap.put(DUTCH, "/nl-sent.bin");
		aMap.put(PORTUGUESE, "/pt-sent.bin");
		aMap.put(SWEDISH, "/se-sent.bin");
		openNLPModels = Collections.unmodifiableMap(aMap);
	}

	public static TreeRangeSet<Integer> detectSentencesWithOpenNLP(String selectedOpenNLPModule, String corpusText) {
		TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
		String modelFileName = openNLPModels.get(selectedOpenNLPModule);
		SentenceModel model = null;
		InputStream modelIn = null;
		modelIn = SentenceDetectionService.class.getResourceAsStream(modelFileName);
		try {
			model = new SentenceModel(modelIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (modelIn != null) {
				try {
					modelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
		Span[] sentenceSpans = sentenceDetector.sentPosDetect(corpusText);
		for (int i = 0; i < sentenceSpans.length; i++) {
			sentenceSet.add(Range.closed(sentenceSpans[i].getStart(), sentenceSpans[i].getEnd()));
		}
		return sentenceSet;
	}

	/**
	 * @param text
	 * @param corpusText
	 * @return
	 */
	public static TreeRangeSet<Integer> detectSentencesWithCustomOpenNLP(String customModelFileName, String corpusText) {
		TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
		SentenceModel customModel = null;
		InputStream customModelIn = null;
		System.err.println(customModelFileName);
		try {
			File file = new File(customModelFileName);
			customModelIn = new FileInputStream(file);
		}
		catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			customModel = new SentenceModel(customModelIn);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			if (customModelIn != null) {
				try {
					customModelIn.close();
				}
				catch (IOException e) {
				}
			}
		}
		SentenceDetectorME customSentenceDetector = new SentenceDetectorME(customModel);
		Span[] customSentenceSpans = customSentenceDetector.sentPosDetect(corpusText);
		for (int i = 0; i < customSentenceSpans.length; i++) {
			sentenceSet.add(Range.closed(customSentenceSpans[i].getStart(), customSentenceSpans[i].getEnd()));
		}
		return sentenceSet;
		
	}

	/**
	 * @param localeDisplayName
	 * @param corpusText
	 * @return
	 */
	public static TreeRangeSet<Integer> detectSentencesWithBreakIterator(String localeDisplayName, String corpusText) {
		TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
		BreakIterator sentenceIterator = BreakIterator.getSentenceInstance(LocaleProvider.getLocale(localeDisplayName));
		sentenceIterator.setText(corpusText);
		int start = sentenceIterator.first();
		int end = -1;
		while ((end = sentenceIterator.next()) != BreakIterator.DONE) {
			if (end == corpusText.length()) {
				// Otherwise last sentence delimiter will be trailing
				// without being part of a sentence:
				sentenceSet.add(Range.closed(start, end));
			}
			else {
				sentenceSet.add(Range.closed(start, (end - 1)));
			}
			start = end;
		}
		return sentenceSet;
	}

	/**
	 * @param text
	 * @param corpusText
	 * @return
	 */
	public static TreeRangeSet<Integer> detectSentencesWithThirdPartyExtension(String extensionName, String corpusText) {
		TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(NewAtomicProjectWizardSentenceDetectionPage.EXTENSION_ID);
		for (IConfigurationElement e : config) {
			if (e.getAttribute(NewAtomicProjectWizardSentenceDetectionPage.THIRDPARTY_DETECTOR_EXTENSION_NAME).equals(extensionName)) {
				try {
					SentenceDetector thirdPartyDetector = (SentenceDetector) e.createExecutableExtension(EXTENSION_PROPERTY_CLASS);
					sentenceSet = thirdPartyDetector.detectSentenceRanges(corpusText);
				}
				catch (CoreException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return sentenceSet;
	}

	/**
	 * @param sDocumentGraph
	 * @param sentenceRanges
	 */
	public static void writeSentencesToModel(SDocumentGraph sDocumentGraph, TreeRangeSet<Integer> sentenceRanges) {
		SLayer sentenceLayer = SaltFactory.eINSTANCE.createSLayer();
		sentenceLayer.setSName(ModelRegistry.SENTENCE_LAYER_SNAME);
		sentenceLayer.setSId(ModelRegistry.SENTENCE_LAYER_SID);
		sDocumentGraph.addSLayer(sentenceLayer);
		EList<SToken> sentenceTokens = new BasicEList<SToken>();
		for (Range<Integer> sentenceRange : sentenceRanges.asRanges()) {
			sentenceTokens.clear();
			for (SToken token : sDocumentGraph.getSTokens()) {
				for (Edge edge : sDocumentGraph.getOutEdges(token.getSId())) {
					if (edge instanceof STextualRelation) {
						Range<Integer> range = Range.closed(((STextualRelation) edge).getSStart(), ((STextualRelation) edge).getSEnd());
						if (sentenceRange.encloses(range)) {
							sentenceTokens.add(token);
						}
					}
				}
			}
			SSpan sentenceSSpan = sDocumentGraph.createSSpan(sentenceTokens);
			sentenceLayer.getSNodes().add(sentenceSSpan);
		}
	}

}
