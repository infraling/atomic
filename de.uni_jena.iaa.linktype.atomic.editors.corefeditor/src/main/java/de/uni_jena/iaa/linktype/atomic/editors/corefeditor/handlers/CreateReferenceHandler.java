/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
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

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
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
				if (sDocument != null) {
					model.setDocument(sDocument);
				}
				if (graphURI != null) {
					model.setGraphURI(graphURI);
				}
				IEditorPart referenceEditor = page
						.openEditor(model,
								"de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceeditor");
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

	private ReferenceModel constructNewReference(SDocumentGraph graph) {
		ReferenceModel model = new ReferenceModel(graph);
		Reference reference = new Reference();
		// reference.addSpan(getSpan());
		reference.setName("New referent");
		model.addReference(reference);
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
