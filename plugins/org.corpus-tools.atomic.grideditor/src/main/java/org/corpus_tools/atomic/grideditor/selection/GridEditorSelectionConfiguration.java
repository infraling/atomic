/**
 * 
 */
package org.corpus_tools.atomic.grideditor.selection;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionLayerConfiguration;

import com.google.common.collect.TreeBasedTable;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditorSelectionConfiguration extends DefaultSelectionLayerConfiguration {

	private AnnotationGrid annotationGrid;

	public GridEditorSelectionConfiguration(AnnotationGrid annotationGrid) {
		this.annotationGrid = annotationGrid;
	}
	
	@Override
	protected void addSelectionUIBindings() {
		addConfiguration(new GridEditorSelectionBindings());
	}

}
