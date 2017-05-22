/**
 * 
 */
package org.corpus_tools.atomic.grideditor.menu;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.ui.action.IMouseAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
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
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class GridPopupMenuConfiguration extends AbstractUiBindingConfiguration {

	private final Menu menu;
	private final AnnotationGrid annotationGrid;
	private final NatTable natTable;

	public GridPopupMenuConfiguration(AnnotationGrid annotationGrid, NatTable natTable) {
		this.menu = new PopupMenuBuilder(natTable).withMenuItemProvider("ColumnPopupMenu", new NewColumnMenuItemProvider()).build();
		this.annotationGrid = annotationGrid;
		this.natTable = natTable;
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON), new CellPopupMenuAction());
	}

	public class NewColumnMenuItemProvider implements IMenuItemProvider {

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#
		 * addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable,
		 * org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem createNewColumnMenuItem = new MenuItem(popupMenu, SWT.PUSH);
			createNewColumnMenuItem.setText("&New annotation column");
			createNewColumnMenuItem.setEnabled(true);
			createNewColumnMenuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					executeCreateColumnCommand();
				}
			});
		}

	}
	
	/**
	 * // TODO Add description
	 * 
	 */
	private void executeCreateColumnCommand() {
		IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
		
		Event event = new Event();
		event.data = annotationGrid;
		event.widget = natTable;
		try {
			handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.newColumn", event);
		}
		catch (Exception e1) {
			throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.newColumn not found!", e1);
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