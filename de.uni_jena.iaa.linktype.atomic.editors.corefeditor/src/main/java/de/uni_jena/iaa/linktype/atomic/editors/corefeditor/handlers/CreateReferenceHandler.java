/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorSashContainer;
import org.eclipse.ui.internal.EditorStack;
import org.eclipse.ui.internal.ILayoutContainer;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSashContainer;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PartStack;
import org.eclipse.ui.internal.WorkbenchPage;

import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextOverlappingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SProcessingAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document.SDocumentProvider;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceView;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;

/**
 * @author Stephan Druskat
 * 
 */
public class CreateReferenceHandler extends CreateMarkableHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		super.execute(event);
		// Open reference editor, add span, give reference a name
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchPage page = workbench.getActiveWorkbenchWindow()
				.getActivePage();
		if (page != null) {
			IEditorPart editor = page.getActiveEditor();
			SDocumentGraph graph = null;
			SDocument sDocument = null;
			URI graphURI = null;
			if (editor instanceof CoreferenceEditor) {
				sDocument = ((SDocumentProvider) ((CoreferenceEditor) editor)
						.getDocumentProvider()).getSDocument();
				graph = sDocument.getSDocumentGraph();
				graphURI = ((SDocumentProvider) ((CoreferenceEditor) editor)
						.getDocumentProvider()).getGraphURI();
			}
			try {
				// page.showView("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview");
				ReferenceModel model = constructNewReference(graph);
				addExistingSpansToModel(model, graph);
				if (sDocument != null) {
					model.setDocument(sDocument);
				}
				if (graphURI != null) {
					model.setGraphURI(graphURI);
				}
				IEditorPart referenceEditor = page
						.openEditor(model,
								"de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceeditor");
				page.activate(referenceEditor);
				if (referenceEditor != null) {
					splitEditorArea();
				}
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}

	private void addExistingSpansToModel(ReferenceModel model, SDocumentGraph graph) {
		ArrayList<SSpan> spansToAdd = new ArrayList<SSpan>(); 
//		Map<Integer, String> existingReferenceNames = new HashMap<Integer, String>();
		Set<String> nameSet = new HashSet<String>();
		int numberOfReferenceNamesIterator = 0;
		for (SSpan span : graph.getSSpans()) {
			for (Edge outEdge : graph.getOutEdges(span.getSId())) {
				if (outEdge instanceof SPointingRelation) {
					if (((SPointingRelation) outEdge).getSAnnotation("ATOMIC::coref") != null) {
						spansToAdd.add(span);
						for (SProcessingAnnotation refAnno : span.getSProcessingAnnotations()) {
							if (refAnno.getName().startsWith("REFERENT_NAME")) {
//								existingReferenceNames.put(numberOfReferenceNamesIterator++, refAnno.getValueString());
								nameSet.add(refAnno.getValueString());
							}
						}
					}
				}
			}
			for (Edge inEdge : graph.getInEdges(span.getSId())) {
				if (inEdge instanceof SPointingRelation) {
					if (((SPointingRelation) inEdge).getSAnnotation("ATOMIC::coref") != null) {
						spansToAdd.add(span);
						for (SProcessingAnnotation refAnno : span.getSProcessingAnnotations()) {
							if (refAnno.getName().startsWith("REFERENT_NAME")) {
//								existingReferenceNames.put(numberOfReferenceNamesIterator++, refAnno.getValueString());
								nameSet.add(refAnno.getValueString());
							}
						}
					}
				}
			}
		}
//		for (int s = 0; s < existingReferenceNames.size(); s++) {
//			System.err.println(s + " " + existingReferenceNames.get(Integer.valueOf(s)));
//			Reference reference = new Reference();
//			reference.setName(existingReferenceNames.get(s));
//			for (SSpan span : spansToAdd) {
//				for (SProcessingAnnotation nameAnno : span.getSProcessingAnnotations()) {
//					if (nameAnno.getValueString().startsWith("REFERENT_NAME")) {
//						if (nameAnno.getValueString().equals(reference.getName())) {
//							reference.addSpan(span);
//							System.err.println("ADD");
//						}
//					}
//				}
//			}
//			model.addReference(reference);
//		}
		for (String name : nameSet) {
			Reference reference = new Reference();
			reference.setName(name);
			EList<STYPE_NAME> refList = new BasicEList<STYPE_NAME>();
			refList.add(STYPE_NAME.SSPANNING_RELATION);
			for (SSpan span: spansToAdd) {
				for (SProcessingAnnotation anno : span.getSProcessingAnnotations()) {
					if (anno.getQName().split("ATOMIC::")[1].startsWith("REFERENT_NAME")) {
						if (anno.getValueString().equals(name)) {
							if (!reference.getSpans().contains(span)) {
								reference.addSpan(span);
								EList<SToken> tokens = graph.getOverlappedSTokens(span, refList);
								int start = graph.getSTextualDSs().get(0).getSText().length() - 1;
								refList.clear();
								refList.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
								for (SToken token : tokens) {
									SDataSourceSequence sequence = graph.getOverlappedDSSequences(token, refList).get(0);
									if (sequence.getSStart() < start) {
										start = sequence.getSStart();
									}
								}
								reference.getSpanMap().put(start, span);
							}
							else {
								System.err.println("Span already exists in reference. " + this.getClass());
							}
						}
					}
				}
			}
			model.addReference(reference);
		}
	}

	private ReferenceModel constructNewReference(SDocumentGraph graph) {
		ReferenceModel model = new ReferenceModel(graph);
//		Reference reference = new Reference();
//		// reference.addSpan(getSpan());
//		model.addReference(reference);
//		reference.setName("New referent " + (model.getReferences().indexOf(reference) + 1));
		return model;
	}

	private void splitEditorArea() {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IWorkbenchPart part = workbenchPage.getActivePart();
		PartPane partPane = ((PartSite) part.getSite()).getPane();
		LayoutPart layoutPart = partPane.getPart();

		IEditorReference[] editorReferences = workbenchPage
				.getEditorReferences();
		// Do it only if we have more that one editor
		if (editorReferences.length > 1) {
			// Get PartPane that correspond to the active editor
			PartPane currentEditorPartPane = ((PartSite) workbenchPage
					.getActiveEditor().getSite()).getPane();
			EditorSashContainer editorSashContainer = null;
			ILayoutContainer rootLayoutContainer = layoutPart.getContainer();
			if (rootLayoutContainer instanceof LayoutPart) {
				ILayoutContainer editorSashLayoutContainer = ((LayoutPart) rootLayoutContainer)
						.getContainer();
				if (editorSashLayoutContainer instanceof EditorSashContainer) {
					editorSashContainer = ((EditorSashContainer) editorSashLayoutContainer);
				}
			}
			/*
			 * Create a new part stack (i.e. a workbook) to home the
			 * currentEditorPartPane which hold the active editor
			 */
			PartStack newPart = createStack(editorSashContainer);
			editorSashContainer.stack(currentEditorPartPane, newPart);
			if (rootLayoutContainer instanceof LayoutPart) {
				ILayoutContainer cont = ((LayoutPart) rootLayoutContainer)
						.getContainer();
				if (cont instanceof PartSashContainer) {
					// "Split" the editor area by adding the new part
					((PartSashContainer) cont).add(newPart);
				}
			}
		}
	}

	/**
	 * A method to create a part stack container (a new workbook)
	 * 
	 * @param editorSashContainer
	 *            the <code>EditorSashContainer</code> to set for the returned
	 *            <code>PartStack</code>
	 * @return a new part stack container
	 */
	private PartStack createStack(EditorSashContainer editorSashContainer) {
		WorkbenchPage workbenchPage = (WorkbenchPage) PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		EditorStack newWorkbook = EditorStack.newEditorWorkbook(
				editorSashContainer, workbenchPage);
		return newWorkbook;
	}

}
