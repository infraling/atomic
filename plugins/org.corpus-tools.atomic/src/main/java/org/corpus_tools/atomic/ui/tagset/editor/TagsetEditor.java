/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.editor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.exceptions.AtomicGeneralException;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.impl.TagsetFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetEditor extends EditorPart {
	
	private static final Logger log = LogManager.getLogger(TagsetEditor.class); 
	
	private SCorpus corpus;
	private Tagset tagset;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		if (!(input instanceof FileEditorInput)) {
			log.error("Input for editors of type {} must be of type {}, but the provided input is of type {}!", this.getClass().getName(), FileEditorInput.class.getName(), input.getClass().getName(), new AtomicGeneralException());
		}
		else {
			String filePath = ((FileEditorInput) input).getPath().makeAbsolute().toOSString();
			log.trace("Input for editor {} is file '{}'.", this.getClass().getName(), filePath);
			IProject iProject = ((FileEditorInput) input).getFile().getProject();
			
			// Get corpus for tagset via SaltProject
			IFile saltProjectResource = null;
			try {
				for (IResource member : iProject.members()) {
					if (member instanceof IFile && member.getName().equalsIgnoreCase(DocumentGraphEditor.SALT_PROJECT_FILE_NAME)) { // FIXME Externalize and treat for case, etc
						saltProjectResource = (IFile) member;
					}
				}
			}
			catch (CoreException e) {
				log.error("An error occurred getting the Salt project file.", e);
			}
			String projectFilePath = saltProjectResource.getRawLocation().toOSString();
			SaltProject saltProject = SaltFactory.createSaltProject();
			saltProject.loadCorpusStructure(URI.createFileURI(projectFilePath));
			Assert.isTrue(saltProject.getCorpusGraphs().size() == 1, "Atomic cannot currently work with Salt projects that contain more than one corpus graph.");
			Assert.isTrue(saltProject.getCorpusGraphs().get(0).getCorpora().size() == 1, "Atomic cannot currently work with Salt projects containins more than one corpus.");
			corpus = saltProject.getCorpusGraphs().get(0).getCorpora().get(0);
			log.trace("Corpus for tagset has been determined as {}.", corpus);
			
			// Load tagset
			long tagsetFileSize = 0;
			try {
				tagsetFileSize = Files.size(Paths.get(filePath));
				log.trace("Tagset file {} has size {}.", filePath, String.valueOf(tagsetFileSize));
			}
			catch (IOException e) {
				log.warn("Failed to calculate tagset file size for {}.", filePath, e);
			}
			if (tagsetFileSize == 0) {
				tagset = TagsetFactory.createTagset(corpus, corpus.getName());
			}
			else {
				tagset = TagsetFactory.load(URI.createFileURI(filePath));
			}
			if (tagset == null) {
				log.error("Could not read tagset from tagset file {}.", filePath);
			}
			log.info("Loaded tagset {} ({}) from {}.", tagset, tagset.getName(), filePath);
			setPartName("Tagset \"" + tagset.getName() + "\"");
			// Set up editor for automatic context switches on activation/deactivation 
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new PartContextListener(site.getId(), site.getPluginId()));
		}


	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
