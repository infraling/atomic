/**
 * 
 */
package org.corpus_tools.atomic.tagset.ui.editor.configuration;

import org.corpus_tools.atomic.tagset.api.TagsetValue;
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

		// Get the row object via data provider
		TagsetValue rowObject = this.bodyRowDataProvider.getRowObject(rowPosition);

		// Check if annotation value validifies
		if (columnPosition == 4) {
			if (!TagsetValue.isValidValue(rowObject)) {
				configLabels.addLabel("INVALID");
			}
		}
	}

}
