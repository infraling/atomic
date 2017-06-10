/**
 * 
 */
package org.corpus_tools.atomic.grideditor.utils;

import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

import com.google.common.collect.Range;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CellUtils {
	
	public static Range<Integer> getRowIndicesForCell(ILayerCell cell) {
		if (cell.isSpannedCell()) {
			int startRowPositition = cell.getOriginRowPosition();
			int rowPosition = cell.getRowPosition();
			int distanceThisRowStartRow = rowPosition - startRowPositition;
			int rowIndex = cell.getRowIndex();
			int startIndex = rowIndex - distanceThisRowStartRow;
			return Range.closed(startIndex, startIndex + cell.getRowSpan() - 1);
		}
		else {
			return Range.closed(cell.getRowIndex(), cell.getRowIndex());
		}
	}

}
