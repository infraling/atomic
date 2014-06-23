/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;

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
			part = new GraphPart();
		}
		
		if (part != null) {
			part.setModel(model);
		}
		
		return part;
	}

}
