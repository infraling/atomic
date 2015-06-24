/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.model;

import java.io.File; 
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * A helper class that handles different aspects of model loading for
 * {@link SDocumentGraph}s.
 * 
 * @author Stephan Druskat
 * 
 */
public class ModelLoader {
	
	private static final Logger log = LogManager.getLogger(ModelLoader.class);

	/**
	 * Opens a {@link ListDialog} listing the {@link SDocument}s contained in the
	 * {@link SaltProject}s first (by index) {@link SCorpusGraph}, and gets
	 * the respective {@link IFile} containing the {@link SDocument} the user
	 * chose to open, which it returns.
	 * 
	 * @param saltProject The {@link SaltProject} that contains the corpus containing
	 * the {@link SDocument} whose {@link SDocumentGraph} should be resolved
	 * @return the {@link IFile} containing the {@link SDocument} whose {@link SDocumentGraph}
	 * should be resolved
	 */
	public static IFile getDocumentIFileFromDialog(IFile saltProjectIFile, SaltProject saltProject) {
		Shell shell = Display.getCurrent().getActiveShell();
		ListDialog dialog = new ListDialog(shell);
		dialog.setContentProvider(new ArrayContentProvider());
		dialog.setLabelProvider(new ArrayLabelProvider());
		dialog.setTitle("Select document");
		dialog.setMessage("Salt project files cannot be opened with the Annotation Graph Editor.\nPlease select the document you want to open.");
		ArrayList<String> documentNames = new ArrayList<String>();
		for (SDocument doc : saltProject.getSCorpusGraphs().get(0).getSDocuments()) {
			documentNames.add(doc.getSName());
		}
		dialog.setInput(documentNames);
		int documentIndex = dialog.open();
		String documentName = documentNames.get(documentIndex);
		if (saltProjectIFile.getName().equals(documentName)) {
			MessageDialog.openError(shell, "Bad document name", "The corpus document must not be named \"SaltProject.salt\"! Please rename it and retry.");
			return null;
		}
		return saltProjectIFile.getProject().getFile(documentName);
	}
	
	/**
	 * A very simple TableLabelProvider which simply returns the element (itself
	 * a String) in {@link #getColumnText(Object, int)}.
	 * 
	 * @author Stephan Druskat
	 * 
	 */
	private static class ArrayLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {return null;}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return (String) element;
		}
	}

	/**
	 * Loads a {@link SaltProject} from the provided {@link IFile}.
	 * 
	 * @param saltProjectIFile The {@link IFile} containing the {@link SaltProject} to load
	 * @return the {@link SaltProject} loaded from the {@link IFile}
	 */
	public static SaltProject loadSaltProjectFromSaltProjectIFile(IFile saltProjectIFile) {
		log.info("Attempting to load SaltProject from IFile {}.", saltProjectIFile);
		SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
		saltProject.loadSaltProject(URI.createFileURI(new File(saltProjectIFile.getLocation().toString()).getAbsolutePath()));
		return saltProject;
	}

	/**
	 * Loads a specific {@link SDocumentGraph} from an already resolved {@link SaltProject}
	 * by comparing it to the name of an {@link IFile}
	 * 
	 * @param documentIFile The {@link IFile} whose name is compared with the {@link SDocument}s
	 * @param saltProject The resolved {@link SaltProject} containing the {@link SDocument}s
	 * @return the {@link SDocumentGraph} that has been identified via the {@link IFile}, or null
	 */
	public static SDocumentGraph loadDocumentGraphFromDocumentIFile(IFile documentIFile, SaltProject saltProject) {
		log.info("Attempting to load SDocumentGraph with the help of the IFile {} and the SaltProject {}.", documentIFile, saltProject);
		String documentFileName = documentIFile.getName();
		if (documentFileName.contains("." + SaltFactory.FILE_ENDING_SALT)) {
			documentFileName = documentFileName.split("." + SaltFactory.FILE_ENDING_SALT)[0];
		}
		for (SDocument doc : saltProject.getSCorpusGraphs().get(0).getSDocuments()) {
			if ((doc.getSName()).equals(documentFileName)) {
				return doc.getSDocumentGraph();
			}
		}
		return null;
	}

	/**
	 * Returns the {@link IFile} containing the {@link SaltProject} in the
	 * {@link IProject} of which the provided {@link SDocument} {@link IFile}
	 * is a child.
	 * 
	 * @param documentIFile The {@link SDocument} {@link IFile} which is a child of the
	 * {@link IProject} whose single {@link SaltProject} {@link IFile} should be returned 
	 * @return the {@link SaltProject} {@link IFile}
	 */
	public static IFile getSaltProjectIFileFromDocumentIFile(IFile documentIFile) {
		return documentIFile.getProject().getFile(SaltFactory.FILE_SALT_PROJECT);
	}

	/**
	 * Loads an {@link SDocument} from the provided {@link IFile}.
	 * 
	 * @param documentFile The {@link IFile} containing the {@link SDocument} to be loaded
	 * @return the {@link SDocument} that has been loaded from the {@link IFile}, or null
	 */
	public static SDocument getSDocumentFromSDocumentIFile(IFile documentFile) {
		String documentFileName = documentFile.getName();
		IFile saltProjectIFile = getSaltProjectIFileFromDocumentIFile(documentFile);
		SaltProject saltProject = loadSaltProjectFromSaltProjectIFile(saltProjectIFile);
		if (documentFileName.contains("." + SaltFactory.FILE_ENDING_SALT)) {
			documentFileName = documentFileName.split("." + SaltFactory.FILE_ENDING_SALT)[0];
		}
		for (SDocument doc : saltProject.getSCorpusGraphs().get(0).getSDocuments()) {
			if (doc.getSName().equals(documentFileName)) {
				return doc;
			}
		}
		return null;
	}

}
