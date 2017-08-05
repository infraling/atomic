/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.editor.configuration;

import org.corpus_tools.atomic.tagset.TagsetValue;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CustomLabelAccumulator extends ColumnOverrideLabelAccumulator {

	private IRowDataProvider<TagsetValue> bodyRowDataProvider;

	public CustomLabelAccumulator(ILayer layer, IRowDataProvider<TagsetValue> bodyRowDataProvider) {
		super(layer);
		this.bodyRowDataProvider = bodyRowDataProvider;
	}
	
	@Override
	public void accumulateConfigLabels(
			LabelStack configLabels, 
			int columnPosition, 
			int rowPosition) {
		super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

		//get the row object out of the dataprovider
		TagsetValue rowObject = this.bodyRowDataProvider
					.getRowObject(rowPosition);

		//in column 3 and 4 there are the values that 
		//are cross validated
		if (columnPosition == 4) {
			if (!TagsetValue.isValidValue(rowObject)) {
				configLabels.addLabel("INVALID");
			}
		}
	}

}
