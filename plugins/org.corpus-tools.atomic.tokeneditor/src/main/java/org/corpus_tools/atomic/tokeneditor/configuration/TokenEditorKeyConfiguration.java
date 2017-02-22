/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenEditorKeyConfiguration extends AbstractUiBindingConfiguration {

	private final StyledText text;

	/**
	 * @param natTable
	 * @param text
	 */
	public TokenEditorKeyConfiguration(StyledText text) {
		this.text = text;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.config.IConfiguration#configureUiBindings(org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry)
	 */
	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		
		/*
		 * CTRL + T sets the focus on the text control.
		 */
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.CTRL, 't'), new IKeyAction() {
			
			@Override
			public void run(NatTable natTable, KeyEvent event) {
				syncTextPosition(natTable);
				text.setFocus();
			}
		});			
		
		/*
		 * CTRL + G selects the text of the currently selected token
		 * in the table in the text widget, and sets the caret to its start
		 * offset.
		 * 
		 * Note that the subsequent focusing of the text widget is handled
		 * by another binding (CTRL + T).
		 */
		uiBindingRegistry.registerKeyBinding(new KeyEventMatcher(SWT.CTRL, 'g'), new IKeyAction() {
			
			@Override
			public void run(NatTable natTable, KeyEvent event) {
				syncTextPosition(natTable);
			}
		});

	}

	/**
	 * TODO: Description
	 *
	 * @param natTable
	 */
	private void syncTextPosition(NatTable natTable) {
		SelectionLayer selectionLayer = null;
		ILayer underlyingLayer = natTable.getLayer();
		if (underlyingLayer instanceof GridLayer) {
			ILayer bodyLayer = ((GridLayer) underlyingLayer).getBodyLayer();
			if (bodyLayer instanceof ViewportLayer) {
				IUniqueIndexLayer scrollLayer = ((ViewportLayer) bodyLayer).getScrollableLayer();
				if (scrollLayer instanceof SelectionLayer) {
					selectionLayer = (SelectionLayer) scrollLayer;
				}
				int selectedCol = selectionLayer.getSelectedColumnPositions()[0];
				Object pos = selectionLayer.getDataValueByPosition(selectedCol, 1); // FIXME: Get int via constant in editor so that it cannot break if row number changes
				if (pos instanceof String) {
					String[] split = ((String) pos).split("\\s-\\s", 2);
					int start = Integer.parseInt(split[0].trim());
					int end = Integer.parseInt(split[1].trim());
					text.setCaretOffset(start);
					text.setSelection(start, end);
				}
			}
		}
	}

}
