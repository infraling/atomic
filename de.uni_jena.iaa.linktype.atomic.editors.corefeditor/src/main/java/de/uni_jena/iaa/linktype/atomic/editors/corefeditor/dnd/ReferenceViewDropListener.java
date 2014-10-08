/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceViewDropListener extends ViewerDropAdapter {

	private final CheckboxTreeViewer viewer;
	private Object target;

	public ReferenceViewDropListener(CheckboxTreeViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	@Override
	public void drop(DropTargetEvent event) {
		this.setTarget(determineTarget(event));
		int location = this.determineLocation(event);
		switch (location) {
		case 1:
			// "Dropped before the target ";
			break;
		case 2:
			// "Dropped after the target ";
			break;
		case 3:
			// "Dropped on the target ";
			break;
		case 4:
			// "Dropped into nothing ";
			performDrop(event.data, true);
			break;
		}
		super.drop(event);
	}

	private boolean performDrop(Object data, boolean createNewReference) {
		Reference reference = new Reference();
		reference.setName("New referent");
		Object input = viewer.getInput();
		ReferenceModel model = null;
		SSpan span = null;
		if (input instanceof ReferenceModel) {
			model = (ReferenceModel) input; 
			model.addReference(reference);
			SDocumentGraph graph = model.getDecoratedSDocumentGraph();
			STextualDS text = graph.getSTextualDSs().get(0);
			// create span
			EList<SToken> tokenListForSpan = new BasicEList<SToken>();
			EList<STYPE_NAME> textualRelations= new BasicEList<STYPE_NAME>();
			textualRelations.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
			int start = text.getSText().indexOf(data.toString());
			int end = start + data.toString().length();
			System.err.println("OPERATING ON " + text.getSText().substring(start, end));
			for (SToken token : graph.getSTokens()) {
				SDataSourceSequence sequence = graph.getOverlappedDSSequences(token, textualRelations).get(0);
				if (sequence.getSStart() >= start && sequence.getSEnd() <= (end)) {
					tokenListForSpan.add(token);
				}
			}
			span = graph.createSSpan(tokenListForSpan);
			reference.getSpans().add(span);
		}
		viewer.setInput(model);
		viewer.setExpandedState(reference, true);
		return false;
	}

	@Override
	public boolean performDrop(Object data) {
		ReferenceModel input = (ReferenceModel) viewer.getInput();
		if (getTarget() instanceof Reference) {
			SDocumentGraph graph = input.getDecoratedSDocumentGraph();
			STextualDS text = graph.getSTextualDSs().get(0);
			// create span
			EList<SToken> tokenListForSpan = new BasicEList<SToken>();
			EList<STYPE_NAME> textualRelations= new BasicEList<STYPE_NAME>();
			textualRelations.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
			int start = text.getSText().indexOf(data.toString());
			int end = start + data.toString().length();
			for (SToken token : graph.getSTokens()) {
				SDataSourceSequence sequence = graph.getOverlappedDSSequences(token, textualRelations).get(0);
				if (sequence.getSStart() >= start && sequence.getSEnd() <= (end)) {
					tokenListForSpan.add(token);
				}
			}
			SSpan span = graph.createSSpan(tokenListForSpan);
			((Reference) getTarget()).getSpans().add(span);
			viewer.setInput(input);
			viewer.setExpandedState(getTarget(), true);
			return false;
		}
		return false;
	}

	@Override
	public boolean validateDrop(Object target, int operation, TransferData transferType) {
		return true;
	}

	/**
	 * @return the target
	 */
	public Object getTarget() {
		return target;
	}

	/**
	 * @param target the target to set
	 */
	public void setTarget(Object target) {
		this.target = target;
	}

}
