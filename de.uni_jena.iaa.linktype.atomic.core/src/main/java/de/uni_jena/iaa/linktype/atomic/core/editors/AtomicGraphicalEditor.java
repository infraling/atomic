/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.editors;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelLoader;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;

/**
 * An abstraction for GEF-based graphical editors in Atomic.
 * 
 * This class provides model handling for {@link SDocumentGraph}s, 
 * so subclasses do not need to take care of it (they can simply call super.method()).
 * 
 * @author Stephan Druskat
 *
 */
public abstract class AtomicGraphicalEditor extends GraphicalEditorWithFlyoutPalette {
	
	private static final Logger log = LoggerFactory.getLogger(AtomicGraphicalEditor.class);

	/**
	 * The model {@link SDocumentGraph} for this editor instance
	 */
	private SDocumentGraph graph;

	/**
	 * Adds auto-adding of model to editor.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithFlyoutPalette#initializeGraphicalViewer()
	 */
	@Override
	protected void initializeGraphicalViewer() {
		super.initializeGraphicalViewer();
		if (getEditorInput() instanceof FileEditorInput) {
			FileEditorInput editorInput = (FileEditorInput) getEditorInput();
			log.info("Getting model from {}.", editorInput.getFile());
			setGraph(ModelRegistry.getModel(editorInput.getFile()));
			log.info("Setting graph as contents in GraphicalViewer: {}.", getGraph());
			getGraphicalViewer().setContents(getGraph());
		}
		else {
			log.info("EditorInput is not an instance of FileEditorInput. Aborting and informing user.");
			MessageDialog.openError(Display.getCurrent().getActiveShell(), "Action not applicable", "The Annotation Graph Editor cannot process the input you have selected.");
		}
	}
	
	/**
	 * Implements default doSave() behaviour for Atomic editors.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		IFile documentIFile = ((FileEditorInput) getEditorInput()).getFile();
		log.info("Saving document {}.", documentIFile);
		SDocument document = ModelLoader.getSDocumentFromSDocumentIFile(((FileEditorInput) getEditorInput()).getFile());
		document.setSDocumentGraph(getGraph());
		URI uri = URI.createFileURI(new File(documentIFile.getLocation().toString()).getAbsolutePath());
		document.saveSDocumentGraph(uri);
		getCommandStack().markSaveLocation();
		firePropertyChange(PROP_DIRTY);
	}
	
	/**
	 * Adds deregistration of the model when disposing the editor.
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.gef.ui.parts.GraphicalEditor#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		if (getEditorInput() instanceof FileEditorInput) {
			ModelRegistry.deregisterEditor(((FileEditorInput) getEditorInput()).getFile());
		}
	}

	/**
	 * @return the model {@link SDocumentGraph} for this editor
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the {@link SDocumentGraph} to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
