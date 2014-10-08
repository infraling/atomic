/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceModel extends SDocumentGraphDecorator {

	public ReferenceModel(SDocumentGraph decoratedGraph) {
		super(decoratedGraph);
	}

}
