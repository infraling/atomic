/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class EditorPopupMenuConfiguration extends AbstractUiBindingConfiguration {

	private final Menu menu;
	private final SDocumentGraph graph;

	public EditorPopupMenuConfiguration(NatTable natTable, SDocumentGraph graph) {
		this.graph = graph;
		this.menu = new PopupMenuBuilder(natTable).withMenuItemProvider("newTokenPopupMenu", new NewTokenMenuItemProvider()).build();
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON), new CellPopupMenuAction());
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class NewTokenMenuItemProvider implements IMenuItemProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#
		 * addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable,
		 * org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem newTokenLabelsMenuItem = new MenuItem(popupMenu, SWT.PUSH);
			newTokenLabelsMenuItem.setText("Create &new token");
			newTokenLabelsMenuItem.setEnabled(true);

			newTokenLabelsMenuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					NatEventData data = (NatEventData) menu.getData(MenuItemProviders.NAT_EVENT_DATA_KEY);
					NatTable natTable = data.getNatTable();
					int columnPosition = data.getColumnPosition();
					IUniqueIndexLayer bodyLayer = ((ViewportLayer) ((GridLayer) natTable.getLayer()).getBodyLayer()).getScrollableLayer();
					int absoluteTokenIndex = LayerUtil.convertColumnPosition(natTable, columnPosition, bodyLayer);
					SToken selectedToken = graph.getSortedTokenByText().get(absoluteTokenIndex);
					Event event = new Event();
					event.data = selectedToken;
					event.widget = natTable;
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.tokeneditor.createToken", event);
					}
					catch (Exception ex) {
						throw new RuntimeException("org.corpus_tools.atomic.tokeneditor.createToken not found");
						// FIXME: Log
					}

				}
			});
		}

	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class CellPopupMenuAction implements IMouseAction {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction#run(org.
		 * eclipse.nebula.widgets.nattable.NatTable,
		 * org.eclipse.swt.events.MouseEvent)
		 */
		@Override
		public void run(NatTable natTable, MouseEvent event) {
			int columnPosition = natTable.getColumnPositionByX(event.x);
			int rowPosition = natTable.getRowPositionByY(event.y);

			ILayerCell cell = natTable.getCellByPosition(columnPosition, rowPosition);

			if (!cell.getDisplayMode().equals(DisplayMode.SELECT)) {
				natTable.doCommand(new SelectCellCommand(natTable, columnPosition, rowPosition, false, false));
			}

			menu.setData(MenuItemProviders.NAT_EVENT_DATA_KEY, event.data);
			menu.setVisible(true);
		}

	}
}
