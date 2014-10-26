/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document.SDocumentProvider;

/**
 * @author Stephan Druskat
 * 
 */
public class CoreferenceEditor extends TextEditor {

	private SourceViewer viewer;
	private final IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part == CoreferenceEditor.this) {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				if (page != null) {
					IEditorReference[] editorRefs = page.getEditorReferences();
					for (int i = 0; i < editorRefs.length; i++) {
						if (editorRefs[i].getId().equals("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceeditor")) {
							part = editorRefs[i].getPart(false);
							page.closeEditor((IEditorPart) part, true);
						}
					}
					getSite().getWorkbenchWindow().getPartService().removePartListener(this);
				}
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	/**
	 * 
	 */
	public CoreferenceEditor() {
		super();
		setDocumentProvider(new SDocumentProvider());
		this.setEditorContextMenuId("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.contextmenu");
	}

	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		viewer = new SourceViewer(parent, ruler /*
												 * TODO: introduce
												 * IVerticalRuler later
												 */, getOverviewRuler() /*
																		 * TODO
																		 * check
																		 * if
																		 * OverviewRuler
																		 * is
																		 * helpful
																		 */, isOverviewRulerVisible() /*
																									 * TODO
																									 * check
																									 * if
																									 * needed
																									 * later
																									 */, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		getSourceViewerDecorationSupport(viewer);
		IPreferenceStore store = getPreferenceStore();
		store.setValue(PREFERENCE_TEXT_DRAG_AND_DROP_ENABLED, true);

		// Add menus
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);

		// Add listeners
		viewer.addSelectionChangedListener(new CorefEditorSelectionChangedListener());
		enableSanityChecking(false);
		this.getSite().getWorkbenchWindow().getPartService().addPartListener(this.partListener);

		return viewer;
	}

	@Override
	protected void sanityCheckState(IEditorInput input) {
		// Do nothing
	}

	@Override
	protected void initializeEditor() {
		super.initializeEditor();
		setSourceViewerConfiguration(new CorefSourceViewerConfiguration());
	}

	// Make the editor read-only
	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public boolean isEditorInputModifiable() {
		return false;
	}

	@Override
	public boolean isEditorInputReadOnly() {
		return true;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	// EO Make the editor read-only

	// Hide the context menu
	@Override
	protected boolean isEditorInputIncludedInContextMenu() {
		return false;
	}

	// EO Hide the context menu (above)

	public SourceViewer getViewer() {
		return viewer;
	}

	public void setViewer(SourceViewer viewer) {
		this.viewer = viewer;
	}

	/**
	 * @author Stephan Druskat
	 * 
	 */
	public class CorefEditorSelectionChangedListener implements ISelectionChangedListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
		 * (org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// selectAllFullWordsInSelection(event);
		}

		/**
		 * @param event
		 */
		private void selectAllFullWordsInSelection(SelectionChangedEvent event) {
			int offset = -1, length = -1;
			ISelection selection = event.getSelection();
			if (selection instanceof TextSelection) {
				offset = ((TextSelection) selection).getOffset();
				length = ((TextSelection) selection).getLength();
			}
			ISelectionProvider provider = event.getSelectionProvider();
			String text = null;
			if (provider instanceof SourceViewer) {
				IDocument document = ((SourceViewer) provider).getDocument();
				text = document.get();
			}
			// Find bordering whitespaces
			String textBeforeOffset = text.substring(0, offset);
			int positionWhitespaceBeforeOffset = 0; // Re-usable in case the
													// selection includes the
													// first word, i.e., no
													// whitespace is found
			for (int i = positionWhitespaceBeforeOffset; i < textBeforeOffset.length(); i++) {
				if (Character.isWhitespace(textBeforeOffset.charAt(i))) {
					positionWhitespaceBeforeOffset = i;
				}
			}
			int positionWhitespaceAfterSelection = offset + length;
			for (int i = positionWhitespaceAfterSelection; i < text.length(); i++) {
				if (Character.isWhitespace(text.charAt(i))) {
					positionWhitespaceAfterSelection = i;
					break;
				}
			}
			IEditorPart activeEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			if (activeEditor instanceof CoreferenceEditor && ((CoreferenceEditor) activeEditor).getViewer() instanceof SourceViewer) {
				((ITextEditor) activeEditor).selectAndReveal(positionWhitespaceBeforeOffset + 1, (positionWhitespaceAfterSelection - positionWhitespaceBeforeOffset));
			}
		}
	}

}
