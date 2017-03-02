package org.corpus_tools.atomic.tokeneditor.providers;
import java.util.ArrayList;
import java.util.List;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenColumnHeaderDataProvider extends DefaultColumnHeaderDataProvider {

		private final List<Integer> tokenIndices;

		/**
		 * @param graph 
		 * @param columnLabels
		 */
		public TokenColumnHeaderDataProvider(SDocumentGraph graph) {
			super(null);
			List<SToken> tokens = graph.getTokens();
			this.tokenIndices = new ArrayList<>(tokens.size());
			for(int i=0; i < tokens.size(); i++) {
				this.tokenIndices.add(i);
			}
		}

		@Override
		public String getColumnHeaderLabel(int columnIndex) {
			return tokenIndices.get(columnIndex).toString();
		}

		@Override
		public int getColumnCount() {
			return tokenIndices.size();
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