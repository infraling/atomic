/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.ListDialog;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;
import de.uni_jena.iaa.linktype.atomic.model.salt.project.AtomicProjectNature;

/**
 * @author Stephan Druskat
 *
 */
public class CreateSaltProjectHandler extends AbstractHandler {
	
	private String userEncoding = "UTF8";

	// TODO Refactor this class (to more than one class?) to maintain encapsulation and readability
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String desiredProjectName = getDesiredProjectNameFromDialog();
		IProject iProject = createIProject(desiredProjectName);
		File corpusSourceTextFile = getCorpusSourceTextFileFromDialog();
		userEncoding = getWantedEncodingFromDialog();
		IFile corpusSourceTextFileInWorkspace = copyCorpusSourceTextFileToWorkspace(iProject, corpusSourceTextFile);
		String corpusString = extractCorpusStringFromCorpusTextFile(corpusSourceTextFileInWorkspace);
		SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
		saltProject.setSName(desiredProjectName);
		createSaltProjectStructure(saltProject);
		SDocumentGraph sDocumentGraph = createSDocumentStructure(saltProject, corpusString);
		tokenizeCorpusString(sDocumentGraph);
		writeTextToTokens(sDocumentGraph);
		saveSaltProjectToIPproject(saltProject, desiredProjectName, sDocumentGraph);
		return null;
	}

	private String getWantedEncodingFromDialog() { // FIXME: General clear-up needed!
		// FIXME Add combobox to FileDialog rather than using extra dialog
		// FIXME Add guessEncoding
		// FIXME Clean up code and make safe
		String[] encodings = new String[] {"UTF8", "ASCII", "ISO8859_1", "ISO8859_2", "ISO8859_4", "ISO8859_5", "ISO8859_7", "ISO8859_9", "ISO8859_13", "ISO8859_15", "KOI8_R", "UTF-16", "UnicodeBigUnmarked", "UnicodeLittleUnmarked", "Cp1250", "Cp1251", "Cp1252", "Cp1253", "Cp1254", "Cp1257", "UnicodeBig", "UnicodeLittle"};
		String[] list = new String[] {"UTF-8 (Eight-bit UCS Transformation Format)", "US-ASCII (American Standard Code for Information Interchange)", "ISO-8859-1 (ISO 8859-1, Latin Alphabet No. 1)", "ISO-8859-2 (Latin Alphabet No. 2)", "ISO-8859-4 (Latin Alphabet No. 4)", "ISO-8859-5 (Latin/Cyrillic Alphabet)", "ISO-8859-7 (Latin/Greek Alphabet)", "ISO-8859-9 (Latin Alphabet No. 5)", "ISO-8859-13 (Latin Alphabet No. 7)", "ISO-8859-15 (Latin Alphabet No. 9)", "KOI8-R (KOI8-R, Russian)", "UTF-16 (Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark)", "UTF-16BE (Sixteen-bit Unicode Transformation Format, big-endian byte order)", "UTF-16LE (Sixteen-bit Unicode Transformation Format, little-endian byte order)", "windows-1250 (Windows Eastern European)", "windows-1251 (Windows Cyrillic)", "windows-1252 (Windows Latin-1)", "windows-1253 (Windows Greek)", "windows-1254 (Windows Turkish)", "windows-1257 (Windows Baltic)"};
		ArrayList<String> listList = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			listList.add(list[i]);
		}
		ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
		ld.setAddCancelButton(true);
		ld.setContentProvider(new ArrayContentProvider());
		ld.setLabelProvider(new LabelProvider());
		ld.setInput(list);
		ld.setInitialSelections(list);
		ld.setTitle("Select file encoding");
		ld.setAddCancelButton(false);
		if (ld.open() == Dialog.OK);
			String result = (String) ld.getResult()[0];
		int ind = listList.indexOf(result);
		return encodings[ind];

	}

	private void writeTextToTokens(SDocumentGraph sDocumentGraph) { // TODO: Solve with accessor!
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        try {
			progressMonitorDialog.run(true, true, new TokenPrimaryTextAnnotatorWithProgress(sDocumentGraph));
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private SDocumentGraph createSDocumentStructure(SaltProject saltProject, String corpusString) {
		// SDocumentGraph
		SDocumentGraph sDocumentGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
		sDocumentGraph.setSName("document_graph");
		SDocument sDocument = saltProject.getSCorpusGraphs().get(0).getSDocuments().get(0);
		sDocument.setSDocumentGraph(sDocumentGraph);
		
		// STextualDS
		STextualDS sTextualDS = SaltFactory.eINSTANCE.createSTextualDS();
		sTextualDS.setSText(corpusString);
		sDocument.getSDocumentGraph().addSNode(sTextualDS);
		return sDocumentGraph;
	}

	private SDocument createSaltProjectStructure(SaltProject saltProject) {
		// SCorpusGraph
		SCorpusGraph sCorpusGraph = SaltFactory.eINSTANCE.createSCorpusGraph();
		saltProject.getSCorpusGraphs().add(sCorpusGraph);
		
		// SCorpus
		SCorpus sCorpus = SaltFactory.eINSTANCE.createSCorpus();
		sCorpus.setSName("corpus");
		sCorpusGraph.addSNode(sCorpus);
		
		// SDocument
		SDocument sDocument = SaltFactory.eINSTANCE.createSDocument();
		sDocument.setSName("corpus_document");
		sCorpusGraph.addSDocument(sCorpus, sDocument);
		
		return sDocument;
	}

	private IFile copyCorpusSourceTextFileToWorkspace(IProject iProject, File corpusSourceTextFile) {
		IFile copiedFile = iProject.getFile(corpusSourceTextFile.getName());
		try {
			copiedFile.create(new FileInputStream(corpusSourceTextFile), true, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return copiedFile;
	}

	private String extractCorpusStringFromCorpusTextFile(IFile corpusSourceTextFileInWorkspace) {
		StringBuffer buffer = new StringBuffer();
	    try {
	        FileInputStream fis = new FileInputStream(corpusSourceTextFileInWorkspace.getLocation().toFile().getAbsolutePath());
	        InputStreamReader isr = new InputStreamReader(fis, userEncoding);
	        Reader in = new BufferedReader(isr);
	        int ch;
	        while ((ch = in.read()) > -1) {
	            buffer.append((char)ch);
	        }
	        in.close();
	        return buffer.toString();
	    } 
	    catch (IOException e) {
	        e.printStackTrace();
	        return null;
	    }
	}

	private File getCorpusSourceTextFileFromDialog() {
		FileDialog corpusTextFileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
		corpusTextFileDialog.setFilterExtensions(new String[] {"*.txt"});
		corpusTextFileDialog.setText("Select .txt file containing the corpus text.");
		String corpusTextFileString = corpusTextFileDialog.open();
		File corpusTextFile = new File(corpusTextFileString);
		return corpusTextFile;
	}

	private void saveSaltProjectToIPproject(SaltProject saltProject, String desiredProjectName, SDocumentGraph sDocumentGraph) {
		IProject iProject = ResourcesPlugin.getWorkspace().getRoot().getProject(desiredProjectName);
		File iProjectLocation = new File(iProject.getLocation().toString());
		URI uri = URI.createFileURI(iProjectLocation.getAbsolutePath());
		saltProject.saveSaltProject(uri);
		/* TODO Save SaltProject to DOT
		 * There seems to be a concurrency problem with saving to the same URI!
		 */
		
		
		try {
			iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO: Expand folder structure
	}

	private IProject createIProject(String desiredProjectName) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject iProject = root.getProject(desiredProjectName);
		try {
			iProject.create(null);
			iProject.open(null);
//			addAtomicProjectNatureToIProject(iProject); // FIXME Throws CoreException
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return iProject;
	}

	// TODO Implement correctly!
	private void addAtomicProjectNatureToIProject(IProject iProject) {
		try {
			if (!iProject.hasNature(AtomicProjectNature.NATURE_ID)) {
			    IProjectDescription description = iProject.getDescription();
			    String[] prevNatures = description.getNatureIds();
			    String[] newNatures = new String[prevNatures.length + 1];
			    System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			    newNatures[prevNatures.length] = AtomicProjectNature.NATURE_ID;
			    description.setNatureIds(newNatures);
			    iProject.setDescription(description, null);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	private String getDesiredProjectNameFromDialog() {
		String projectNameToUse;
		InputDialog projectNameInputDialog = new InputDialog(Display.getCurrent().getActiveShell(), "Enter a name for the project", "Please enter a name for the project.", null, new ProjectNameValidator());
		if (projectNameInputDialog.open() == Window.OK) {
			projectNameToUse = projectNameInputDialog.getValue();
		}
		else { // This will not be called unless both InputDialog and InputValidator fail.
			String randomString = Long.toHexString(Double.doubleToLongBits(Math.random()));
			MessageDialog.open(MessageDialog.ERROR, 
					Display.getCurrent().getActiveShell(), 
					"Using a random project name!", 
					"For some reason, the project name you want "+
					"to use could not be recorded correctly.\n" +
					"Instead, the project was given the random name\n" + randomString, 
					SWT.NONE);
			projectNameToUse = randomString;
		}
		return projectNameToUse;
	}

	private void tokenizeCorpusString(SDocumentGraph sDocumentGraph) {
		ProgressMonitorDialog progressMonitorDialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
        try {
			progressMonitorDialog.run(true, true, new TokenizerWithProgress(sDocumentGraph));
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class TokenizerWithProgress implements IRunnableWithProgress {

		private SDocumentGraph graph;

		public TokenizerWithProgress(SDocumentGraph sDocumentGraph) {
			this.graph = sDocumentGraph;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Creating tokens in Salt document.", IProgressMonitor.UNKNOWN);
			Tokenizer tokenizer = new Tokenizer();
			tokenizer.setsDocumentGraph(graph);
			if (monitor.isCanceled()) throw new OperationCanceledException();
			tokenizer.tokenize(graph.getSTextualDSs().get(0));
			monitor.done();
		}

	}
	
	/**
	 * @author Stephan Druskat
	 *
	 */
	public class TokenPrimaryTextAnnotatorWithProgress implements IRunnableWithProgress {

		private SDocumentGraph graph;

		public TokenPrimaryTextAnnotatorWithProgress(SDocumentGraph sDocumentGraph) {
			this.graph = sDocumentGraph;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.operation.IRunnableWithProgress#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			String text = graph.getSTextualDSs().get(0).getSText();
			EList<SToken> tokens = graph.getSTokens();
			int workLoad = tokens.size();
			monitor.beginTask("Setting the token text for the tokens.", workLoad);
			for (SToken token : tokens) {
				if (monitor.isCanceled()) throw new OperationCanceledException();
				for (Edge edge: graph.getOutEdges(token.getSId())) {
					if (edge instanceof STextualRelation) {
						STextualRelation textualRelation = (STextualRelation) edge;
						String primaryText = text.substring(textualRelation.getSStart(), textualRelation.getSEnd());
						token.createSProcessingAnnotation("ATOMIC", "TOKEN_TEXT", primaryText);
						monitor.worked(1);
					}
				}
			}
			monitor.done();
		}

	}
	
}

/**
 * @author Stephan Druskat
 *
 */
class ProjectNameValidator implements IInputValidator {

	/**
	 * Checks whether the input String equals the name of an existing project,
	 * ignoring the case, and whether it is an empty String ("").
	 */
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IInputValidator#isValid(java.lang.String)
	 */
	@Override
	public String isValid(String newText) {
		if (newText.equals("")) return "Project name must not be empty.";
		IProject[] existingProjectNamesList = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (IProject existingProject : existingProjectNamesList) {
			if (existingProject.getName().equalsIgnoreCase(newText))
				return "A project with the name " + newText + " already exists!";
		}
		return null;
	}

}
