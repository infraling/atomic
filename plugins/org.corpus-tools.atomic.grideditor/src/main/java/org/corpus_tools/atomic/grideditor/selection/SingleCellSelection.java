/**
 * 
 */
package org.corpus_tools.atomic.grideditor.selection;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SingleCellSelection implements ISelection {

	private final ILayerCell cell;

	public SingleCellSelection(ILayerCell cell) {
		this.cell = cell;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelection#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return cell == null;
	}
	
	public int getRowIndex() {
		return cell.getRowIndex();
	}
	
	public int getColumnIndex() {
		return cell.getColumnIndex();
	}
	
	public Object getValue() {
		return cell.getDataValue();
	}
	
	public boolean singleCell() {
		return true;
	}

}
