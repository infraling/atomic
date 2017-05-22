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
		configRegistry.registerConfigAttribute(EditConfigAttributes.CELL_EDITABLE_RULE, IEditableRule.NEVER_EDITABLE);
		configRegistry.registerConfigAttribute(CellConfigAttributes.DISPLAY_CONVERTER, getAnnotationValueConverter(), DisplayMode.NORMAL);
	}

	private IDisplayConverter getAnnotationValueConverter() {
		return new DisplayConverter() {
			
			private Object canonicalValue;

			@Override
			public Object displayToCanonicalValue(Object displayValue) {
				return canonicalValue;
			}
			
			@Override
			public Object canonicalToDisplayValue(Object canonicalValue) {
				this.canonicalValue = canonicalValue;
				if (canonicalValue instanceof SAnnotation) {
					return ((SAnnotation) canonicalValue).getValue();
				}
				else {
					return canonicalValue;
				}
			}
		};
	}

}
