/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.Arrays;
import java.util.List;

import org.corpus_tools.atomic.editors.DocumentGraphEditor;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
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
		IDataProvider headerDataProvider = new TokenizationEditorTableColumnHeaderDataProvider();
		DataLayer headerDataLayer = new DataLayer(headerDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(headerDataLayer, viewportLayer, selectionLayer);

		// create the composition
		// set the region labels to make default configurations work, e.g.
		// selection
		CompositeLayer compositeLayer = new CompositeLayer(1, 2);
		compositeLayer.setChildLayer(GridRegion.COLUMN_HEADER, columnHeaderLayer, 0, 0);
		compositeLayer.setChildLayer(GridRegion.BODY, viewportLayer, 0, 1);

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
		return new ListDataProvider<>(tokens, new TokenColumnPropertyAccessor());
	}

	/**
	 * TODO Description
	 *
	 * @author Stephan Druskat <mail@sdruskat.net>
	 *
	 */
	public class TokenizationEditorTableColumnHeaderDataProvider extends DefaultColumnHeaderDataProvider {

		/**
		 * @param columnLabels
		 */
		public TokenizationEditorTableColumnHeaderDataProvider() {
			super(null);
		}

		@Override
		public String getColumnHeaderLabel(int columnIndex) {
			String propertyName = null;
			switch (columnIndex) {
			case 0:
				propertyName = "Token text";
				break;

			case 1:
				propertyName = "Offset";
				break;

			default:
				break;
			}
			return propertyName;
		}

		@Override
		public int getColumnCount() {
			return 2;
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
	public class TokenColumnPropertyAccessor implements IColumnPropertyAccessor<SToken> {

		private final List<String> propertyNames = Arrays.asList("text", "offsets");

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

			return "";
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
			return propertyNames.get(columnIndex);
		}

		@Override
		public int getColumnIndex(String propertyName) {
			return propertyNames.indexOf(propertyName);
		}
	}

}
