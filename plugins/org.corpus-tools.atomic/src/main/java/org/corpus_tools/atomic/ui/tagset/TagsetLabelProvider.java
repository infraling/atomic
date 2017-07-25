/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset;

import org.corpus_tools.atomic.tagset.TagsetValue;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetLabelProvider extends LabelProvider implements ITableLabelProvider {
	
	@Override
	public String getText(Object element) {
		return element == null ? "" : element.toString();
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof TagsetValue) {
			TagsetValue tagsetValue = (TagsetValue) element;
			switch (columnIndex) {
			case 0:
				return tagsetValue.getLayer();
			case 1:
				return tagsetValue.getElementType() == null ? "" : tagsetValue.getElementType().toString();
			case 2:
				return tagsetValue.getNamespace();
			case 3:
				return tagsetValue.getName();
			case 4:
				return tagsetValue.getValue();
			case 5: 
				return tagsetValue.getDescription();
				
			default:
				return null;
			}
		}
		else {
			return null;
		}
	}

}
