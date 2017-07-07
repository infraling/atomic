package org.corpus_tools.atomic.grideditor.layers;

import org.corpus_tools.atomic.grideditor.configuration.GridEditorEditBindings;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.config.DefaultGridLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

/**
 * A customized {@link GridLayer}.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CustomGridLayer extends GridLayer {

	/**
	 * Constructor that passes `false` to the respective constructor, 
	 * {@link GridLayer#GridLayer(ILayer, ILayer, ILayer, ILayer, boolean)}.
	 * 
	 * @param bodyLayer
	 * @param columnHeaderLayer
	 * @param rowHeaderLayer
	 * @param cornerLayer
	 */
	public CustomGridLayer(ILayer bodyLayer, ILayer columnHeaderLayer, ILayer rowHeaderLayer, ILayer cornerLayer) {
		super(bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer, false);
	}
	
	@Override
    protected void init(boolean useDefaultConfiguration) {
        super.init(false);
        addConfiguration(new CustomGridLayerConfiguration(this));    }

	/**
	 * A customized {@link DefaultGridLayerConfiguration}.
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class CustomGridLayerConfiguration extends DefaultGridLayerConfiguration {

		/**
		 * Constructor simply calling the super constructor
		 * {@link DefaultGridLayerConfiguration#DefaultGridLayerConfiguration(CompositeLayer)}.
		 * 
		 * @param gridLayer
		 */
		public CustomGridLayerConfiguration(CompositeLayer gridLayer) {
			super(gridLayer);
		}
		
		/**
		 * Adds a custom configuration for edit bindings
		 * instead of using the default one.
		 */
		@Override
        protected void addEditingUIConfig() {
            addConfiguration(new GridEditorEditBindings());
        }

	}

}
