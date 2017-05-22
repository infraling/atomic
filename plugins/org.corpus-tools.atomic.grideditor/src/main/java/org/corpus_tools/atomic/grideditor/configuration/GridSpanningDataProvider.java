/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GridSpanningDataProvider extends AutomaticSpanningDataProvider {

	public GridSpanningDataProvider(IDataProvider underlyingDataProvider, boolean autoColumnSpan, boolean autoRowSpan) {
		super(underlyingDataProvider, autoColumnSpan, autoRowSpan);
	}
	
	@Override
	protected boolean valuesNotEqual(Object value1, Object value2) {
		if (value1 == null && value2 == null) {
			return true;
		}
		else if (value1 == value2) {
            return false;
        }
        return ((value1 == null && value2 != null)
                || (value1 != null && value2 == null)
                || !value1.equals(value2));
    }

}
