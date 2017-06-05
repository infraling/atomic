/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditConfiguration extends AbstractRegistryConfiguration {

	private final GridEditor editor;

	public GridEditConfiguration(GridEditor gridEditor) {
		this.editor = gridEditor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureRegistry(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)
	 */
	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		// Make cells editable
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
		// Configure what to show in the grid
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, getAnnotationValueConverter(), DisplayMode.NORMAL);
		// Configure cell editing
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new AnnotationCellEditor(), DisplayMode.EDIT, null);
		// Automatically open the editor for the cell below FIXME Make this configurable
//		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, true);
		// Switch off out-of-the-box multi-cell editing
		configRegistry.registerConfigAttribute(EditConfigAttributes.SUPPORT_MULTI_EDIT, false);
	}

	private IDisplayConverter getAnnotationValueConverter() {
		return new DisplayConverter() {
			
			@Override
			public Object displayToCanonicalValue(Object displayValue) {
				return displayValue;
			}
			
			@Override
			public Object canonicalToDisplayValue(Object canonicalValue) {
				if (canonicalValue instanceof SAnnotation) {
					return ((SAnnotation) canonicalValue).getValue();
				}
				else {
					return canonicalValue;
				}
			}
		};
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class AnnotationCellEditor extends TextCellEditor {
		
		private Object originalCanonicalValue = null;

		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = GridEditConfiguration.this.getAnnotationValueConverter().canonicalToDisplayValue(originalCanonicalValue);
			return super.activateCell(parent, originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (!getCanonicalValue().equals(originalCanonicalValue)) {
				GridEditConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.DOWN, true, true);
		}
	}

}
