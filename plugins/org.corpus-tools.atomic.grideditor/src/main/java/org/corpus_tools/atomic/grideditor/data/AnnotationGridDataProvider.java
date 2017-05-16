/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

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
