/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.layerview;

import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;

/**
 * @author Stephan Druskat
 *
 */
public class LayerLabelProvider extends LabelProvider implements ITableColorProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof String) {
			return (String) element;
		}
		return null;
	}

	/**
	 * @param layerView
	 */
	public LayerLabelProvider(LayerView layerView) {
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
