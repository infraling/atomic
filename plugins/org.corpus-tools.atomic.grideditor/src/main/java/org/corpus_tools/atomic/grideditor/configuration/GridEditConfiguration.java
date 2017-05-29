/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IEditableRule;
import org.eclipse.nebula.widgets.nattable.data.convert.DisplayConverter;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.edit.EditConfigAttributes;
import org.eclipse.nebula.widgets.nattable.edit.editor.TextCellEditor;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditConfiguration extends AbstractRegistryConfiguration {

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureRegistry(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)
	 */
	@Override
	public void configureRegistry(IConfigRegistry configRegistry) {
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.ALWAYS_EDITABLE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, getAnnotationValueConverter(), DisplayMode.NORMAL);
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITOR, new TextCellEditor(), DisplayMode.EDIT, null);
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

//	/**
//		 * // TODO Add description
//		 *
//		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
//		 * 
//		 */
//	public class AnnotationCellEditor extends TextCellEditor {
//
//		@Override
//		public boolean commit(MoveDirectionEnum direction, boolean closeAfterCommit, boolean skipValidation) {
//			if (!isClosed()) {
//				// always do the conversion
//				Object canonicalValue = getCanonicalValue();
//				if (skipValidation || (!skipValidation && validateCanonicalValue(canonicalValue))) {
//					boolean committed = new InlineEditHandler(layerCell.getLayer(), layerCell.getColumnPosition(),
//							layerCell.getRowPosition()).commit(canonicalValue, direction);
//					if (committed && closeAfterCommit) {
//						close();
//						if (direction != MoveDirectionEnum.NONE && openAdjacentEditor()) {
//							this.layerCell.getLayer().doCommand(new EditSelectionCommand(null, this.configRegistry));
//						}
//					}
//					return committed;
//				}
//				return false;
//			}
//			if (!isClosed()) {
//				close();
//			}
//			return true;
//		}
//	}

}
