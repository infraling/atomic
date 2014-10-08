/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceCellEditor extends TextCellEditor {

	int minHeight = 0;

	public ReferenceCellEditor(Tree tree) {
		super(tree, SWT.BORDER);
		Text txt = (Text) getControl();

		Font fnt = txt.getFont();
		FontData[] fontData = fnt.getFontData();
		if (fontData != null && fontData.length > 0)
			minHeight = fontData[0].getHeight() + 10;
	}

	public LayoutData getLayoutData() {
		LayoutData data = super.getLayoutData();
		if (minHeight > 0)
			data.minimumHeight = minHeight;
		return data;
	}

}
