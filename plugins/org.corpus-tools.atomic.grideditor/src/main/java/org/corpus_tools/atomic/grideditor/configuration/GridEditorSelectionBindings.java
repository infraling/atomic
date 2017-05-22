/**
 * 
 */
package org.corpus_tools.atomic.grideditor.configuration;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.IConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.action.PageUpAction;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionBindings;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditorSelectionBindings extends DefaultSelectionBindings {

//	/* (non-Javadoc)
//	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureLayer(org.eclipse.nebula.widgets.nattable.layer.ILayer)
//	 */
//	@Override
//	public void configureLayer(ILayer layer) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureRegistry(org.eclipse.nebula.widgets.nattable.config.IConfigRegistry)
//	 */
//	@Override
//	public void configureRegistry(IConfigRegistry configRegistry) {
//		// TODO Auto-generated method stub
//
//	}
//
//	/* (non-Javadoc)
//	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureUiBindings(org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry)
//	 */
//	@Override
//	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
//		// TODO Auto-generated method stub
//
//	}
	
	@Override
	protected void configurePageDownButtonBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
		IKeyAction newAction = new IKeyAction() {

			@Override
			public void run(NatTable natTable, KeyEvent event) {
				// FIXME: Preferentialize the distance value
				int distance = 10;
				jumpTo(natTable, distance);

			}

		};
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.PAGE_DOWN), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.PAGE_DOWN), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.PAGE_DOWN), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.PAGE_DOWN), newAction);
	}
	
	@Override
	protected void configurePageUpButtonBindings(UiBindingRegistry uiBindingRegistry, PageUpAction action) {
		IKeyAction newAction = new IKeyAction() {

			@Override
			public void run(NatTable natTable, KeyEvent event) {
				// FIXME: Preferentialize the distance value
				int distance = -10;
				jumpTo(natTable, distance);

			}

		};
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.NONE, SWT.PAGE_UP), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2, SWT.PAGE_UP), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD1, SWT.PAGE_UP), newAction);
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.PAGE_UP), newAction);
	}
	
	private void jumpTo(NatTable natTable, int distance) {
		SelectionLayer selectionLayer = null;
		ILayer underlyingLayer = natTable.getLayer();
		if (underlyingLayer instanceof GridLayer) {
			ILayer bodyLayer = ((GridLayer) underlyingLayer).getBodyLayer();
			if (bodyLayer instanceof ViewportLayer) {
				IUniqueIndexLayer scrollLayer = ((ViewportLayer) bodyLayer).getScrollableLayer();
				if (scrollLayer instanceof SelectionLayer) {
					selectionLayer = (SelectionLayer) scrollLayer;
				}
				try {
					int selectedRow = selectionLayer.getSelectedRowPositions().iterator().next().start;
//					int selectedRow = selectionLayer.getSelectedColumnPositions()[0];
					int lastItemIndex = selectionLayer.getRowCount() - 1;
					int jumpPosition = selectedRow + distance;
					if (jumpPosition > lastItemIndex) {
						jumpPosition = lastItemIndex;
					}
					else if (jumpPosition < 0) {
						jumpPosition = 0;
					}
					selectionLayer.selectRow(0, jumpPosition, false, false);
//					selectionLayer.selectColumn(jumpPosition, 0, false, false);
				}
				catch (ArrayIndexOutOfBoundsException e) {
					// Ignore
				}
			}
		}
	}

}
