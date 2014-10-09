/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model;

import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceModel extends SDocumentGraphDecorator implements IEditorInput {
	
	private final int id;
	private SDocument document;
	private URI graphURI;

	public ReferenceModel(SDocumentGraph decoratedGraph, int id) {
		super(decoratedGraph);
		this.id = id;
	}
	
	public ReferenceModel(SDocumentGraph decoratedGraph) {
		super(decoratedGraph);
		id = -1;
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		return String.valueOf(id);
	}

	@Override
	public IPersistableElement getPersistable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToolTipText() {
		return "Display referents and markables";
	}

	/**
	 * @return the document
	 */
	public SDocument getDocument() {
		return document;
	}

	/**
	 * @param document the document to set
	 */
	public void setDocument(SDocument document) {
		this.document = document;
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
