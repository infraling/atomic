/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.editors.text.TextEditor;

import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.documentb.CorpusDocumentProvider;

/**
 * @author Stephan Druskat
 *
 */
public class CoreferenceEditor extends TextEditor {
	
	private SourceViewer viewer;

	/**
	 * 
	 */
	public CoreferenceEditor() {
		super();
		setDocumentProvider(new CorpusDocumentProvider());
		this.setEditorContextMenuId("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.contextmenu");
	}
	
	@Override
	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		viewer = new SourceViewer(parent, ruler /* TODO: introduce IVerticalRuler later*/, getOverviewRuler() /* TODO check if OverviewRuler is helpful*/, isOverviewRulerVisible() /* TODO check if needed later */, SWT.WRAP | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI | SWT.READ_ONLY);
		getSourceViewerDecorationSupport(viewer);
		
		// Add menus
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuManager, viewer);
		
		// Add listeners
		viewer.addSelectionChangedListener(new CorefEditorSelectionChangedListener());
				
		return viewer;
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

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// TODO Auto-generated method stub

		}
	}

}
