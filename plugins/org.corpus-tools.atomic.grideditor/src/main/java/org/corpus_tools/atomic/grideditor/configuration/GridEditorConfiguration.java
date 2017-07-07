/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
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
public class GridEditorConfiguration extends AbstractRegistryConfiguration {

	private static final String TOKEN_TEXT_COLUMN_LABEL = "Token";
	private final GridEditor editor;
	private final ColumnOverrideLabelAccumulator accumulator;

	public GridEditorConfiguration(GridEditor gridEditor, ColumnOverrideLabelAccumulator columnLabelAccumulator) {
		this.editor = gridEditor;
		this.accumulator = columnLabelAccumulator; 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureRegistry(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)
	 */
	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		// Register configuration labels on (some) columns
		registerConfigLabelsOnColumns();
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
		// Disable editing of token text
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, TOKEN_TEXT_COLUMN_LABEL);
	}

	private void registerConfigLabelsOnColumns() {
		accumulator.registerColumnOverrides(0, TOKEN_TEXT_COLUMN_LABEL);
		
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
				else if (canonicalValue instanceof SToken) {
					if (((SToken) canonicalValue).getGraph().getText((SNode) canonicalValue).isEmpty()) {
						return "∅";
						// FIXME: Use different colouring or similar
					}
					return ((SToken) canonicalValue).getGraph().getText((SNode) canonicalValue);
				}
				else {
					return canonicalValue;
				}
			}
		};
	}

	public class AnnotationCellEditor extends TextCellEditor {
		
		private Object originalCanonicalValue = null;

		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = GridEditorConfiguration.this.getAnnotationValueConverter().canonicalToDisplayValue(originalCanonicalValue);
			return super.activateCell(parent, originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (!getCanonicalValue().equals(originalCanonicalValue)) {
				GridEditorConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.DOWN, true, true);
		}
	}

}
