/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.ui.part.FileEditorInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;

/**
 * A registry that keeps track of the {@link SDocumentGraph}s that have already
 * been resolved and provides access methods to resolved {@link SDocumentGraph}s
 * as well as functionality to load unresolved ones (via a {@link ModelLoader}).
 * 
 * @author Stephan Druskat
 * 
 */
public class ModelRegistry {

	private static final Logger log = LoggerFactory.getLogger(ModelRegistry.class);

	private static Map<IFile, SDocumentGraph> documentGraphs = new HashMap<IFile, SDocumentGraph>();
	private static Map<IFile, Integer> editorsOnDocumentIFile = new HashMap<IFile, Integer>();
	private static Map<SDocumentGraph, ArrayList<SSpan>> sentenceMap = new HashMap<SDocumentGraph, ArrayList<SSpan>>();

	public static final String SENTENCE_LAYER_SID = "ATOMIC::SENTENCES";
	public static final String SENTENCE_LAYER_SNAME = "Sentences (detected)";
	public static final String NO_LAYERS_SELECTED = "No layers selected!";
	public static final String NO_SENTENCES_SELECTED = "No sentences selected!";

	/**
	 * Returns an {@link SDocumentGraph} that has been loaded from an
	 * {@link IFile}. The {@link SDocumentGraph} is either loaded on-the-fly if
	 * no editor has been registered for its {@link IFile} yet, or the existing
	 * model instance is got from the registry and passed to the editor. The
	 * registry {@link Map}s are updated accordingly.
	 * 
	 * @param iFile The {@link IFile} that the registered editor has been passed
	 * as {@link FileEditorInput}
	 * @return the resolved {@link SDocumentGraph}
	 */
	public static SDocumentGraph getModel(IFile iFile) {
		if (documentGraphs.get(iFile) != null) {
			// Graph exists, so simply return it!
			return documentGraphs.get(iFile);
		}
		log.info("Getting model for IFile {}.", iFile);
		SDocumentGraph documentGraph = null;
		IFile documentIFile;

		IFile saltProjectIFile;
		SaltProject saltProject;
		if (iFile.getName().equals(SaltFactory.FILE_SALT_PROJECT)) {
			log.info("IFile is a SaltProject file.");
			saltProjectIFile = iFile;
			saltProject = ModelLoader.loadSaltProjectFromSaltProjectIFile(saltProjectIFile);
			documentIFile = ModelLoader.getDocumentIFileFromDialog(saltProjectIFile, saltProject);
			log.info("Document to open: {}.", documentIFile);
			documentGraph = documentGraphs.get(documentIFile);
			if (documentGraph == null) {
				documentGraph = loadAndRegisterSDocumentGraph(saltProject, documentIFile);
			}
			else {
				increaseEditorCountForSDocument(documentIFile);
			}
			// If graph contains sentence layer, add sentence spans
			// to sentence map
			if (documentGraph.getSLayer(SENTENCE_LAYER_SID) != null) {
				ArrayList<SSpan> sentenceList = new ArrayList<SSpan>();
				for (Node node : documentGraph.getSLayer(SENTENCE_LAYER_SID).getAllIncludedNodes()) {
					if (node instanceof SSpan) {
						sentenceList.add((SSpan) node);
					}
				}
				sentenceMap.put(documentGraph, sentenceList);
			}
		}
		else if (iFile.getName().endsWith(SaltFactory.FILE_ENDING_SALT)) {
			documentIFile = iFile;
			documentGraph = null;

			log.info("IFile is an SDocument file.");
			saltProjectIFile = ModelLoader.getSaltProjectIFileFromDocumentIFile(documentIFile);
			saltProject = ModelLoader.loadSaltProjectFromSaltProjectIFile(saltProjectIFile);
			log.info("Document to open: {}.", documentIFile);
			documentGraph = documentGraphs.get(documentIFile);
			if (documentGraph == null) {
				log.info("Document not loaded yet, loading now.");
				documentGraph = loadAndRegisterSDocumentGraph(saltProject, documentIFile);
			}
			else {
				log.info("Document already loaded.");
				increaseEditorCountForSDocument(documentIFile);
			}
		}
		else {
			// Should never be called as extension point filters on file ending
		}
		log.info("Returning SDocumentGraph {} for use as editor input.", documentGraph);
		return documentGraph;
	}

	/**
	 * @param documentIFile The
	 */
	private static void increaseEditorCountForSDocument(IFile documentIFile) {
		log.info("SDocumentGraph for IFile {} is already registered!", documentIFile);
		Integer editorCount = editorsOnDocumentIFile.get(documentIFile);
		editorCount++;
		log.info("Registering another editor for SDocumentGraph {}. This is editor number {}.", documentIFile, editorCount);
		editorsOnDocumentIFile.put(documentIFile, editorCount++);
	}

	/**
	 * @param saltProject
	 * @param documentIFile
	 * @return
	 */
	private static SDocumentGraph loadAndRegisterSDocumentGraph(SaltProject saltProject, IFile documentIFile) {
		SDocumentGraph documentGraph;
		log.info("SDocumentGraph for IFile {} is not resolved yet. Resolving SDocumentGraph now.", documentIFile);
		documentGraph = ModelLoader.loadDocumentGraphFromDocumentIFile(documentIFile, saltProject);
		log.info("SDocumentGraph for IFile {} is resolved now as {}.", documentIFile, documentGraph);
		log.info("Registering editor as first editor for for SDocumentGraph {}.", documentIFile);
		documentGraphs.put(documentIFile, documentGraph);
		editorsOnDocumentIFile.put(documentIFile, 1);
		return documentGraph;
	}

	/**
	 * Deregisters an editor from an instance of @see {@link SDocumentGraph},
	 * i.e., decreases the count of editors working on this model by 1. If the
	 * editor count for a model is 0, the model instance is disposed of to free
	 * memory.
	 * 
	 * @param iFile
	 */
	public static void deregisterEditor(IFile iFile) {
		if (iFile.getName().endsWith(SaltFactory.FILE_ENDING_SALT)) {
			log.info("Deregistering editor from SDocument {}.", iFile);
			Integer editorCount = editorsOnDocumentIFile.get(iFile);
			editorCount--;
			if (editorCount == 0) {
				log.info("There are no longer any editors registered for SDocument {}. Removing SDocumentGraph from memory.", iFile);
				documentGraphs.remove(iFile);
			}
			else {
				log.info("Editor de-registered from SDocument {}. There are now {} editors registered for this SDocument.", iFile, editorCount);
				editorsOnDocumentIFile.put(iFile, editorCount);
			}
		}
		else {
			// Should never be called
		}
	}

	/**
	 * @return the sentenceMap
	 */
	public static Map<SDocumentGraph, ArrayList<SSpan>> getSentenceMap() {
		return sentenceMap;
	}

}
