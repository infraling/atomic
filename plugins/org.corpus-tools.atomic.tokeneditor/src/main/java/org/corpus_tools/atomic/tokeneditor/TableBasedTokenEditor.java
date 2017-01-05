/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.List;

import org.corpus_tools.atomic.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.tokeneditor.data.TokenListDataProvider;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
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
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import com.gs.collections.impl.list.Interval;

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
		// TODO Auto-generated constructor stub
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
		DataLayer bodyDataLayer = new DataLayer(dataProvider);
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
		NatTable natTable = new NatTable(parent, compositeLayer);

		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);
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
