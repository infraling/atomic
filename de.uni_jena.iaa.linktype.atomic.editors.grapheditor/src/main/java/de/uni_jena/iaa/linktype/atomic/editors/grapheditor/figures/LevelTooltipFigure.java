/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.figures;

import org.eclipse.draw2d.Border;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.TextFlow;

/**
 * @author Stephan Druskat
 * 
 * Based on https://www.vainolo.com/2011/09/04/creating-an-opm-gef-editor-%E2%80%93-part-19-displaying-tooltips/
 *
 */
public class LevelTooltipFigure extends FlowPage {
	
	private final Border TOOLTIP_BORDER = new MarginBorder(0, 2, 1, 0);
    private TextFlow message;
     
    public LevelTooltipFigure() {
        setOpaque(true);
        setBorder(TOOLTIP_BORDER);
        message = new TextFlow();
        message.setText("");
        add(message);
    }
     
    @Override
    public Dimension getPreferredSize(int w, int h) {
        Dimension d = super.getPreferredSize(-1, -1);
        if (d.width > 150)
            d = super.getPreferredSize(150, -1);
        return d;
    }
     
    public void setMessage(String txt) {
        message.setText(txt);
        revalidate();
        repaint();
    }
}
