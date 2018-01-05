/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import java.util.ArrayList; 
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.tagset.api.Tagset;
import org.corpus_tools.atomic.tagset.api.TagsetValue;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.graph.LabelableElement;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.ComboBoxCellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.ICellEditor;
import org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
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
import org.eclipse.swt.graphics.Image;
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
	private static final String INVALID = "INVALID";
	private final static Image ERROR_IMAGE = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
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
		ICellEditor cellEditor = new AnnotationCellEditor();
		cellEditor = editor.hasTagset ? new AnnotationComboEditor(new TagsetValuesDataProvider()) : new AnnotationCellEditor();
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, cellEditor, DisplayMode.EDIT, null);
		// Automatically open the editor for the cell below FIXME Make this configurable
//		configRegistry.registerConfigAttribute(EditConfigAttributes.OPEN_ADJACENT_EDITOR, true);
		// Switch off out-of-the-box multi-cell editing
		configRegistry.registerConfigAttribute(EditConfigAttributes.SUPPORT_MULTI_EDIT, false);
		// Disable editing of token text
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE, DisplayMode.EDIT, TOKEN_TEXT_COLUMN_LABEL);
		
		
		
		
		CellPainterDecorator errorDecoration = new CellPainterDecorator(new TextPainter(), CellEdgeEnum.TOP_LEFT,
				new ImagePainter(ERROR_IMAGE), false);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_PAINTER, errorDecoration, DisplayMode.NORMAL,
				INVALID);
		IStyle validationErrorStyle = new Style();
		validationErrorStyle.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR, GUIHelper.COLOR_RED);

		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, validationErrorStyle,
				DisplayMode.NORMAL, INVALID);
	}

	private void registerConfigLabelsOnColumns() {
		accumulator.registerColumnOverrides(0, TOKEN_TEXT_COLUMN_LABEL);
		
	}

	private IDisplayConverter getAnnotationValueConverter() {
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
				else if (canonicalValue instanceof SAnnotation) {
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
			if (getCanonicalValue() != null) {
				if (!getCanonicalValue().equals(originalCanonicalValue)) {
					GridEditorConfiguration.this.editor.setDirty(true);
				}
			}
			else if (originalCanonicalValue != null) {
				GridEditorConfiguration.this.editor.setDirty(true);
			}
			return super.commit(MoveDirectionEnum.DOWN, true, true);

		}
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class AnnotationComboEditor extends ComboBoxCellEditor {
		
		public AnnotationComboEditor(IComboBoxDataProvider provider) {
			super(provider);
			this.freeEdit = true;
		}
		
		private Object originalCanonicalValue = null;

		@Override
	    protected Control activateCell(final Composite parent, Object originalCanonicalValue) {
			this.originalCanonicalValue = GridEditorConfiguration.this.getAnnotationValueConverter().canonicalToDisplayValue(originalCanonicalValue);
			if (this.originalCanonicalValue == null) {
				return super.activateCell(parent, this.originalCanonicalValue);
			}
			return super.activateCell(parent, this.originalCanonicalValue);
		}

		@Override
		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
			if (getCanonicalValue() != null) {
				if (!getCanonicalValue().equals(originalCanonicalValue)) {
					GridEditorConfiguration.this.editor.setDirty(true);
				}
			}
			else if (originalCanonicalValue != null) {
				GridEditorConfiguration.this.editor.setDirty(true);
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
	public class TagsetValuesDataProvider implements IComboBoxDataProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.edit.editor.IComboBoxDataProvider#getValues(int, int)
		 */
		@Override
		public List<?> getValues(int columnIndex, int rowIndex) {
			List<String> validValues = new ArrayList<>();
			Tagset tagset = GridEditorConfiguration.this.editor.getTagset();
			AnnotationGrid grid = GridEditorConfiguration.this.editor.getAnnotationGrid();
			Object gridObject = grid.get(rowIndex, columnIndex);
			if (gridObject != null) {
				if (gridObject instanceof SAnnotation) {
					SAnnotation annotation = (SAnnotation) gridObject;
					LabelableElement container = annotation.getContainer();
					if (container instanceof SStructuredNode) {
						/*
						 * TODO In this iteration of the editor, no element types
						 * are queried.
						 */
						String namespace = annotation.getNamespace();
						String name = annotation.getName();
//						SStructuredNode node = (SStructuredNode) container;
//						Set<SLayer> layers = node.getLayers();
//						if (layers.size() != 0) {
//							for (SLayer l : layers) {
//								tagset.getValuesForParameters(l.getName(), null, namespace, name).forEach(v -> validValues.add(v.getValue()));
//							}
//						}
//						else {
							// FIXME: Document that GridEditor does not support layers and element types yet!
							tagset.getValuesForParameters(null, null, namespace, name).forEach(v -> validValues.add(v.getValue()));
//						}
					}
				}
			}
			else {
				// gridObject is null
				/* 
				 * TODO: INTRODUCE LAYER IN DISPLAY (in grid column header, add layer name)
				 * THEN ADD LAYER TO NEWLY CREATED ANNOTATION!
				 */
				String header = grid.getColumnHeaderMap().get(columnIndex);
				String namespace = null;
				String name = null;
				String[] headerSplit = header.split("::");
				if (headerSplit.length == 2) {
					namespace = headerSplit[0].equals("null") ? null : headerSplit[0];
					name = headerSplit[1];
				}
				else if (headerSplit.length == 1) {
					name = headerSplit[0];
				}
				// FIXME: Document that GridEditor does not support layers and element types yet!
				Set<TagsetValue> values = tagset.getValuesForParameters(null, null, namespace, name);
				values.stream().forEach(v -> validValues.add(v.getValue()));
			}
			return validValues;
		}
	
	}

}
	