/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.layerview;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;

/**
 * @author Stephan Druskat
 *
 */
public class LayerLabelProvider extends LabelProvider implements ITableColorProvider {

	private LayerView layerView;
	
	@Override
	public String getText(Object element) {
		if (element instanceof SLayer) {
			return ((SLayer) element).getSName();
		}
		return null;
	}

	/**
	 * @param layerView
	 */
	public LayerLabelProvider(LayerView layerView) {
		this.layerView = layerView;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	@Override
	public Color getForeground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	@Override
	public Color getBackground(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
