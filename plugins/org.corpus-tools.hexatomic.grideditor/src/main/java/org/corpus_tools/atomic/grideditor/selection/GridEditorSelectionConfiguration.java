/**
 * 
 */
package org.corpus_tools.atomic.grideditor.selection;

import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionLayerConfiguration;

import com.google.common.collect.TreeBasedTable;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditorSelectionConfiguration extends DefaultSelectionLayerConfiguration {

	private TreeBasedTable<Integer, String, Object> annotationTable;

	public GridEditorSelectionConfiguration(TreeBasedTable<Integer, String, Object> annotationTable) {
		this.annotationTable = annotationTable;
	}
	
	@Override
	protected void addSelectionUIBindings() {
//		addConfiguration(new GridEditorSelectionBindings());
	}

}
