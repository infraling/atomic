/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.sentenceview;

import java.util.Arrays;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * @author Stephan Druskat
 *
 */
public class SentenceLabelProvider extends LabelProvider implements ITableColorProvider {
	
	private SentenceView sentenceView;

	/**
	 * @param sentenceView
	 */
	public SentenceLabelProvider(SentenceView sentenceView) {
		this.sentenceView = sentenceView;
	}
	
	@Override
	public String getText(Object element) {
		return retrieveSentenceFromSpan((SSpan) element);
	}
	
	protected String retrieveSentenceFromSpan(SSpan span) {
		EList<SToken> overlappedTokens = span.getSDocumentGraph().getOverlappedSTokens(span, new BasicEList<STYPE_NAME>(Arrays.asList(STYPE_NAME.SSPANNING_RELATION)));
		EList<SToken> sortedTokens = span.getSDocumentGraph().getSortedSTokenByText(overlappedTokens);
		int sentenceIndex = span.getSDocumentGraph().getSLayer(ModelRegistry.SENTENCE_LAYER_SID).getAllIncludedNodes().indexOf(span);
		String sentence = "";
		for (int i = 0; i < sortedTokens.size(); i++) {
			String tokenText = getTokenText(sortedTokens.get(i));
			if (i == 0) {
				sentence = sentence + "[" + sentenceIndex + "] " + tokenText;
			}
			else {
				sentence = sentence + " " + tokenText;
			}
		}
		if (!sentence.isEmpty()) {
			// Add whitespace at the end to make sure the whole sentence is displayed
			// and not cut off by table border
			return sentence + " ";
		}
		return null;
	}
	
	private String getTokenText(SToken token) {
		for (Edge edge : token.getSDocumentGraph().getOutEdges(token.getSId())) {
			if (edge instanceof STextualRelation) {
				STextualRelation textualRelation = (STextualRelation) edge;
				return token.getSDocumentGraph().getSTextualDSs().get(0).getSText().substring(textualRelation.getSStart(), textualRelation.getSEnd());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	@Override
	public Color getForeground(Object element, int columnIndex) {
//		if (sentenceView.getLinkedSentences().contains(element)) {
//			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
//		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	@Override
	public Color getBackground(Object element, int columnIndex) {
		if (sentenceView.getLinkedSentences().contains(element)) {
			return Display.getCurrent().getSystemColor(SWT.COLOR_RED);
		}
		return null;
	}

}
