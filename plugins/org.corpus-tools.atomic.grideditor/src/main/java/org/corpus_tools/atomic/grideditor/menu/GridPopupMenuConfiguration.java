/**
 * 
 */
package org.corpus_tools.atomic.grideditor.menu;

import java.util.Collection;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
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

	private static final String MENU_NEW_COLUMN = "New annotation column";
	private static final String MENU_CREATE_SPAN = "Create span";
	private final Menu menu;
	private final SelectionLayer selectionLayer;
	private final AnnotationGrid grid;

    public GridPopupMenuConfiguration(final NatTable natTable, final AnnotationGrid grid, final SelectionLayer selectionLayer) {
    	this.grid = grid;
    	this.selectionLayer = selectionLayer;
        this.menu = new PopupMenuBuilder(natTable)
        		.withMenuItemProvider(MENU_NEW_COLUMN, new NewAnnotationColumnMenuItemProvider())
                .withMenuItemProvider(MENU_CREATE_SPAN, new CreateSpanMenuItemProvider())
                .withVisibleState(MENU_CREATE_SPAN, new MultiEmptyCellSelectionInOneColumnMenuItemState())
                .build();
    }

    @Override
    public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
        uiBindingRegistry.registerMouseDownBinding(
                new MouseEventMatcher(SWT.NONE, null, 3),
                new PopupMenuAction(this.menu));
    }

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class NewAnnotationColumnMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("New annotation column");
            menuItem.setEnabled(true);

            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    executeCreateAnnotationColumn();
                }

				private void executeCreateAnnotationColumn() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					
					Event event = new Event();
					event.data = grid;
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.newAnnotationColumn", event);
					}
					catch (Exception e1) {
						throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.newAnnotationColumn not found!", e1);
					}
				}
            });
		}
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class CreateSpanMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
            MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Create span");
            menuItem.setEnabled(true);

			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					executeCreateSpanCommand();
				}

				private void executeCreateSpanCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);

					Event event = new Event();
					event.data = grid;
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.createSpan", event);
					}
					catch (Exception e1) {
						throw new RuntimeException(
								"Command org.corpus_tools.atomic.grideditor.commands.createSpan not found!", e1);
					}
				}
			});
		}

	}

	/**
	 * An {@link IMenuItemState} which is active only when
	 * more than one cells in the {@link NatTable} are
	 * selected, and all of the selected cells are in a
	 * single column.
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class MultiEmptyCellSelectionInOneColumnMenuItemState implements IMenuItemState {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(
		 * org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();
			int noOfSelCells = selectedCells.size();
			int noOfSelCols = selectionLayer.getSelectedColumnPositions().length;
			final boolean allCellsEmpty = selectedCells.stream().allMatch(c -> {
				Object val = c.getDataValue();
				/*
				 * Checks whether cell data is either `null`, or if it isn't,
				 * then if it is an SAnnnotation, and if it is,
				 * then if the annotation value is either `null` or an empty String.
				 */
				if (val == null || (val instanceof SAnnotation && (((SAnnotation) val).getValue() == null) || ((SAnnotation) val).getValue_STEXT().isEmpty())) {
					return true;
				}
				return false;
			});
			return noOfSelCells > 1 && noOfSelCols == 1 && allCellsEmpty;
		}

	}

}
//}