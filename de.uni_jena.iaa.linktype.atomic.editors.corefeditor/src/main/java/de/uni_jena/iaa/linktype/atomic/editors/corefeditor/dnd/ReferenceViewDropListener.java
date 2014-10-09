/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.ReferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceViewDropListener extends ViewerDropAdapter {

	private final CheckboxTreeViewer viewer;
	private Object target;
	private ReferenceEditor editor;

	public ReferenceViewDropListener(CheckboxTreeViewer viewer) {
		super(viewer);
		this.viewer = viewer;
	}

	public ReferenceViewDropListener(CheckboxTreeViewer treeViewer, ReferenceEditor referenceEditor) {
		this(treeViewer);
		this.editor = referenceEditor;
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
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		TextSelection selection = null;
		int start = -1;
		int end = -1;
		if (editor instanceof CoreferenceEditor) {
			selection = (TextSelection) ((CoreferenceEditor) editor).getSelectionProvider().getSelection();
			start = selection.getOffset();
			end = start + selection.getLength();
		}
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
			for (SToken token : graph.getSTokens()) {
				SDataSourceSequence sequence = graph.getOverlappedDSSequences(token, textualRelations).get(0);
				if (sequence.getSStart() >= start && sequence.getSEnd() <= (end)) {
					tokenListForSpan.add(token);
				}
			}
			span = graph.createSSpan(tokenListForSpan);
			reference.getSpans().add(span);
		}
		TreeMap<Integer, SSpan> spanMap = reference.getSpanMap();
		spanMap.put(start, span);
		viewer.setInput(model);
		viewer.setExpandedState(reference, true);
		getEditor().setDirty(true);
		getEditor().fireDirtyProperty();
		return false;
	}

	@Override
	public boolean performDrop(Object data) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		TextSelection selection = null;
		int start = -1;
		int end = -1;
		if (editor instanceof CoreferenceEditor) {
			selection = (TextSelection) ((CoreferenceEditor) editor).getSelectionProvider().getSelection();
			start = selection.getOffset();
			end = start + selection.getLength();
		}
		ReferenceModel input = (ReferenceModel) viewer.getInput();
		if (getTarget() instanceof Reference) {
			SDocumentGraph graph = input.getDecoratedSDocumentGraph();
			STextualDS text = graph.getSTextualDSs().get(0);
			// create span
			EList<SToken> tokenListForSpan = new BasicEList<SToken>();
			EList<STYPE_NAME> textualRelations= new BasicEList<STYPE_NAME>();
			textualRelations.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
			for (SToken token : graph.getSTokens()) {
				SDataSourceSequence sequence = graph.getOverlappedDSSequences(token, textualRelations).get(0);
				if (sequence.getSStart() >= start && sequence.getSEnd() <= (end)) {
					tokenListForSpan.add(token);
				}
			}
			SSpan span = graph.createSSpan(tokenListForSpan);
			((Reference) getTarget()).getSpans().add(span);
			TreeMap<Integer, SSpan> spanMap = ((Reference) getTarget()).getSpanMap();
			spanMap.put(start, span);
			viewer.setInput(input);
			viewer.setExpandedState(getTarget(), true);
			getEditor().setDirty(true);
			getEditor().fireDirtyProperty();
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

	/**
	 * @return the editor
	 */
	public ReferenceEditor getEditor() {
		return editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(ReferenceEditor editor) {
		this.editor = editor;
	}

}
