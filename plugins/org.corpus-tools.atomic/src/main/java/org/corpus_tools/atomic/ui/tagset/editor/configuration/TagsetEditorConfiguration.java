/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.editor.configuration;

import java.util.ArrayList;  
import java.util.EnumSet;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.atomic.ui.tagset.editor.TagsetEditor;
import org.corpus_tools.salt.SALT_TYPE;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.validate.DataValidator;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.config.RenderErrorHandling;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.AbstractOverrider;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.painter.cell.ImagePainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.TextPainter;
import org.eclipse.nebula.widgets.nattable.painter.cell.decorator.CellPainterDecorator;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.util.CellEdgeEnum;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetEditorConfiguration extends AbstractRegistryConfiguration {


	private final static Image ERROR_IMAGE = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
	private static final String LAYER_COLUMN_LABEL = "Layer";
	private static final String ELEMENT_TYPE_COLUMN_LABEL = "Element type";
	private static final String NAMESPACE_COLUMN_LABEL = "Annotation namespace";
	private static final String NAME_COLUMN_LABEL = "Annoation name";
	private static final String VALUE_COLUMN_LABEL = "Annotation value";
	private static final String DESCRIPTION_COLUMN_LABEL = "Description";
	private static final String INVALID = "INVALID";
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
		// Automatically open the editor for the cell below FIXME Make this
		// configurable
		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, true);
		// Configure what to show in the grid
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, getValueConverter(),
				DisplayMode.NORMAL);
		// Configure cell editing
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(),
				DisplayMode.EDIT, LAYER_COLUMN_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR,
				new ElementTypeComboBoxEditor(getElementTypes()), DisplayMode.EDIT, ELEMENT_TYPE_COLUMN_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(),
				DisplayMode.EDIT, NAMESPACE_COLUMN_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new RightMovingTextCellEditor(),
				DisplayMode.EDIT, NAME_COLUMN_LABEL);

		// Configure regex validation for values
		UnmovingRegexableTextCellEditor regexEditor = new UnmovingRegexableTextCellEditor();
		regexEditor.setErrorDecorationEnabled(true);
		regexEditor.setDecorationPositionOverride(SWT.LEFT | SWT.TOP);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, regexEditor, DisplayMode.EDIT,
				VALUE_COLUMN_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.DATA_VALIDATOR, new RegexDataValidator(),
				DisplayMode.EDIT, VALUE_COLUMN_LABEL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.VALIDATION_ERROR_HANDLER, new RenderErrorHandling(),
				DisplayMode.EDIT, VALUE_COLUMN_LABEL);
		CellPainterDecorator errorDecoration = new CellPainterDecorator(new TextPainter(), CellEdgeEnum.TOP_LEFT,
				new ImagePainter(ERROR_IMAGE), false);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, errorDecoration, DisplayMode.NORMAL,
				INVALID);
		IStyle validationErrorStyle = new Style();
		validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle,
				DisplayMode.NORMAL, INVALID);

		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new UnmovingTextCellEditor(),
				DisplayMode.EDIT, DESCRIPTION_COLUMN_LABEL);
				
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
	
	
	public class UnmovingRegexableTextCellEditor extends TextCellEditor {
		
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
	
	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class RegexDataValidator extends DataValidator {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.nebula.widgets.nattable.data.validate.DataValidator#
		 * validate(int, int, java.lang.Object)
		 */
		@Override
		public boolean validate(int columnIndex, int rowIndex, Object newValue) {
			if (columnIndex == 4) {
				if (newValue instanceof String) {
					if (((String) newValue).startsWith("/") && ((String) newValue).endsWith("/")) {
						try {
							String pattern = ((String) newValue).substring(1, ((String) newValue).length() - 1);
							Pattern.compile(pattern);
						}
						catch (PatternSyntaxException exception) {
							return false;
						}
					}
					else {
						return true;
					}
				}
			}
			return true;
		}
	}

	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class WeakRegexValidationLabelAccumulator extends AbstractOverrider {

		private IRowDataProvider<TagsetValue> bodyDataProvider;

		public WeakRegexValidationLabelAccumulator(IRowDataProvider<TagsetValue> bodyDataProvider) {
			this.bodyDataProvider = bodyDataProvider;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.nebula.widgets.nattable.layer.cell.
		 * IConfigLabelAccumulator#accumulateConfigLabels(org.eclipse.nebula.
		 * widgets.nattable.layer.LabelStack, int, int)
		 */
		@Override
		public void accumulateConfigLabels(LabelStack configLabels, int columnPosition, int rowPosition) {
			TagsetValue rowObject = (TagsetValue) this.bodyDataProvider.getRowObject(rowPosition);

			if (columnPosition == 4) {
				if (!TagsetValue.isValidValue(rowObject)) {
					configLabels.addLabel(INVALID);
				}
			}
		}

	}


}
