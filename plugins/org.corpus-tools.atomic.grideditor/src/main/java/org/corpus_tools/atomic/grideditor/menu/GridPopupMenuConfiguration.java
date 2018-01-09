/**
 * 
 */
package org.corpus_tools.atomic.grideditor.menu;

import java.util.Collection;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
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
	private static final String MENU_DELETE_CLICKED_SPAN_ANNOTATION = "Delete clicked on span annotation";
	private static final String MENU_DELETE_SELECTED_SPAN_ANNOTATION = "Delete span annotation(s) in selection";
	private static final String MENU_SPLIT_CLICKED_SPAN_ANNOTATION = "Split clicked-on multi-cell span";
	private static final String MENU_MERGE_TO_SPAN = "Merge annotations into span";
	private static final String MENU_CREATE_TOKEN_AFTER_THIS = "Create token after the clicked one";
	private static final String MENU_CREATE_TOKEN_BEFORE_FIRST = "Create token before first";
	private static final String MENU_DELETE_TOKEN = "Delete token";
	private static final String MENU_PURGE_TOKEN = "Purge token";
	private static final String MENU_MERGE_TOKEN = "Merge token";
	private static final String MENU_SPLIT_TOKEN = "Split token";
	private final Menu menu;
	private final SelectionLayer selectionLayer;
	private final AnnotationGrid grid;
	private ILayerCell clickedCell;
	private Collection<ILayerCell> selectedCells = null;

    public GridPopupMenuConfiguration(final NatTable natTable, final AnnotationGrid grid, final SelectionLayer selectionLayer) {
    	this.grid = grid;
    	this.selectionLayer = selectionLayer;
        this.menu = new PopupMenuBuilder(natTable)
        		// Tokens
        		.withMenuItemProvider(MENU_CREATE_TOKEN_AFTER_THIS, new CreateTokenMenuItemProvider(false))
                .withVisibleState(MENU_CREATE_TOKEN_AFTER_THIS, new TokenMenuItemState())
                .withMenuItemProvider(MENU_CREATE_TOKEN_BEFORE_FIRST, new CreateTokenMenuItemProvider(true))
                .withVisibleState(MENU_CREATE_TOKEN_BEFORE_FIRST, new TokenMenuBeforeFirstItemState())
                .withMenuItemProvider(MENU_DELETE_TOKEN, new DeleteTokenMenuItemProvider())
                .withVisibleState(MENU_DELETE_TOKEN, new TokenMenuItemState())
                .withMenuItemProvider(MENU_PURGE_TOKEN, new PurgeTokenMenuItemProvider())
                .withVisibleState(MENU_PURGE_TOKEN, new TokenMenuItemState())
                .withMenuItemProvider(MENU_MERGE_TOKEN, new MergeTokenMenuItemProvider())
                .withVisibleState(MENU_MERGE_TOKEN, new MultiTokenSelectionMenuItemState())
                .withMenuItemProvider(MENU_SPLIT_TOKEN, new SplitTokenMenuItemProvider())
                .withVisibleState(MENU_SPLIT_TOKEN, new TokenMenuItemState())
        		// Spans
        		.withMenuItemProvider(MENU_NEW_COLUMN, new NewAnnotationColumnMenuItemProvider())
                .withMenuItemProvider(MENU_CREATE_SPAN, new CreateSpanMenuItemProvider())
                .withVisibleState(MENU_CREATE_SPAN, new MultiEmptyCellSelectionInOneColumnMenuItemState())
                .withMenuItemProvider(MENU_MERGE_TO_SPAN, new MergeCellsToSpanMenuItemProvider())
                .withVisibleState(MENU_MERGE_TO_SPAN, new MultiSpanCellSelectionMenuItemState())
                .withMenuItemProvider(MENU_SPLIT_CLICKED_SPAN_ANNOTATION, new SplitClickedSpanAnnotationMenuItemProvider())
                .withVisibleState(MENU_SPLIT_CLICKED_SPAN_ANNOTATION, new ClickedOnAnnotatedMultiCellSpanMenuItemState())
                .withMenuItemProvider(MENU_DELETE_CLICKED_SPAN_ANNOTATION, new DeleteClickedSpanAnnotationMenuItemProvider())
                .withVisibleState(MENU_DELETE_CLICKED_SPAN_ANNOTATION, new ClickedOnAnnotatedSpanMenuItemState())
                .withMenuItemProvider(MENU_DELETE_SELECTED_SPAN_ANNOTATION, new DeleteSelectedSpanAnnotationMenuItemProvider())
                .withVisibleState(MENU_DELETE_SELECTED_SPAN_ANNOTATION, new MultiSpanCellSelectionMenuItemState())
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
	public class CreateTokenMenuItemProvider implements IMenuItemProvider {
	
		private final boolean isFirstToken;

		public CreateTokenMenuItemProvider(boolean isLastToken) {
			this.isFirstToken = isLastToken;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
			if (!isFirstToken) {
				menuItem.setText("Create new token");
			}
			else {
				menuItem.setText("Create new first token");
			}
            menuItem.setEnabled(true);
            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    executeCreateAnnotationColumn();
                }

				private void executeCreateAnnotationColumn() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					
					Event event = new Event();
					event.data = new Object[]{clickedCell, grid, isFirstToken};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.newToken", event);
					}
					catch (Exception e1) {
						throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.newToken not found!", e1);
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
	public class DeleteTokenMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Delete token");
            menuItem.setEnabled(true);
            menuItem.addSelectionListener(new SelectionAdapter() {
            	@Override
            	public void widgetSelected(SelectionEvent event) {
            		executeDeleteTokenCommand();
            	}

				private void executeDeleteTokenCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					
					Event event = new Event();
					event.data = new Object[]{clickedCell, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.deleteToken", event);
					}
					catch (Exception e1) {
						throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.deleteToken not found!", e1);
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
public class PurgeTokenMenuItemProvider implements IMenuItemProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
	 */
	@Override
	public void addMenuItem(NatTable natTable, Menu popupMenu) {
		MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
        menuItem.setText("Purge token");
        menuItem.setEnabled(true);
        menuItem.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent event) {
        		executePurgeTokenCommand();
        	}

			private void executePurgeTokenCommand() {
				IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
				
				Event event = new Event();
				event.data = new Object[]{clickedCell, grid};
				event.widget = natTable;
				try {
					handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.purgeToken", event);
				}
				catch (Exception e1) {
					throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.purgeToken not found!", e1);
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
	public class MergeTokenMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Merge token");
            menuItem.setEnabled(true);

            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    executeMergeTokenCommand();
                }

				private void executeMergeTokenCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					
					Event event = new Event();
					event.data = new Object[]{selectedCells, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.mergeTokens", event);
					}
					catch (Exception e1) {
						throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.mergeTokens not found!", e1);
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
	public class SplitTokenMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Split token");
            menuItem.setEnabled(true);

            menuItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    executeSplitTokenCommand();
                }

				private void executeSplitTokenCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getService(IHandlerService.class);
					
					Event event = new Event();
					event.data = new Object[]{clickedCell, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.splitToken", event);
					}
					catch (Exception e1) {
						throw new RuntimeException("Command org.corpus_tools.atomic.grideditor.commands.splitToken not found!", e1);
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
            menuItem.setText("Create new span");
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
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class MergeCellsToSpanMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Merge to single span");
            menuItem.setEnabled(true);

			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					executeMergeCellsToSpanCommand();
				}

				private void executeMergeCellsToSpanCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);

					Event event = new Event(
							);
					if (selectedCells == null) {
						throw new RuntimeException("The collection of selected cells is null which shouldn't be the case!"); // FIME Use log, provide better info, etc.
					}
					event.data = new Object[]{selectedCells, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.mergeCellsToSpan", event);
					}
					catch (Exception e1) {
						throw new RuntimeException(
								"Command org.corpus_tools.atomic.grideditor.commands.mergeCellsToSpan not found!", e1);
					}
				}
			});
		}
	
	}

	/**
		 * // TODO Add description
		 * 
		 * TODO Check if this can be refactored to be used for token annotations as well
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class DeleteClickedSpanAnnotationMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Delete cell annotation");
            menuItem.setEnabled(true);

			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					executeDeleteSpanAnnotationCommand();
				}

				private void executeDeleteSpanAnnotationCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);

					Event event = new Event();
					if (clickedCell == null) {
						throw new RuntimeException("The clicked on object is null which shouldn't be the case!"); // FIME Use log, provide better info, etc.
					}
					event.data = new Object[]{clickedCell, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.deleteClickedSpanAnnotation", event);
					}
					catch (Exception e1) {
						throw new RuntimeException(
								"Command org.corpus_tools.atomic.grideditor.commands.deleteClickedSpanAnnotation not found!", e1);
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
	public class DeleteSelectedSpanAnnotationMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Delete selected");
            menuItem.setEnabled(true);

			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					executeDeleteSelectedSpanCommand();
				}

				private void executeDeleteSelectedSpanCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);

					Event event = new Event();
					if (selectedCells == null) {
						throw new RuntimeException("The collection of selected cells is null which shouldn't be the case!"); // FIME Use log, provide better info, etc.
					}
					event.data = new Object[]{selectedCells, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.deleteSelectedSpanAnnotation", event);
					}
					catch (Exception e1) {
						throw new RuntimeException(
								"Command org.corpus_tools.atomic.grideditor.commands.deleteSelectedSpanAnnotation not found!", e1);
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
	public class SplitClickedSpanAnnotationMenuItemProvider implements IMenuItemProvider {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider#addMenuItem(org.eclipse.nebula.widgets.nattable.NatTable, org.eclipse.swt.widgets.Menu)
		 */
		@Override
		public void addMenuItem(NatTable natTable, Menu popupMenu) {
			MenuItem menuItem = new MenuItem(popupMenu, SWT.PUSH);
            menuItem.setText("Split span");
            menuItem.setEnabled(true);

			menuItem.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					executeSplitSpanCommand();
				}

				private void executeSplitSpanCommand() {
					IHandlerService handlerService = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getService(IHandlerService.class);

					Event event = new Event();
					if (clickedCell == null) {
						throw new RuntimeException("The clicked on object is null which shouldn't be the case!"); // FIME Use log, provide better info, etc.
					}
					event.data = new Object[]{clickedCell, grid};
					event.widget = natTable;
					try {
						handlerService.executeCommand("org.corpus_tools.atomic.grideditor.commands.splitClickedSpanAnnotation", event);
					}
					catch (Exception e1) {
						throw new RuntimeException(
								"Command org.corpus_tools.atomic.grideditor.commands.splitClickedSpanAnnotation not found!", e1);
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
	public class TokenMenuItemState implements IMenuItemState {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			GridPopupMenuConfiguration.this.clickedCell = natEventData.getNatTable().getCellByPosition(natEventData.getColumnPosition(), natEventData.getRowPosition());
			Object value = clickedCell.getDataValue();
			return value instanceof SToken;
		}
	
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class TokenMenuBeforeFirstItemState implements IMenuItemState {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			GridPopupMenuConfiguration.this.clickedCell = natEventData.getNatTable().getCellByPosition(natEventData.getColumnPosition(), natEventData.getRowPosition());
			Object value = clickedCell.getDataValue();
			return value instanceof SToken && clickedCell.getRowIndex() == 0;
		}
	
	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class MultiTokenSelectionMenuItemState implements IMenuItemState {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();
			GridPopupMenuConfiguration.this.selectedCells = selectedCells;
			int noOfSelCells = selectedCells.size();
			int noOfSelCols = selectionLayer.getSelectedColumnPositions().length;
			return noOfSelCells > 1 && noOfSelCols == 1 && selectedCells.stream().allMatch(c -> {
				return c.getColumnIndex() == 0;
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
				 * Check whether cell data is either `null`, or if it isn't,
				 * then if it is an SAnnnotation, and if it is,
				 * then if the annotation value is either `null` or an empty String.
				 */
				if (val == null || (val instanceof SAnnotation && (((SAnnotation) val).getValue() == null) || (val instanceof SAnnotation && ((SAnnotation) val).getValue_STEXT().isEmpty()))) {
					return true;
				}
				return false;
			});
			return noOfSelCells > 1 && noOfSelCols == 1 && allCellsEmpty;
		}

	}

	/**
		 * // TODO Add description
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class MultiSpanCellSelectionMenuItemState implements IMenuItemState {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			Collection<ILayerCell> selectedCells = selectionLayer.getSelectedCells();
			/*
			 * Check whether at least one cell is selected, and whether the
			 * data value is not null and is an instance of SAnnotation and
			 * whether the annotation value is not null and not the empty String.
			 */
			if (selectedCells.size() > 0) {
				for (ILayerCell cell : selectedCells) {
					Object val = cell.getDataValue();
					if (val != null && (val instanceof SAnnotation && (((SAnnotation) val).getValue() != null) && !((SAnnotation) val).getValue_STEXT().isEmpty())) {
						GridPopupMenuConfiguration.this.selectedCells = selectedCells;
						return true;
					} 
				}
			}
			return false;
		}
	
	}

	/**
		 * // TODO Add description
		 * 
		 * Works only on the clicked on cell, not the selection
		 * 
		 * TODO implement for selection as extra menu item
		 *
		 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
		 * 
		 */
	public class ClickedOnAnnotatedSpanMenuItemState implements IMenuItemState {
	
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			GridPopupMenuConfiguration.this.clickedCell = natEventData.getNatTable().getCellByPosition(natEventData.getColumnPosition(), natEventData.getRowPosition());
			Object value = clickedCell.getDataValue();
			if (value != null && value instanceof SAnnotation && ((SAnnotation) value).getContainer() instanceof SSpan) {
				return true;
			}
			return false;
		}
	
	}
	
	public class ClickedOnAnnotatedMultiCellSpanMenuItemState implements IMenuItemState {
		
		/* (non-Javadoc)
		 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
		 */
		@Override
		public boolean isActive(NatEventData natEventData) {
			GridPopupMenuConfiguration.this.clickedCell = natEventData.getNatTable().getCellByPosition(natEventData.getColumnPosition(), natEventData.getRowPosition());
			Object value = clickedCell.getDataValue();
			if (value != null && value instanceof SAnnotation && ((SAnnotation) value).getContainer() instanceof SSpan && clickedCell.isSpannedCell()) {
				return true;
			}
			return false;
		}
	
	}
	

	// TODO Use for checking for cell selection
//	/* (non-Javadoc)
//	 * @see org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemState#isActive(org.eclipse.nebula.widgets.nattable.ui.NatEventData)
//	 */
//	@Override
//	public boolean isActive(NatEventData natEventData) {
//		boolean selectedCellsIncludeAnnotatedSpan = selectionLayer.getSelectedCells().stream().anyMatch(c -> {
//			Object val = c.getDataValue();
//			/*
//			 * Check whether any of the selected cells includes a non-empty
//			 * cell, and if they do,
//			 * then if its value is an SAnnotation, and if it is,
//			 * then whether the annotation container is a span.
//			 */
//			if (val != null && val instanceof SAnnotation && ((SAnnotation) val).getContainer() instanceof SSpan){// && (((SAnnotation) val).getValue() != null || !((SAnnotation) val).getValue_STEXT().isEmpty())) {
//				return true;
//			}
//			return false;
//		});
//		return selectedCellsIncludeAnnotatedSpan;
//	}


}
//}