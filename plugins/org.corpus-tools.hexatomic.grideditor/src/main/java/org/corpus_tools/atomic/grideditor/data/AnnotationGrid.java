/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class AnnotationGrid {

	Map<Integer, Row> rowMap = new HashMap<>();
	BiMap<Integer, String> headerMap = HashBiMap.create();

	public void record(int rowIndex, int colIndex, String header, Object object) {
		Row row = rowMap.get(rowIndex);
		if (row == null) {
			row = new Row();
		}
		row.put(colIndex, header, object);
		rowMap.put(rowIndex, row);
	}

	public Object get(int rowIndex, int colIndex) {
		Row row = rowMap.get(rowIndex);
		return row.get(colIndex);
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

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < rowMap.size(); i++) {
			Row row = rowMap.get(i);
			for (int j = 0; j < row.cells.size(); j++) {
				Object cell = row.cells.get(i);
				if (cell != null)
				sb.append(" | " + cell.toString());
			}
			sb.append(" |\n");
		}
		return sb.toString();
	}

	/**
	 * @author Stephan Druskat
	 * 
	 *         // TODO Add description
	 *
	 */
	public class Row {
	
		Map<Integer, Object> cells = new HashMap<>();
	
		public void put(int colIndex, String header, Object object) {
			// Check if header already exists
			if (headerMap.inverse().containsKey(header)) {
				colIndex = headerMap.inverse().get(header);
			}
			headerMap.put(colIndex, header);
			cells.put(colIndex, object);
		}
	
		public Object get(int colIndex) {
			return cells.get(colIndex);
		}
	
	}

}
