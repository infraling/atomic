/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.data;

import java.util.List;

import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.cell.DataCell;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenListDataProvider extends ListDataProvider<SToken> implements ISpanningDataProvider {

	/**
	 * @param list
	 * @param columnAccessor
	 */
	public TokenListDataProvider(List<SToken> list, IColumnAccessor<SToken> columnAccessor) {
		super(list, columnAccessor);
		// TODO Auto-generated constructor stub
	}
	
    @Override
    public int getColumnCount() {
//        return this.columnAccessor.getColumnCount();
    	return this.list.size();
    }

    @Override
    public int getRowCount() {
//        return this.list.size();
    	return this.columnAccessor.getColumnCount();
    }

    @Override
    public Object getDataValue(int columnIndex, int rowIndex) {
        SToken colObj = this.list.get(columnIndex);
        return this.columnAccessor.getDataValue(colObj, rowIndex);
    }

    @Override
    public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
    	// FIXME Check if implemenatation is necessary
//        SToken colObj = this.list.get(columnIndex);
//        this.columnAccessor.setDataValue(colObj, rowIndex, newValue);
    }

    @Override
    public SToken getRowObject(int rowIndex) {
        return this.list.get(rowIndex);
    }

    @Override
    public int indexOfRowObject(SToken rowObject) {
        return this.list.indexOf(rowObject);
    }

    @Override
	public List<SToken> getList() {
        return this.list;
    }

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider#getCellByPosition(int, int)
	 */
	@Override
	public DataCell getCellByPosition(int columnPosition, int rowPosition) {
		// TODO Auto-generated method stub
		return null;
	}


}
