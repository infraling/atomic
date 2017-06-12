/*******************************************************************************
 * Copyright (c) 2016, 2017 Stephan Druskat
 * Exploitation rights belong exclusively to Humboldt-Universität zu Berlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.grideditor.data.annotationgrid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * A custom data structure for annotation data.
 * 
 * In an annotation grid, {@link SToken} and
 * {@link SSpan} {@link SAnnotation}s are mapped
 * to a grid.
 * 
 * The layout of the grid is as follows.
 * 
 * | Row index = index of token in list of graph's sorted tokens | Token text | Token annotation key  1 (random order) | Token annotation key 2 | Token annotation key n | Span annotation key 1 (random order) | Span annotation  key 2 | Span annotation key n |
 * |-------------------------------------------------------------|------------|----------------------------------------|------------------------|------------------------|--------------------------------------|------------------------|-----------------------|
 * | {@link Integer} | {@link String} | {@link SAnnotation} | {@link SAnnotation} | {@link SAnnotation} | {@link SAnnotation} | {@link SAnnotation} | {@link SAnnotation} |
 *
 * @author Stephan Druskat
 */
public class AnnotationGrid {

	Map<Integer, Row> rowMap = new HashMap<>();
	BiMap<Integer, String> headerMap = HashBiMap.create();
	private final SDocumentGraph graph;

	public AnnotationGrid(SDocumentGraph graph) {
		this.graph = graph;
	}

	/**
	 * Top-level method for adding content to the grid.
	 * 
	 * Takes row and column indices, a header for the
	 * column in which the cell to be constructed down
	 * the line should be placed, and the value object.
	 * 
	 * If no {@link Row} exists at the respective row
	 * index, a new one is initialized, otherwise, the
	 * existing {@link Row} is used. The arguments
	 * `colIndex`, `header` and `value` are then passed
	 * to {@link Row#put(int, String, Object)}, and the
	 * row put (back) in the {@link #rowMap}.
	 * 
	 * @param rowIndex The row index for the passed value.
	 * @param colIndex The column index for the passed value.
	 * @param header The column header for the passed value.
	 * @param value The value object.
	 */
	public void record(int rowIndex, int colIndex, String header, Object value) {
		Row row = rowMap.get(rowIndex);
		if (row == null) {
			row = new Row();
		}
		row.put(colIndex, header, value);
		rowMap.put(rowIndex, row);
	}

	/**
	 * Retrieves the value object from the cell
	 * located at the give row index and column
	 * index.
	 * 
	 * @param rowIndex The row index for the value to be retrieved.
	 * @param colIndex The column index for the value to be retrieved.
	 * @return The value of the cell which is located at rowIndex:colIndex in the grid.
	 */
	public Object get(int rowIndex, int colIndex) {
		Row row = rowMap.get(rowIndex);
		if (row.get(colIndex) == null) {
			return null;
		}
		return row.get(colIndex).getValue();
	}

	/**
	 * @return the headerMap
	 */
	public Map<Integer, String> getHeaderMap() {
		return headerMap;
	}

	/**
	 * @return the rowMap
	 */
	public Map<Integer, Row> getRowMap() {
		return rowMap;
	}

