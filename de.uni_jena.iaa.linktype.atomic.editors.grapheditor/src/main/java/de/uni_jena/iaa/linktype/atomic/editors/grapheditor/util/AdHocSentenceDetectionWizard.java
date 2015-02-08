/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util;

import org.eclipse.jface.wizard.Wizard;

import com.google.common.collect.TreeRangeSet;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.core.corpus.SentenceDetectionService;

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

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		TreeRangeSet<Integer> sentenceRanges = detectSentences();
		SentenceDetectionService.writeSentencesToModel(getGraph(), sentenceRanges);
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
			System.err.println("SLAYER!");
			break;

		default:
			break;
		}
		return sentenceSet;	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
