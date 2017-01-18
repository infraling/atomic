/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.corpus_tools.atomic.tokeneditor.TableBasedTokenEditor;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractUiBindingConfiguration;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.layer.IUniqueIndexLayer;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.ui.NatEventData;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.MouseEventMatcher;
import org.eclipse.nebula.widgets.nattable.ui.menu.IMenuItemProvider;
import org.eclipse.nebula.widgets.nattable.ui.menu.MenuItemProviders;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuAction;
import org.eclipse.nebula.widgets.nattable.ui.menu.PopupMenuBuilder;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class EditorPopupMenuConfiguration extends AbstractUiBindingConfiguration {

	private final Menu menu;
	private final SDocumentGraph graph;

	public EditorPopupMenuConfiguration(NatTable natTable) {
		this.graph = (SDocumentGraph) natTable.getData(TableBasedTokenEditor.DATA_GRAPH);
		this.menu = new PopupMenuBuilder(natTable).withMenuItemProvider("newTokenPopupMenu", new NewTokenMenuItemProvider()).build();
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		uiBindingRegistry.registerMouseDownBinding(new MouseEventMatcher(SWT.NONE, GridRegion.BODY, MouseEventMatcher.RIGHT_BUTTON), new PopupMenuAction(this.menu));
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
					NatEventData natEventData = MenuItemProviders.getNatEventData(e);
					NatTable natTable = natEventData.getNatTable();
					int columnPosition = natEventData.getColumnPosition();
					int rowPosition = natEventData.getRowPosition();

					System.err.println("Selected token: " + natTable.getDataValueByPosition(columnPosition, rowPosition));
					int absoluteTokenIndex = LayerUtil.convertColumnPosition(natTable, columnPosition, (IUniqueIndexLayer) natTable.getData("dataLayer"));
					System.err.println("SELECTED TOKEN : " + graph.getText(graph.getSortedTokenByText().get(absoluteTokenIndex)));
				}
			});
		}

	}
}