	/**
	 * Lays out the annotation grid so that it can be used
	 * for display.
	 * 
	 * Before this method is run for the first time, there
	 * may be {@link Row}s in the annotation grid which
	 * contain less cells than others. This is due to the
	 * fact the the grid is constructed row by row, and
	 * the process will not know about the contents of
	 * any other row. Hence it is necessary to evaluate
	 * whether all rows contain cells for all existing
	 * column headers across the grid.
	 * 
	 * The method iterates over the {@link #rowMap} and
	 * checks whether for each row, there is a cell for
	 * each column header (using the {@link #headerMap},
	 * which at this point must be complete, i.e., this
	 * method should only be run after all values have 
	 * been added to the grid, and re-run after each 
	 * change to the grid's contents; also using a
	 * newly streamed map from headers to cell values
	 * for each {@link Row}).
	 * 
	 * If there is no cell for a column header, a new
	 * cell is added to the {@link Row}, using the
	 * respective index for the column header from the
	 * {@link #headerMap}, the value from the created
	 * header-to-cell-value map, and the header from
	 * the {@link #headerMap}. 
	 */
	public void layout() {
		Iterator<Entry<Integer, Row>> it = rowMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Row> current = it.next();
			Row currentRow = current.getValue();
			if (headerMap.size() != currentRow.getCells().size()) {
				Map<String, Object> cellHeaderMap = currentRow.getCells().entrySet().stream().collect(Collectors.toMap(e -> e.getValue().getColumnHeader(), e -> e.getValue().getValue()));
				for (Entry<String, Integer> headerEntry : headerMap.inverse().entrySet()) {
					currentRow.getCells().put(headerEntry.getValue(), new Cell(cellHeaderMap.get(headerEntry.getKey()), headerEntry.getKey()));
				}
			}
		}
	}

	/**
	 * @author Stephan Druskat
	 * 
	 * Objects of this type represent rows in an
	 * annotation grid of type {@link AnnotationGrid}.
	 * 
	 * They are backed by a map of cells ({@link #cells}),
	 * which is keyed on the position of the row in the grid.
	 */
	public class Row {
	
		private final Map<Integer, Cell> cells = new HashMap<>();
	
		/**
		 * Adds cells to this row.
		 * 
		 * First, the method performs a check whether the given
		 * column header already exists in the global header map.
		 * If so, `colIndex` is overwritten with the column
		 * index from the global header map for pre-sorting.
		 * Otherwise, the passed column header is added to the
		 * header map with the passed column index as key.
		 * 
		 * Subsequently, a new {@link Cell} is constructed
		 * with the passed column index and the passed
		 * column header, and keyed with the column index.
		 * 
		 * @param colIndex The index of the column where the cell should be located in the grid (this may change during layout).
		 * @param colHeader The header of the column the constructed cell should be in.
		 * @param value The cell value.
		 */
		public void put(int colIndex, String colHeader, Object value) {
			// Check if columnHeader already exists
			if (headerMap.inverse().containsKey(colHeader)) {
				colIndex = headerMap.inverse().get(colHeader);
			} 
			else { 
				headerMap.put(colIndex, colHeader);
			}
			cells.put(colIndex, new Cell(value, colHeader));
		}
	
		/**
		 * @param colIndex A column index.
		 * @return The {@link Cell} at the respective column index.
		 */
		public Cell get(int colIndex) {
			return cells.get(colIndex);
		}
		
		/**
		 * @param colIndex A column index.
		 * @return The header of the {@link Cell} at the respective column index.
		 */
		public String getHeader(int colIndex) {
			return cells.get(colIndex).getColumnHeader();
		}
		
		/**
		 * @param colIndex A column index.
		 * @return The value object of the {@link Cell} at the respective column index.
		 */
		public Object getValue(int colIndex) {
			return cells.get(colIndex).getValue();
		}

		/**
		 * @return The map of cells for this row, keyed by index.
		 */
		public Map<Integer, Cell> getCells() {
			return cells;
		}
	
	}

	/**
	 * @author Stephan Druskat
	 * 
	 * A bean representing a cell in an annotation grid of
	 * type {@link AnnotationGrid}.
	 * 
	 * A cell contains a value and is sorted under
	 * a column header.
	 */
	public class Cell {
	
		private Object value;
		private String columnHeader;
	
		public Cell(Object value, String columnHeader) {
			this.value = value;
			this.columnHeader = columnHeader;
		}
	
		/**
		 * @return the value
		 */
		public Object getValue() {
			return value;
		}
	
		/**
		 * @return the columnHeader
		 */
		public String getColumnHeader() {
			return columnHeader;
		}
	
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

}
