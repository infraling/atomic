/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.sentenceview;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * @author Stephan Druskat
 *
 */
public class SentenceContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<SSpan> sentenceSpans = new ArrayList<SSpan>();
		if (inputElement instanceof SDocumentGraph) {
			SDocumentGraph graph = (SDocumentGraph) inputElement;
			SLayer sentenceLayer = graph.getSLayer(ModelRegistry.SENTENCE_LAYER_SID);
			if (sentenceLayer != null) {
				for (Node node : sentenceLayer.getAllIncludedNodes()) {
					if (node instanceof SSpan) {
						sentenceSpans.add((SSpan) node);
					}
				}
			}
			return sentenceSpans.toArray();
		}
		return null;
	}
}
