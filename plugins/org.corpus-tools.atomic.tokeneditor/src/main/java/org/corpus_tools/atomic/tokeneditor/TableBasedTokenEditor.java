/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.tokeneditor.accessors.TokenRowPropertyAccessor;
import org.corpus_tools.atomic.tokeneditor.configuration.EditorPopupMenuConfiguration;
import org.corpus_tools.atomic.tokeneditor.configuration.TokenEditorSelectionConfiguration;
import org.corpus_tools.atomic.tokeneditor.data.TokenListDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenColumnHeaderDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenRowHeaderDataProvider;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.LayerUtil;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.ColumnSelectionEvent;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TableBasedTokenEditor extends DocumentGraphEditor {

	ISpanningDataProvider dataProvider;

	/**
	 * 
	 */
	public TableBasedTokenEditor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.corpus_tools.atomic.editors.DocumentGraphEditor#createEditorPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		// build the body layer stack
		dataProvider = createDataProvider();
		final DataLayer bodyDataLayer = new DataLayer(dataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer, false);
		selectionLayer.addConfiguration(new TokenEditorSelectionConfiguration(graph));
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// build the column header layer stack
		IDataProvider colHeaderDataProvider = new TokenColumnHeaderDataProvider(graph);
		DataLayer colHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(colHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer stack
		IDataProvider rowHeaderDataProvider = new TokenRowHeaderDataProvider(dataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);
		ILayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer, columnHeaderLayer);

		GridLayer compositeLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		final NatTable natTable = new NatTable(parent, compositeLayer, false);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		// Set graph for retrieval via NatTable#getData() method later on.
		natTable.setData("graph", graph);
		// Set dataLayer for retrieval via NatTable#getData() method later on.
		natTable.setData("dataLayer", bodyDataLayer);
		// Set dataLayer for retrieval via NatTable#getData() method later on.
		natTable.setData("selectionLayer", selectionLayer);

		natTable.addConfiguration(new EditorPopupMenuConfiguration(natTable));
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.configure();

		// Handle selection
		natTable.addLayerListener(new ILayerListener() {

			// Default selection behavior selects cells by default.
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof CellSelectionEvent) {
					CellSelectionEvent cellEvent = (CellSelectionEvent) event;
					selectToken(cellEvent.getColumnPosition());
				}
				else if (event instanceof ColumnSelectionEvent) {
					ColumnSelectionEvent colEvent = (ColumnSelectionEvent) event;
					List<Range> rangesList = new ArrayList<>(colEvent.getColumnPositionRanges());
					Range range = rangesList.get(0);
					Set<Integer> members = range.getMembers();
					if (members.size() == 1) {
						selectToken(new ArrayList<>(members).get(0));
					}
				}
			}

			private void selectToken(int colPos) {
				// transform the NatTable column position to the row position
				// of the body layer stack
				int absoluteColPos = LayerUtil.convertColumnPosition(natTable, colPos, bodyDataLayer);
				SToken token = graph.getSortedTokenByText().get(absoluteColPos);
				natTable.setData("selectedToken", token);
			}
		});
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private ISpanningDataProvider createDataProvider() {
		final List<SToken> tokens = graph.getSortedTokenByText();
		return new TokenListDataProvider(tokens, new TokenRowPropertyAccessor(graph));
	}

}
