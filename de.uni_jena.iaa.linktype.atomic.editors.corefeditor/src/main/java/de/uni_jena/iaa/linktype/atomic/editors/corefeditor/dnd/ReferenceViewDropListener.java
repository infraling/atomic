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
import org.eclipse.swt.events.TypedEvent;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.ReferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.SDocumentGraphDecorator;

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
		Object input = viewer.getInput();
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
		ReferenceModel model = null;
		SSpan span = null;
		if (input instanceof ReferenceModel) {
			model = (ReferenceModel) input; 
			model.addReference(reference);
			reference.setName("New referent " + (model.getReferences().indexOf(reference) + 1));
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
			int numberOfReferenceParticipation = 0;
			for (SProcessingAnnotation proAnno : span.getSProcessingAnnotations()) {
				if (proAnno.getQName().split("ATOMIC::")[0].startsWith("REFERENT_NAME")) {
					numberOfReferenceParticipation++;
				}
			}
			span.createSProcessingAnnotation("ATOMIC", "REFERENT_NAME" + (numberOfReferenceParticipation + 1), reference.getName());
			if (!reference.getSpans().contains(span)) {
				reference.getSpans().add(span);
				TreeMap<Integer, SSpan> spanMap = reference.getSpanMap();
				spanMap.put(start, span);
			}
			else System.err.println("Span already exists in reference. " + this.getClass());
		}
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
			System.err.println("-------- " + span.getSId());
			int numberOfReferenceParticipation = 0;
			for (SProcessingAnnotation proAnno : span.getSProcessingAnnotations()) {
				if (proAnno.getQName().split("ATOMIC::")[0].startsWith("REFERENT_NAME")) {
					numberOfReferenceParticipation++;
				}
			}
			span.createSProcessingAnnotation("ATOMIC", "REFERENT_NAME" + (numberOfReferenceParticipation + 1), ((Reference) getTarget()).getName());
			Reference reference = (Reference) getTarget();
			TreeMap<Integer, SSpan> spanMap = reference.getSpanMap();
			if (!reference.getSpans().contains(span)) {
				reference.getSpans().add(span);
				spanMap.put(start, span);
				createRelations(span, (Reference) getTarget(), spanMap);
			}
			else {
				System.err.println("Span already exists in reference. " + this.getClass()); // FIXME: Doesn't work yet as there's always a new span added
			}
			viewer.setInput(input);
			viewer.setExpandedState(getTarget(), true);
			getEditor().setDirty(true);
			getEditor().fireDirtyProperty();
			return false;
		}
		return false;
	}

	private void createRelations(SSpan span, Reference reference, TreeMap<Integer,SSpan> map) {
		System.err.println(reference.getSpans().size());
//		if (refherence.getSpans().size() > 1) {
//			TreeMap<Integer, SSpan> map = reference.getSpanMap();
			for (Entry<Integer, SSpan> entry : map.entrySet()) {
				if (entry.getValue() == span) {
					SDocumentGraph graph = span.getSDocumentGraph();
					Integer higherKey = map.higherKey(entry.getKey());
					SSpan sourceSpan = null;
					if (higherKey != null) {
						sourceSpan = map.get(higherKey);
					}
					Integer lowerKey = map.lowerKey(entry.getKey());
					SSpan targetSpan = null;
					if (lowerKey != null) {
						targetSpan = map.get(lowerKey);
					}
					String targetAnno = null;
					String sourceAnno = null;
					if (sourceSpan != null) {
						SPointingRelation sourceToThis = SaltFactory.eINSTANCE.createSPointingRelation();
						EList<Edge> sourceOutEdges;
						SNode oldTarget = null; 
						Node oldTargetsTarget = null; 
						if ((sourceOutEdges = graph.getOutEdges(sourceSpan.getSId())).size() != 0) {// I.e., span is already source for another span
							ArrayList<Edge> edgesToRemove = new ArrayList<Edge>();
							for (Edge outEdge : sourceOutEdges) {
								if (outEdge instanceof SPointingRelation) {
									if (((SPointingRelation) outEdge).getSAnnotation("ATOMIC::coref") != null) {
										sourceAnno = ((SPointingRelation) outEdge).getSAnnotation("ATOMIC::coref").getValueString();
										if (((SPointingRelation) outEdge).getSTarget() != null) {
//											graph.removeEdge(outEdge);
											edgesToRemove.add(outEdge);
										}
//										edge.setTarget(span);
//										EList<Edge> oldOutEdges;
//										if ((oldOutEdges = graph.getOutEdges(oldTarget.getSId())).size() != 0) {// I.e., span is already source for another span)
//											for (Edge oldOutEdge : oldOutEdges) {
//												if (((SPointingRelation) oldOutEdge).getSAnnotation("ATOMIC::coref") != null) {
//													oldTargetsTarget = oldOutEdge.getTarget();
//												}
//											}
//										}
									}
								}
							}
							for (Edge edge : edgesToRemove) {
								graph.removeEdge(edge);
							}
						}
						EList<Edge> targetInEdges;
						if (targetSpan != null) {
							if ((targetInEdges = graph.getInEdges(targetSpan.getSId())).size() != 0) {// I.e., span is already target for another span
								ArrayList<Edge> edgesToRemove2 = new ArrayList<Edge>();
								for (Edge inEdge : targetInEdges) {
									if (inEdge instanceof SPointingRelation) {
										if (((SPointingRelation) inEdge).getSAnnotation("ATOMIC::coref") != null) {
											targetAnno  = ((SPointingRelation) inEdge).getSAnnotation("ATOMIC::coref").getValueString();
											if (((SPointingRelation) inEdge).getSSource() != null) { 
	//											graph.removeEdge(inEdge);
												edgesToRemove2.add(inEdge);
											}
										}
									}
								}
								for (Edge edge : edgesToRemove2) {
									graph.removeEdge(edge);
								}
							}
						}
						sourceToThis.setSSource(sourceSpan);
						sourceToThis.setSTarget(span);
						if (sourceAnno != null) {
							sourceToThis.createSAnnotation("ATOMIC", "coref", sourceAnno);
						}
						else {
							sourceToThis.createSAnnotation("ATOMIC", "coref", "[edit]");
						}
						span.getSDocumentGraph().addEdge(sourceToThis);
					}
					if (targetSpan != null) {
						SPointingRelation thisToTarget = SaltFactory.eINSTANCE.createSPointingRelation();
						thisToTarget.setSSource(span);
						thisToTarget.setSTarget(targetSpan);
						if (targetAnno != null) {
							thisToTarget.createSAnnotation("ATOMIC", "coref", targetAnno);
						}
						else {
							thisToTarget.createSAnnotation("ATOMIC", "coref", "[edit]");
						}
						span.getSDocumentGraph().addEdge(thisToTarget);
					}
				}
			}
//		}
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
