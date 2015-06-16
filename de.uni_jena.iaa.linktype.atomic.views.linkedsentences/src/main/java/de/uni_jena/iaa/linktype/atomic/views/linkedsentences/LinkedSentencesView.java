/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.linkedsentences;

import java.util.Arrays;
import java.util.HashSet;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.GRAPH_TRAVERSE_TYPE;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphService;
import de.uni_jena.iaa.linktype.atomic.core.corpus.LinkedSentencesTraverser;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * @author Stephan Druskat
 * 
 */
public class LinkedSentencesView extends ViewPart {

	private HashSet<SSpan> linkedSentences;
	private SSpan sentenceSpan;
	private Text sentenceSpanText = null;

	ISelectionListener listener = new ISelectionListener() {
		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection incomingSelection) {
			if (part instanceof EditorPart) {
				// Ignore selection, since the GraphEditor, e.g., can select
				// single Spans as well
			}
			if (incomingSelection instanceof IStructuredSelection) {
				IStructuredSelection selection = (IStructuredSelection) incomingSelection;
				if (selection.getFirstElement() instanceof IStructuredSelection) { // SentenceView
																					// wraps
																					// twice
					if (((IStructuredSelection) selection.getFirstElement()).size() == 1 && ((IStructuredSelection) selection.getFirstElement()).getFirstElement() instanceof SSpan) {
						setSentenceSpan((SSpan) ((IStructuredSelection) selection.getFirstElement()).getFirstElement());
						Display.getCurrent().asyncExec(new Runnable() {
							@Override
							public void run() {
								setLinkedSentences(traverseSpanForLinkedSentences(getSentenceSpan()));
								getSentenceSpanText().setText(retrieveSentenceFromSpan(getSentenceSpan()) + "\n is linked to\n" + getLinkedSentences());
							}
						});
					}
				}
			}
		}
	};

	/**
	 * 
	 */
	public LinkedSentencesView() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		getSite().getPage().addSelectionListener(listener);
		setSentenceSpanText(new Text(parent, SWT.MULTI | SWT.READ_ONLY | SWT.WRAP));
		getSentenceSpanText().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// l = new Label(parent, SWT.NONE);
		// l.setText("Nothing to do");
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
			// Add whitespace at the end to make sure the whole sentence is
			// displayed
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

	private void setLinkedSentences(HashSet<SSpan> linkedSentences) {
		this.linkedSentences = linkedSentences;
	}

	private HashSet<SSpan> traverseSpanForLinkedSentences(SSpan sentenceSpan) {
		EList<SToken> tokens = GraphService.getOverlappedTokens(sentenceSpan);
		LinkedSentencesTraverser traverser = new LinkedSentencesTraverser();
		traverser.setTokenSet(new HashSet<SToken>(tokens));
		sentenceSpan.getSDocumentGraph().traverse(tokens, GRAPH_TRAVERSE_TYPE.BOTTOM_UP_BREADTH_FIRST, "linkedSentences", traverser, false);
		return traverser.getLinkedSentences();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the sentenceSpan
	 */
	public SSpan getSentenceSpan() {
		return sentenceSpan;
	}

	/**
	 * @param sentenceSpan
	 *            the sentenceSpan to set
	 */
	public void setSentenceSpan(SSpan sentenceSpan) {
		this.sentenceSpan = sentenceSpan;
	}

	/**
	 * @return the linkedSentences
	 */
	public HashSet<SSpan> getLinkedSentences() {
		return linkedSentences;
	}

	/**
	 * @return the sentenceSpanText
	 */
	public Text getSentenceSpanText() {
		return sentenceSpanText;
	}

	/**
	 * @param sentenceSpanText the sentenceSpanText to set
	 */
	public void setSentenceSpanText(Text sentenceSpanText) {
		this.sentenceSpanText = sentenceSpanText;
	}

}
