/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class AnnotationGridDataProvider implements IDataProvider {

	private AnnotationGrid annotationGrid;

	public AnnotationGridDataProvider(AnnotationGrid annotationGrid) {
		this.annotationGrid = annotationGrid;
//		System.err.println("CREATE PROVIDER");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getDataValue(int, int)
	 */
	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		return annotationGrid.get(rowIndex, columnIndex);
	}

//	private Object getColumnIndex() {
//		colKeySet = annotationGrid.columnKeySet()
//		return null;
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#setDataValue(int, int, java.lang.Object)
	 */
	@Override
	public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
		Object previous = getDataValue(columnIndex, rowIndex); 
		SDocumentGraph graph = annotationGrid.getGraph();
		if (previous instanceof SAnnotation) {
			((SAnnotation) previous).setValue(newValue);
			annotationGrid.record(rowIndex, columnIndex, annotationGrid.getHeaderMap().get(columnIndex), previous);
		}
		else if (previous == null) {
			SToken token = graph.getSortedTokenByText().get(rowIndex);
			SSpan span = graph.createSpan(token);
			SAnnotation newAnno = span.createAnnotation(null, annotationGrid.getHeaderMap().get(columnIndex), newValue); // FIXME Introduce namespace
			annotationGrid.record(rowIndex, columnIndex, annotationGrid.getHeaderMap().get(columnIndex), newAnno);
		}
		else {
			// Not null, not an SAnnotation
			throw new UnsupportedOperationException("Not supperted yet!");
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return annotationGrid != null ? annotationGrid.getHeaderMap().size() : 0;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.IDataProvider#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return annotationGrid != null ? annotationGrid.getRowMap().size() : 0;
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
	 * @return the annotationGrid
	 */
	public AnnotationGrid getAnnotationTable() {
		return annotationGrid;
	}

	/**
	 * @param annotationGrid the annotationGrid to set
	 */
	public void setAnnotationTable(AnnotationGrid annotationTable) {
		this.annotationGrid = annotationTable;
	}

}
