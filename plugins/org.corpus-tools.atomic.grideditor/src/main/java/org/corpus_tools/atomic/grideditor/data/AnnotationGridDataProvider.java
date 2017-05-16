/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.DataCell;

import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class AnnotationGridDataProvider implements IDataProvider {

	private AnnotationGrid annotationTable;

	public AnnotationGridDataProvider(AnnotationGrid annotationTable) {
		this.annotationTable = annotationTable;
//		System.err.println("CREATE PROVIDER");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getDataValue(int, int)
	 */
	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
////		Object node = null;
//		LinkedHashMap colMap = (LinkedHashMap) annotationTable.columnMap();
////		colMap.gete
//		Object cell = annotationTable.get(rowIndex, ((LinkedHashMap<String, Object>)annotationTable.columnMap()).get);
////		SortedMap<String, Object> sortedRow = (SortedMap<String, Object>) annotationTable.rowMap().get(rowIndex);
////		if (sortedRow.values().size() > columnIndex) {
////			node = sortedRow.values().toArray()[columnIndex];
////		}
//		System.err.println("GOT NODE");
		return annotationTable.get(rowIndex, columnIndex);
	}

//	private Object getColumnIndex() {
//		colKeySet = annotationTable.columnKeySet()
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#setDataValue(int, int, java.lang.Object)
	 */
	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return annotationTable.getHeaderMap().size();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return annotationTable.getRowMap().size();
	}

//	/* (non-Javadoc)
//	 * @see org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider#getCellByPosition(int, int)
//	 */
//	@Override
//	public DataCell getCellByPosition(int columnPosition, int rowPosition) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/**
	 * @return the annotationTable
	 */
	public AnnotationGrid getAnnotationTable() {
		return annotationTable;
	}

	/**
	 * @param annotationTable the annotationTable to set
	 */
	public void setAnnotationTable(AnnotationGrid annotationTable) {
		this.annotationTable = annotationTable;
	}

}
