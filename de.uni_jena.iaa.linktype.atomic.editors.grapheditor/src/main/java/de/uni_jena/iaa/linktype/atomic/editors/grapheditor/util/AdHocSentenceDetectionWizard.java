/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.jface.wizard.Wizard;

import com.google.common.collect.TreeRangeSet;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.corpus.SentenceDetectionService;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * @author Stephan Druskat
 * 
 */
public class AdHocSentenceDetectionWizard extends Wizard {

	private SDocumentGraph graph;
	private AdHocSentenceDetectionPage page;

	/**
	 * @param sDocumentGraph
	 * 
	 */
	public AdHocSentenceDetectionWizard(SDocumentGraph sDocumentGraph) {
		super();
		this.setGraph(sDocumentGraph);
		setNeedsProgressMonitor(false);
	}

	@Override
	public void addPages() {
		page = new AdHocSentenceDetectionPage("Sentence detection", getGraph());
		addPage(page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		TreeRangeSet<Integer> sentenceRanges = detectSentences();
		if (sentenceRanges != null) {
			SentenceDetectionService.writeSentencesToModel(getGraph(), sentenceRanges);
		}
		return true;
	}

	/**
	 * @param graph2
	 * @return
	 */
	private TreeRangeSet<Integer> detectSentences() {
		String corpusText = getGraph().getSTextualDSs().get(0).getSText();
		TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
		switch (page.getSentenceDetectorTypeToUse()) {
		case OPENNLP:
			sentenceSet = SentenceDetectionService.detectSentencesWithOpenNLP(page.getPredefinedOpenNLPCombo().getText(), corpusText);
			break;
		case OPENNLP_CUSTOM:
			sentenceSet = SentenceDetectionService.detectSentencesWithCustomOpenNLP(page.getTextUseOwnApache().getText(), corpusText);
			break;
		case BREAK_ITERATOR:
			sentenceSet = SentenceDetectionService.detectSentencesWithBreakIterator(page.getLocaleCombo().getText(), corpusText);
			break;
		case THIRDPARTY:
			sentenceSet = SentenceDetectionService.detectSentencesWithThirdPartyExtension(page.getThirdPartyCombo().getText(), corpusText);
			break;
		case EXISTING_LAYER:
			sentenceSet = null;
			SLayer existingLayer = getGraph().getSLayer(page.getLayerList().get(page.getLayerCombo().getSelectionIndex()).getSId());
			if (page.getLayerOverwriteCombo().getText().equals(SentenceDetectionService.RENAME)) {
				existingLayer.setSId(ModelRegistry.SENTENCE_LAYER_SID);
				existingLayer.setSName(ModelRegistry.SENTENCE_LAYER_SNAME);
			}
			else if (page.getLayerOverwriteCombo().getText().equals(SentenceDetectionService.COPY)) {
				SLayer duplicateLayer = SaltFactory.eINSTANCE.createSLayer();
				copyLayer(existingLayer, duplicateLayer);
			}
			break;

		default:
			break;
		}
		return sentenceSet;
	}

	/**
	 * @param existingLayer
	 * @param duplicateLayer
	 */
	private void copyLayer(SLayer existingLayer, SLayer duplicateLayer) {
		duplicateLayer.setSId(ModelRegistry.SENTENCE_LAYER_SID);
		duplicateLayer.setSName(ModelRegistry.SENTENCE_LAYER_SNAME);
		getGraph().addSLayer(duplicateLayer);
		for (Node node : existingLayer.getAllIncludedNodes()) {
			if (node instanceof SSpan) {
				duplicateLayer.getSNodes().add((SSpan) node);
			}
		}
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph
	 *            the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
