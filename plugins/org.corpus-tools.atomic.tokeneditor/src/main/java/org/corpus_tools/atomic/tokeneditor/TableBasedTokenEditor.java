/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.tokeneditor.data.TokenListDataProvider;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.collections.impl.list.Interval;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
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

	IDataProvider dataProvider;
	ISpanningDataProvider spanningDataProvider;
	// private BodyLayerStack bodyLayer;

	/**
	 * 
	 */
	public TableBasedTokenEditor() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.corpus_tools.atomic.editors.DocumentGraphEditor#
	 * createEditorPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		// build the body layer stack
		dataProvider = createDataProvider();
		final DataLayer bodyDataLayer = new DataLayer(dataProvider);
		SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer);
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// build the column header layer stack
		IDataProvider colHeaderDataProvider = new TokenColumnHeaderDataProvider();
		DataLayer colHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(colHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer stack
		IDataProvider rowHeaderDataProvider = new TokenRowHeaderDataProvider(dataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);
		ILayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer, columnHeaderLayer);

		GridLayer compositeLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		
		final NatTable natTable = new NatTable(parent, compositeLayer);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
		
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
						System.err.println(members);
						selectToken(new ArrayList<>(members).get(0));
					}
				}
			}
            
            private void selectToken(int colPos) {
            	// transform the NatTable column position to the row position
				// of the body layer stack
				int absoluteColPos = LayerUtil.convertColumnPosition(natTable, colPos, bodyDataLayer);
				SToken token = graph.getSortedTokenByText().get(absoluteColPos);
				int start = 0, end = 0;
				for (SRelation rel : ((SNode) token).getOutRelations()) {
					if (rel instanceof STextualRelation) {
						start = ((STextualRelation) rel).getStart();
						end = ((STextualRelation) rel).getEnd();
					}
				}
//				SToken newToken = graph.createToken(graph.getTextualDSs().get(0), start + 1, end + 1);
//				System.err.println("TOKEN SELECTED: >" + graph.getText(token) + "<");
				setDirty(true);
            }
		});
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private IDataProvider createDataProvider() {
		final List<SToken> tokens = graph.getSortedTokenByText();
		return new TokenListDataProvider(tokens, new TokenRowPropertyAccessor());
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class TokenColumnHeaderDataProvider extends DefaultColumnHeaderDataProvider {
		
		private List<Integer> tokenIndices = Interval.zeroTo(graph.getTokens().size() - 1);

		/**
		 * @param columnLabels
		 */
		public TokenColumnHeaderDataProvider() {
			super(null);
		}

		@Override
		public String getColumnHeaderLabel(int columnIndex) {
			return tokenIndices.get(columnIndex).toString();
		}

		@Override
		public int getColumnCount() {
			return graph.getTokens().size();
		}

		@Override
		public int getRowCount() {
			return 1;
		}

		/**
		 * This class does not support multiple rows in the column header layer.
		 */
		@Override
		public Object getDataValue(int columnIndex, int rowIndex) {
			return getColumnHeaderLabel(columnIndex);
		}

		@Override
		public void setDataValue(int columnIndex, int rowIndex, Object newValue) {
			throw new UnsupportedOperationException();
		}

	}

	/**
		 * TODO Description
		 *
		 * @author Stephan Druskat <mail@sdruskat.net>
		 *
		 */
	public class TokenRowHeaderDataProvider extends DefaultRowHeaderDataProvider {

		/**
		 * @param bodyDataProvider
		 */
		public TokenRowHeaderDataProvider(IDataProvider bodyDataProvider) {
			super(bodyDataProvider);
			// TODO Auto-generated constructor stub
		}
		
		@Override
	    public int getColumnCount() {
	        return 1;
	    }

	    @Override
	    public int getRowCount() {
	        return 2;
	    }

	    @Override
	    public Object getDataValue(int columnIndex, int rowIndex) {
	    	switch (rowIndex) {
			case 0:
				return "Token text";
				
			case 1:
				return "Token index";

			default:
				return null;
			}
	    }
	
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class TokenRowPropertyAccessor implements IColumnPropertyAccessor<SToken> {

//		private final List<String> propertyNames = Arrays.asList("text", "offsets");

		@Override
		public Object getDataValue(SToken token, int columnIndex) {
			switch (columnIndex) {
			case 0:
				return graph.getText(token);

			case 1:
				int start = 0, end = 0;
				for (SRelation outRel : ((SNode) token).getOutRelations()) {
					if (outRel instanceof STextualRelation) {
						start = ((STextualRelation) outRel).getStart();
						end = ((STextualRelation) outRel).getEnd();
						return start + " - " + end;
					}
				}
				break;

			default:
				break;
			}

			return null;
		}

		@Override
		public void setDataValue(SToken token, int columnIndex, Object newValue) {
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnProperty(int columnIndex) {
			return null;
		}

		@Override
		public int getColumnIndex(String propertyName) {
			return -1;
		}
	}

	

}
