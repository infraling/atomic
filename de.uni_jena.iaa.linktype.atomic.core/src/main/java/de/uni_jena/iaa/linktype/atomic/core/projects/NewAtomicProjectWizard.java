/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.corpus.SentenceDetectionService;
import de.uni_jena.iaa.linktype.atomic.core.utils.AtomicProjectUtils;

/**
 * @author Stephan Druskat
 * 
 *         TODO: Introduce OperationCanceledExceptions!
 * 
 */
public class NewAtomicProjectWizard extends Wizard implements INewWizard {

	private static final Logger log = LoggerFactory.getLogger(NewAtomicProjectWizard.class);

	private NewAtomicProjectWizardDetailsPage detailsPage;
	private Object[] typedTokenizerToUse;

	private NewAtomicProjectWizardSentenceDetectionPage sentenceDetectionPage;

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

	@Override
	public void addPages() {
		setWindowTitle("New Atomic project");
		detailsPage = new NewAtomicProjectWizardDetailsPage();
		addPage(detailsPage);
		sentenceDetectionPage = new NewAtomicProjectWizardSentenceDetectionPage();
		addPage(sentenceDetectionPage);
	}

	/*
	 * (non-Javadoc)
	 * 
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
			String projectName = detailsPage.getTxtProjectName().getText();
			String corpusText = detailsPage.getCorpusText();
			String tokenizerName = detailsPage.getComboTokenizer().getText();

			monitor.beginTask("Creating project from corpus", /* 100 */IProgressMonitor.UNKNOWN);

			// Create IProject
			monitor.subTask("Creating Atomic project");
			IProject iProject = createIProject(projectName);
			monitor.worked(1);
			log.info("Created new IProject");

			// select tokenizer
			monitor.subTask("Collecting project metadata");
			typedTokenizerToUse = getTokenizerInstance(tokenizerName);
			monitor.worked(1);
			log.info("Collected project-specific creation settings (tokenizer to use, etc.).");

			// Create SaltProject and structure
			monitor.subTask("Creating data model");
			SaltProject saltProject = SaltFactory.eINSTANCE.createSaltProject();
			saltProject.setSName(projectName);
			createSaltProjectStructure(saltProject);
			monitor.worked(1);
			log.info("Created data model.");

			// Populate project with corpus
			monitor.subTask("Populating data model with corpus data");
			SDocumentGraph sDocumentGraph = createSDocumentStructure(saltProject, corpusText);
			monitor.worked(1);
			log.info("Populated data model with corpus data.");

			// Tokenize
			monitor.subTask("Tokenizing corpus with selected tokenizer (tokenization)");
			tokenizeCorpusString(sDocumentGraph, typedTokenizerToUse);
			monitor.worked(1);
			log.info("Tokenized corpus.");

			// Annotate tokens with text meta info
			monitor.subTask("Tokenizing corpus with selected tokenizer (token annotation)");
			writeTextToTokens(sDocumentGraph);
			monitor.worked(1);
			log.info("Wrote token texts to tokens.");

			// Detect sentence
			monitor.subTask("Detecting sentences in corpus text");
			TreeRangeSet<Integer> sentenceRanges = detectSentences(sDocumentGraph, iProject);
			writeSentencesToModel(sDocumentGraph, sentenceRanges);
			monitor.worked(1);
			log.info("Detected sentences.");

			// Annotate tokens with text meta info
			monitor.subTask("Serializing project");
			try {
				saveSaltProjectToIPproject(saltProject, iProject, projectName, sDocumentGraph);
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
			monitor.worked(1);
			log.info("Saved SaltProject to IProject.");

			// Finished
			monitor.done();
		}

		/**
		 * @param sDocumentGraph
		 * @param sentenceRanges
		 */
		private void writeSentencesToModel(SDocumentGraph sDocumentGraph, TreeRangeSet<Integer> sentenceRanges) {
			SLayer sentenceLayer = SaltFactory.eINSTANCE.createSLayer();
			sentenceLayer.setSName("sentences");
			sDocumentGraph.addSLayer(sentenceLayer);
			EList<SToken> sentenceTokens = new BasicEList<SToken>();
			for (Range<Integer> sentenceRange : sentenceRanges.asRanges()) {
				sentenceTokens.clear();
				for (SToken token : sDocumentGraph.getSTokens()) {
					for (Edge edge : sDocumentGraph.getOutEdges(token.getSId())) {
						if (edge instanceof STextualRelation) {
							Range<Integer> range = Range.closed(((STextualRelation) edge).getSStart(), ((STextualRelation) edge).getSEnd());
							if (sentenceRange.encloses(range)) {
								sentenceTokens.add(token);
							}
						}
					}
				}
				SSpan sentenceSSpan = sDocumentGraph.createSSpan(sentenceTokens);
				sentenceLayer.getSNodes().add(sentenceSSpan);
			}
		}

		/**
		 * @param sDocumentGraph
		 * @param iProject
		 * @throws FileNotFoundException
		 */
		private TreeRangeSet<Integer> detectSentences(SDocumentGraph sDocumentGraph, IProject iProject) {
			String corpusText = sDocumentGraph.getSTextualDSs().get(0).getSText();
			TreeRangeSet<Integer> sentenceSet = TreeRangeSet.create();
			switch (sentenceDetectionPage.getSentenceDetectorTypeToUse()) {
			case OPENNLP:
				sentenceSet = SentenceDetectionService.detectSentencesWithOpenNLP(sentenceDetectionPage.getPredefinedOpenNLPCombo().getText(), corpusText);
				break;
			case OPENNLP_CUSTOM:
				sentenceSet = SentenceDetectionService.detectSentencesWithCustomOpenNLP(sentenceDetectionPage.getTextUseOwnApache().getText(), corpusText);
				break;
			case BREAK_ITERATOR:
				sentenceSet = SentenceDetectionService.detectSentencesWithBreakIterator(sentenceDetectionPage.getLocaleCombo().getText(), corpusText);
				break;
			case THIRDPARTY:
				sentenceSet = SentenceDetectionService.detectSentencesWithThirdPartyExtension(sentenceDetectionPage.getThirdPartyCombo().getText(), corpusText);
				break;

			default:
				break;
			}
			return sentenceSet;

		}

		private IFile saveSaltProjectToIPproject(SaltProject saltProject, IProject iProject, String projectName, SDocumentGraph sDocumentGraph) throws CoreException {
			File iProjectLocation = new File(iProject.getLocation().toString());
			URI uri = URI.createFileURI(iProjectLocation.getAbsolutePath());
			saltProject.saveSaltProject(uri);
			/*
			 * TODO Save SaltProject to DOT There seems to be a concurrency
			 * problem with saving to the same URI!
			 */
			iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
			IFile iFile = iProject.getFile("SaltProject.salt");
			return iFile;

		}

		private void writeTextToTokens(SDocumentGraph sDocumentGraph) {
			String text = sDocumentGraph.getSTextualDSs().get(0).getSText();
			EList<SToken> tokens = sDocumentGraph.getSTokens();
			for (SToken token : tokens) {
				for (Edge edge : sDocumentGraph.getOutEdges(token.getSId())) {
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
				sDocumentGraph.tokenize();
			}
		}

		private SDocumentGraph createSDocumentStructure(SaltProject saltProject, String corpusText) {
			// SDocumentGraph
			SDocumentGraph sDocumentGraph = SaltFactory.eINSTANCE.createSDocumentGraph();
			sDocumentGraph.setSName("document_graph");
			SDocument sDocument = saltProject.getSCorpusGraphs().get(0).getSDocuments().get(0);
			sDocument.setSDocumentGraph(sDocumentGraph);

			// STextualDS
			sDocumentGraph.createSTextualDS(corpusText);
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

			// return sDocument;
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
				AtomicProjectUtils.addAtomicProjectNatureToIProject(iProject);
			}
			catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return iProject;
		}
	}

}
