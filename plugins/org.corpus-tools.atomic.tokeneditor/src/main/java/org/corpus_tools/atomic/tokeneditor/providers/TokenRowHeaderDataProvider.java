/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.providers;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenRowHeaderDataProvider extends DefaultRowHeaderDataProvider {

	/**
	 * @param bodyDataProvider
	 */
	public TokenRowHeaderDataProvider(IDataProvider bodyDataProvider) {
		super(bodyDataProvider);
		// TODO Auto-generated constructor stub
	}

	@Override
	public int getColumnCount() {
		return 1;
	}

	@Override
	public int getRowCount() {
		return 2;
	}

	@Override
	public Object getDataValue(int columnIndex, int rowIndex) {
		switch (rowIndex) {
		case 0:
			return "Token text";

		case 1:
			return "Token index";

		default:
			return null;
		}
	}

}