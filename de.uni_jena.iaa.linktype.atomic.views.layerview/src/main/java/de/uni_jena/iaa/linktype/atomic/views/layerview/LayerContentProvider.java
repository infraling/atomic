/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.layerview;

import java.util.ArrayList;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.views.layerview.util.NewLayer;

/**
 * @author Stephan Druskat
 * 
 */
public class LayerContentProvider implements IStructuredContentProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		ArrayList<String> layers = new ArrayList<String>();
		if (inputElement instanceof SDocumentGraph) {
			for (SLayer layer : ((SDocumentGraph) inputElement).getSLayers()) {
				layers.add(layer.getSName());
			}
			layers.add("\u269B NO ASSIGNED LEVEL \u269B");
			return layers.toArray();
		}
		return null;
	}

}
