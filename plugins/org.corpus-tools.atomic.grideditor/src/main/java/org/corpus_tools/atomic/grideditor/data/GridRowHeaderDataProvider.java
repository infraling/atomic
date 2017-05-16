/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;

import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridRowHeaderDataProvider extends DefaultRowHeaderDataProvider {

	private AnnotationGrid annotationTable = null;

	public GridRowHeaderDataProvider(AnnotationGrid annotationTable) {
		super(null);
		this.annotationTable = annotationTable;
	}
	
	@Override
    public Object getDataValue(int columnIndex, int rowIndex) {
        return Integer.valueOf(rowIndex);
    }
	
	@Override
	public int getRowCount() {
		return annotationTable.getRowMap().size();
	}

}
