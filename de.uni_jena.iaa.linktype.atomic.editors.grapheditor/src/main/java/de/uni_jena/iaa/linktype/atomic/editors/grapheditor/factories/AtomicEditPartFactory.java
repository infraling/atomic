/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.AnnotationPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GenericStringPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.StructurePart;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.TokenPart;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicEditPartFactory implements EditPartFactory {

	/* (non-Javadoc)
	 * @see org.eclipse.gef.EditPartFactory#createEditPart(org.eclipse.gef.EditPart, java.lang.Object)
	 */
	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if (model instanceof SDocumentGraph) {
			part = new GraphPart((SDocumentGraph) model);
		}
		else if (model instanceof SToken) {
			part = new TokenPart();
		}
		else  if (model instanceof SStructure) {
			part = new StructurePart();
		}
		else if (model instanceof String) {
			part = new GenericStringPart();
		}
		else if (model instanceof SAnnotation) {
			part = new AnnotationPart();
		}
		
		if (part != null) {
			part.setModel(model);
		}
		
		return part;
	}

}
