/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Table;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridColumnHeaderDataProvider extends DefaultColumnHeaderDataProvider {

	private AnnotationGrid annotationTable = null;

	public GridColumnHeaderDataProvider(AnnotationGrid annotationTable) {
		super(null);
		this.annotationTable = annotationTable;
	}
	
	@Override
	public String getColumnHeaderLabel(int columnIndex) {
		String label = annotationTable.getColumnHeaderMap().get(columnIndex);//FluentIterable.from(annotationTable.columnKeySet()).skip(columnIndex).limit(columnIndex + 1).toList().get(0);
		return label;
	}
	
	@Override
    public int getColumnCount() {
        return annotationTable.getColumnHeaderMap().size();//columnKeySet().size();
    }

}
