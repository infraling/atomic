/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import java.util.ArrayList; 
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CustomGridLabelAccumulator extends ColumnOverrideLabelAccumulator {

	private GridSpanningDataProvider rowDataProvider;
	private Tagset tagset;
	private AnnotationGrid grid;

	public CustomGridLabelAccumulator(ILayer layer, GridSpanningDataProvider rowDataProvider, Tagset tagset, AnnotationGrid annotationGrid) {
		super(layer);
		this.rowDataProvider = rowDataProvider;
		this.tagset = tagset;
		this.grid = annotationGrid;
	}
	
	@Override
	public void accumulateConfigLabels(
			LabelStack configLabels, 
			int columnPosition, 
			int rowPosition) {
		super.accumulateConfigLabels(configLabels, columnPosition, rowPosition);

		// Get the row object via data provider
		Object rowObject = rowDataProvider.getDataValue(columnPosition, rowPosition);
		if (rowObject instanceof SAnnotation) {
			String annoVal = ((SAnnotation) rowObject).getValue_STEXT();
//		String rowObject = this.rowDataProvider.getRowObject(rowPosition);
		String[] headerSplit = grid.getHeaderMap().get(columnPosition).split("::");
		String namespace = headerSplit[0];
		String name = headerSplit[1];
		Set<TagsetValue> values = tagset.getValuesForParameters(null, null, namespace, name);
		Collection<String> valueStrings = values.stream().map(TagsetValue::getValue).collect(Collectors.toCollection(ArrayList::new));
		// Check if annotation value validifies
		if (!valueStrings.contains(annoVal)) {
			configLabels.addLabel("INVALID");
		}
		}
	}

}
