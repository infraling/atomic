/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.eclipse.ui.IFileEditorInput;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class SDocumentProvider extends FileDocumentProvider {
	
	private SDocument sDocument;
	private SDocumentModel model;
	private IFileEditorInput input;
	private URI graphURI;

	@Override
	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = new Document();
		if (element instanceof IFileEditorInput) {
			setInput((IFileEditorInput) element);
			String fileName = getInput().getFile().getName();
			// FIXME Check if input is a SaltProject file, and if it is, display info about SaltProject
			if (fileName.equals("saltProject." + SaltFactory.FILE_ENDING_SALT)) {
				StringBuilder contents = buildSaltProjectInfo(getInput());
				document.set(contents.toString());
			}
			// Check if input is a .salt file, and if it is not a SaltProject,
			// i.e., if it is a persisted SDocument.
			else if (fileName.endsWith(SaltFactory.FILE_ENDING_SALT) && !fileName.equals("saltProject." + SaltFactory.FILE_ENDING_SALT)) {
				setSDocument(SaltFactory.eINSTANCE.createSDocument());
				setGraphURI(URI.createFileURI(getInput().getFile().getLocation().toOSString()));
				getSDocument().loadSDocumentGraph(getGraphURI());
				System.err.println(getSDocument());
				setModel(new SDocumentModel(getSDocument()));
				getModel().setGraphURI(getGraphURI());
				document.set(getModel().getCorpusText());
			}
			return document;
		}
		return null;
	}

	/**
	 * @param input
	 * @return
	 */
	private StringBuilder buildSaltProjectInfo(IFileEditorInput input) {
		final String nl = "\n";
		StringBuilder contents = new StringBuilder();
		SaltProject project = SaltFactory.eINSTANCE.createSaltProject();
		project.loadSaltProject(URI.createFileURI(input.getFile().getRawLocation().makeAbsolute().toOSString()));
		contents.append("Information about SaltProject " + project.getSName() + nl);
		int headlineLength = contents.length();
		for (int i = 1; i < headlineLength; i++) {
			contents.append("=");
		}
		contents.append(nl + nl + "Name: " + project.getSName());
		contents.append(nl + "Corpora: " + project.getSCorpusGraphs().get(0).getSCorpora().size() + nl + nl);
		EList<SDocument> sDocs = project.getSCorpusGraphs().get(0).getSDocuments();
		String sDocsString = nl + "| Documents (" + sDocs.size() + ") |";
		for (int j = 1; j < sDocsString.length(); j++)
			contents.append("-");
		contents.append(sDocsString + nl);
		for (int j = 1; j < sDocsString.length(); j++)
			contents.append("-");
		for (SDocument sDoc : sDocs) {
			SDocumentGraph graph = sDoc.getSDocumentGraph();
			int ind = ECollections.indexOf(sDocs, sDoc, 0);
			contents.append(nl + "(" + (ind + 1) + ") "+ sDoc.getSName());
			contents.append(nl + "Tokens: " + graph.getSTokens().size());
			contents.append(nl + "Nodes: " + graph.getSNodes().size());
		}
		return contents;
	}
	
	@Override
	protected IDocument createEmptyDocument() {
		return null;
	}

	/**
	 * @return the sDocument
	 */
	public SDocument getSDocument() {
		return sDocument;
	}

	/**
	 * @param sDocument the sDocument to set
	 */
	public void setSDocument(SDocument sDocument) {
		this.sDocument = sDocument;
	}

	/**
	 * @return the model
	 */
	public SDocumentModel getModel() {
		return model;
	}

	/**
	 * @param model the model to set
	 */
	public void setModel(SDocumentModel model) {
		this.model = model;
	}

	/**
	 * @return the input
	 */
	public IFileEditorInput getInput() {
		return input;
	}

	/**
	 * @param input the input to set
	 */
	public void setInput(IFileEditorInput input) {
		this.input = input;
	}

	/**
	 * @return the graphURI
	 */
	public URI getGraphURI() {
		return graphURI;
	}

	/**
	 * @param graphURI the graphURI to set
	 */
	public void setGraphURI(URI graphURI) {
		this.graphURI = graphURI;
	}

}
