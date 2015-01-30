/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.projects;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.util.Span;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import de.uni_jena.iaa.linktype.atomic.core.update.AtomicAutoUpdateJob;
import de.uni_jena.iaa.linktype.atomic.core.utils.AtomicProjectUtils;

/**
 * @author Stephan Druskat
 * 
 *         TODO: Introduce OperationCanceledExceptions!
 * 
 */
public class NewAtomicProjectWizard extends Wizard implements INewWizard {

	private static final Logger log = LoggerFactory.getLogger(AtomicAutoUpdateJob.class);

	private static final Map<String, String> openNLPModels;
	static {
		Map<String, String> aMap = new HashMap<String, String>();
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.DANISH, "/da-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.GERMAN, "/de-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.ENGLISH, "/en-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.FRENCH, "/fr-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.ITALIAN, "/it-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.DUTCH, "/nl-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.PORTUGUESE, "/pt-sent.bin");
		aMap.put(NewAtomicProjectWizardSentenceDetectionPage.SWEDISH, "/se-sent.bin");
		openNLPModels = Collections.unmodifiableMap(aMap);
	}

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
		} catch (Exception e) {
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
			detectSentences(sDocumentGraph, iProject);
			monitor.worked(1);
			log.info("Detected sentences.");

			// Annotate tokens with text meta info
			monitor.subTask("Serializing project");
			try {
				saveSaltProjectToIPproject(saltProject, iProject, projectName, sDocumentGraph);
			} catch (CoreException e) {
				e.printStackTrace();
			}
			monitor.worked(1);
			log.info("Saved SaltProject to IProject.");

			// Finished
			monitor.done();
		}

		/**
		 * @param sDocumentGraph
		 * @param iProject 
		 * @throws FileNotFoundException
		 */
		private void detectSentences(SDocumentGraph sDocumentGraph, IProject iProject) {
			switch (sentenceDetectionPage.getSentenceDetectorTypeToUse()) {
			case OPENNLP:
				String modelFileName = openNLPModels.get(sentenceDetectionPage.getPredefinedOpenNLPCombo().getText());
				SentenceModel model = null;
				InputStream modelIn = null;
				modelIn = getClass().getResourceAsStream(modelFileName);
				try {
					model = new SentenceModel(modelIn);
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (modelIn != null) {
						try {
							modelIn.close();
						} catch (IOException e) {
						}
					}
				}
				SentenceDetectorME sentenceDetector = new SentenceDetectorME(model);
				String[] sentences = sentenceDetector.sentDetect(sDocumentGraph.getSTextualDSs().get(0).getSText());
				Span[] sentenceSpans = sentenceDetector.sentPosDetect(sDocumentGraph.getSTextualDSs().get(0).getSText());
				
				// Serialization and deserialization example!
				FileOutputStream fos;
				try {
					File iProjectLocation = new File(iProject.getLocation().toString());
					String fileName = sDocumentGraph.getSDocument().getSName() + "_sentences.atomic";
					fos = new FileOutputStream(iProjectLocation.getAbsolutePath() + "/" + fileName);
					ObjectOutputStream oos = new ObjectOutputStream(fos);
					oos.writeObject(sentences);
					oos.close();
					fos.close();
					iProject.refreshLocal(IProject.DEPTH_INFINITE, null);
					IFile iFile = iProject.getFile(fileName);
					
					// Deserialize
					 FileInputStream inputFileStream = new FileInputStream(new File(iFile.getLocationURI()));
				      ObjectInputStream objectInputStream = new ObjectInputStream(inputFileStream);
				      String[] deserSentences = (String[]) objectInputStream.readObject();
				      objectInputStream.close();
				      inputFileStream.close();
				      for (int i = 0; i < deserSentences.length; i++) {
						System.err.println(">>> " + deserSentences[i]);
					}
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CoreException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// Serialization and deserialization example!

				break;
			case OPENNLP_CUSTOM:

				break;
			case REGEX:

				break;
			case THIRDPARTY:

				break;

			default:
				break;
			}

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
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return iProject;
		}
	}
}
