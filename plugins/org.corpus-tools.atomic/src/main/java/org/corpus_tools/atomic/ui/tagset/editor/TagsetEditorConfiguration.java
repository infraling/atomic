/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.editor;

import java.util.ArrayList; 
import java.util.EnumSet;
import java.util.List;

import org.corpus_tools.salt.SALT_TYPE;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetEditorConfiguration extends AbstractRegistryConfiguration {

	private static final String LAYER_COLUMN_LABEL = "Layer";
	private static final String ELEMENT_TYPE_COLUMN_LABEL = "Element type";
	private static final String NAMESPACE_COLUMN_LABEL = "Annotation namespace";
	private static final String NAME_COLUMN_LABEL = "Annoation name";
	private static final String VALUE_COLUMN_LABEL = "Annotation value";
	private static final String DESCRIPTION_COLUMN_LABEL = "Description";
	private final ColumnOverrideLabelAccumulator accumulator;
	private final TagsetEditor editor;

	public TagsetEditorConfiguration(TagsetEditor tagsetEditor, ColumnOverrideLabelAccumulator columnLabelAccumulator) {
		this.accumulator = columnLabelAccumulator;
		this.editor = tagsetEditor;
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
				// Automatically open the editor for the cell below FIXME Make this configurable
				configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, true);
				// Configure what to show in the grid
				configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, getValueConverter(), DisplayMode.NORMAL);
				// Configure cell editing
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(), DisplayMode.EDIT, LAYER_COLUMN_LABEL);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new ElementTypeComboBoxEditor(getElementTypes()), DisplayMode.EDIT, ELEMENT_TYPE_COLUMN_LABEL);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(), DisplayMode.EDIT, NAMESPACE_COLUMN_LABEL);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(), DisplayMode.EDIT, NAME_COLUMN_LABEL);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new UnmovingTextCellEditor(), DisplayMode.EDIT, VALUE_COLUMN_LABEL);
				configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new UnmovingTextCellEditor(), DisplayMode.EDIT, DESCRIPTION_COLUMN_LABEL);
	}

	private List<SALT_TYPE> getElementTypes() {
		List<SALT_TYPE> saltTypeList = new ArrayList<>();
		saltTypeList.add(null);
		saltTypeList.addAll(new ArrayList<SALT_TYPE>(EnumSet.allOf(SALT_TYPE.class)));
		return saltTypeList;
	}

	private void registerConfigLabelsOnColumns() {
		accumulator.registerColumnOverrides(0, LAYER_COLUMN_LABEL);
		accumulator.registerColumnOverrides(1, ELEMENT_TYPE_COLUMN_LABEL);
		accumulator.registerColumnOverrides(2, NAMESPACE_COLUMN_LABEL);
		accumulator.registerColumnOverrides(3, NAME_COLUMN_LABEL);
		accumulator.registerColumnOverrides(4, VALUE_COLUMN_LABEL);
		accumulator.registerColumnOverrides(5, DESCRIPTION_COLUMN_LABEL);
	}
	
	private IDisplayConverter getValueConverter() {
		return new DisplayConverter() {
			
			@Override
			public Object displayToCanonicalValue(Object displayValue) {
				if (displayValue.equals("")) {
					return null;
				}
				else {
					return displayValue;
				}
			}
			
			@Override
			public Object canonicalToDisplayValue(Object canonicalValue) {
				if (canonicalValue == null) {
					return "";
				}
				else if (canonicalValue instanceof SALT_TYPE) {
					return ((SALT_TYPE) canonicalValue).name();
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
	private class ElementTypeComboBoxEditor extends ComboBoxCellEditor {
		
		private Object originalCanonicalValue = null;

		public ElementTypeComboBoxEditor(List<SALT_TYPE> elementTypes) {
			super(elementTypes);
		}
		
		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = TagsetEditorConfiguration.this.getValueConverter().canonicalToDisplayValue(originalCanonicalValue);
			return super.activateCell(parent, originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (getCanonicalValue() != null) {
				if (!getCanonicalValue().equals(originalCanonicalValue)) {
					TagsetEditorConfiguration.this.editor.setDirty(true);
				}
			}
			else if (originalCanonicalValue != null) {
				TagsetEditorConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.RIGHT, true, true);
		}

	}


	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class RightMovingTextCellEditor extends TextCellEditor {
		private String originalCanonicalValue;

		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = String.valueOf(originalCanonicalValue);
			return super.activateCell(parent, originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (getCanonicalValue() != null) {
				if (!getCanonicalValue().equals(originalCanonicalValue)) {
					TagsetEditorConfiguration.this.editor.setDirty(true);
				}
			}
			else if (originalCanonicalValue != null) {
				TagsetEditorConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.RIGHT, true, true);
		}
	
	}


	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class UnmovingTextCellEditor extends TextCellEditor {
		
		private String originalCanonicalValue;
		
		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = String.valueOf(originalCanonicalValue);
			return super.activateCell(parent, originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (getCanonicalValue() != null) {
				if (!getCanonicalValue().equals(originalCanonicalValue)) {
					TagsetEditorConfiguration.this.editor.setDirty(true);
				}
			}
			else if (originalCanonicalValue != null) {
				TagsetEditorConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.NONE, true, true);
		}
	
	}

}
