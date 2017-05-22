/**
 * 
 */
package org.corpus_tools.atomic.grideditor.selection;

import java.util.ArrayList;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class MultiCellSelection implements ISelection {
	
	// FIXME Implement

	private final ArrayList<ILayerCell> cells;

	public MultiCellSelection(ArrayList<ILayerCell> cells) {
		this.cells = cells;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.cells == null;
	}

}
