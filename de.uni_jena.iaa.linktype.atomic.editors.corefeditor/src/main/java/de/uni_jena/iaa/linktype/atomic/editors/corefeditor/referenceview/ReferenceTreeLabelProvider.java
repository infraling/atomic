/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Image;

/**
 * @author Stephan Druskat
 *
 */
public class ReferenceTreeLabelProvider extends StyledCellLabelProvider implements ILabelProvider {
	
	public void update(ViewerCell cell) {
        Object obj = cell.getElement();
        StyledString styledString = new StyledString(obj.toString());

        if(obj instanceof MyTreeNode) {
             MyTreeNode parent = (MyTreeNode) obj;
             styledString.append(" (" + parent.getText() + ")", StyledString.COUNTER_STYLER);
        }

       cell.setText(styledString.toString());
       cell.setStyleRanges(styledString.getStyleRanges());
       cell.setImage(getImage(obj));
       super.update(cell);
   }


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	@Override
	public Image getImage(Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	@Override
	public String getText(Object element) {
		if (element instanceof MyTreeNode) {
			return ((MyTreeNode) element).getText();
		}
		return "Birthday pony!";
	}

}
