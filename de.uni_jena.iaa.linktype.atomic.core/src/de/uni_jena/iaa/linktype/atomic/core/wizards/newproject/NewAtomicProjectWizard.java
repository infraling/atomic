/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.wizards.newproject;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
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
import de.uni_jena.iaa.linktype.atomic.core.utils.AtomicProjectUtils;

/**
 * @author stephan
 * 
 * TODO: Introduce OperationCanceledExceptions!
 *
 */
public class NewAtomicProjectWizard extends Wizard implements INewWizard {
	
	private NewAtomicProjectWizardDetailsPage page;
	private Object[] typedTokenizerToUse;
	public IFile projectIFile;
	
	/**
	 * 
	 */
	public NewAtomicProjectWizard() {
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// Do nothing.
	}
	
//	@Override
//	public boolean canFinish(){
//		System.err.println("NOT EMPTY: " + (!page.getCorpusText().isEmpty()));
//		System.err.println("NOT DEFAULT: " + (!page.getCorpusText().equals(Messages.AtomicProjectBasicsWizardPage_CORPUS_TEXTFIELD_DEFAULT)));
//		return ((!page.getCorpusText().isEmpty()) && (!page.getCorpusText().equals(Messages.AtomicProjectBasicsWizardPage_CORPUS_TEXTFIELD_DEFAULT)));
//	}

	@Override
	public void addPages() {
		setWindowTitle("New Atomic project");
		page = new NewAtomicProjectWizardDetailsPage();
		addPage(page);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
            getContainer().run(false, true, new AtomicProjectCreationRunnable());
        } 
		catch (Exception e) {
			e.printStackTrace();
        }
		return true;
	}
	
	private final class AtomicProjectCreationRunnable implements IRunnableWithProgress {
		
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			setNeedsProgressMonitor(true);
			String projectName = page.getTxtProjectName().getText();
			String corpusText = page.getCorpusText();
			String tokenizerName = page.getComboTokenizer().getText();
//			Tokenizer tokenizer = null;
			
			monitor.beginTask("Creating project from corpus", /*100*/IProgressMonitor.UNKNOWN);
			
			// Task 1 : Create IProject
			monitor.subTask("Creating Atomic project (1/7)");
			IProject iProject = createIProject(projectName);
			monitor.worked(1);
			
			// Task 2 : select tokenizer
			monitor.subTask("Collecting project metadata (2/7)");
			typedTokenizerToUse = getTokenizerInstance(tokenizerName);
			monitor.worked(1);
			
			// Task 2 : Create SaltProject and structure
			monitor.subTask("Creating data model (3/7)");
			SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
			saltProject.setSName(projectName);
			createSaltProjectStructure(saltProject);
			monitor.worked(1);
			
			// Task 3 : Populate project with corpus
			monitor.subTask("Populating data model with corpus data (4/7)");
			SDocumentGraph sDocumentGraph = createSDocumentStructure(saltProject, corpusText);
			monitor.worked(1);
			
			// Task 4 : Tokenize
			monitor.subTask("Tokenizing corpus with selected tokenizer (tokenization) (5/7)");
			tokenizeCorpusString(sDocumentGraph, typedTokenizerToUse);
			monitor.worked(1);
			
			// Task 5 : Annotate tokens with text meta info 
			monitor.subTask("Tokenizing corpus with selected tokenizer (token annotation) (6/7)");
			writeTextToTokens(sDocumentGraph);
			monitor.worked(1);
			
			// Task 6 : Annotate tokens with text meta info 
			monitor.subTask("Serializing project (7/7)");
			try {
				NewAtomicProjectWizard.this.projectIFile = saveSaltProjectToIPproject(saltProject, iProject, projectName, sDocumentGraph);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			monitor.worked(1);
			
			// Finished
			monitor.done();
		}

		private IFile saveSaltProjectToIPproject(SaltProject saltProject, IProject iProject, String projectName, SDocumentGraph sDocumentGraph) throws CoreException {
			File iProjectLocation = new File(iProject.getLocation().toString());
			URI uri = URI.createFileURI(iProjectLocation.getAbsolutePath());
			saltProject.saveSaltProject(uri);
			/* TODO Save SaltProject to DOT
			 * There seems to be a concurrency problem with saving to the same URI!
			 */
			iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
			IFile iFile = iProject.getFile("SaltProject.salt");
			return iFile;
			
		}

		private void writeTextToTokens(SDocumentGraph sDocumentGraph) {
			String text = sDocumentGraph.getSTextualDSs().get(0).getSText();
			EList<SToken> tokens = sDocumentGraph.getSTokens();
			for (SToken token : tokens) {
				for (Edge edge: sDocumentGraph.getOutEdges(token.getSId())) {
					if (edge instanceof STextualRelation) {
						STextualRelation textualRelation = (STextualRelation) edge;
						String primaryText = text.substring(textualRelation.getSStart(), textualRelation.getSEnd());
						token.createSProcessingAnnotation("ATOMIC", "TOKEN_TEXT", primaryText);
					}
				}
			}
		}

		private void tokenizeCorpusString(SDocumentGraph sDocumentGraph, Object[] typedTokenizerToUse) {
			if (typedTokenizerToUse[0] instanceof Tokenizer) {
				Tokenizer tokenizer = (Tokenizer) typedTokenizerToUse[0];
				tokenizer.setsDocumentGraph(sDocumentGraph);
				tokenizer.tokenize(sDocumentGraph.getSTextualDSs().get(0));
			}
		}

		private SDocumentGraph createSDocumentStructure(SaltProject saltProject, String corpusText) {
			// SDocumentGraph
			SDocumentGraph sDocumentGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
			sDocumentGraph.setSName("document_graph");
			SDocument sDocument = saltProject.getSCorpusGraphs().get(0).getSDocuments().get(0);
			sDocument.setSDocumentGraph(sDocumentGraph);
			
			// STextualDS
			STextualDS sTextualDS = SaltFactory.eINSTANCE.createSTextualDS();
			sTextualDS.setSText(corpusText);
			sDocument.getSDocumentGraph().addSNode(sTextualDS);
			return sDocumentGraph;
		}

		private void createSaltProjectStructure(SaltProject saltProject) {
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
			
//			return sDocument;
		}

		/**
		 * @param tokenizerName
		 */
		private Object[] getTokenizerInstance(String tokenizerName) {
			Object[] typedTokenizer = new Object[2];
			Object tokenizer = null;
			for (int i = 0; i < AtomicProjectUtils.getTokenizerNames().length; i++) {
				if (AtomicProjectUtils.getTokenizerNames()[i].equals(tokenizerName))
					tokenizer = AtomicProjectUtils.getTokenizers()[i];
					typedTokenizer[0] = tokenizer;
					typedTokenizer[1] = tokenizer.getClass();
			}
			if (typedTokenizer[0] == null) {
				ErrorDialog.openError(getShell(), "Tokenizer not found!", "Sorry, it appears that the tokenizer you have chosen is not available.", Status.OK_STATUS);
			}
			return typedTokenizer;
		}

		private IProject createIProject(String projectName) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject iProject = root.getProject(projectName);
			try {
				iProject.create(null);
				iProject.open(null);
//						addAtomicProjectNatureToIProject(iProject); // FIXME Throws CoreException
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return iProject;
		}
	}
	
}
