/**
 * 
 */
package org.corpus_tools.atomic.grideditor.data.annotationgrid;

import java.util.Collections; 
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class AnnotationGrid {

	/**
		 * @author Stephan Druskat
		 * 
		 * // TODO Add description
		 *
		 */
	public class Cell {

		private Object object;
		private String header;

		public Cell(Object object, String header) {
			this.object = object;
			this.header = header;
		}

		/**
		 * @return the object
		 */
		public Object getObject() {
			return object;
		}

		/**
		 * @return the header
		 */
		public String getHeader() {
			return header;
		}

	}

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
		return row.get(colIndex).getObject();
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
	
		Map<Integer, Cell> cells = new HashMap<>();
	
		public void put(int colIndex, String header, Object object) {
			// Check if header already exists
			if (headerMap.inverse().containsKey(header)) {
				colIndex = headerMap.inverse().get(header);
			}
			headerMap.put(colIndex, header);
			cells.put(colIndex, new Cell(object, header));
		}
	
		public Cell get(int colIndex) {
			return cells.get(colIndex);
		}
		
		public String getHeader(int colIndex) {
			return cells.get(colIndex).getHeader();
		}
		
		public Object getObject(int colIndex) {
			return cells.get(colIndex).getObject();
		}

		public Map<Integer, Cell> getCells() {
			return cells;
		}
	
	}

	public void layout() {
		Map<java.lang.Integer, Row> sortedMap = sortByValue(rowMap);
		Iterator<Entry<Integer, Row>> it = sortedMap.entrySet().iterator();
		while (it.hasNext()) {
			Entry<Integer, Row> current = it.next();
			Row currentRow = current.getValue();
			if (headerMap.size() != currentRow.getCells().size()) {
				Map<String, Object> cellHeaderMap = currentRow.getCells().entrySet().stream().collect(Collectors.toMap(e -> e.getValue().getHeader(), e -> e.getValue().getObject()));
				for (Entry<String, Integer> headerEntry : headerMap.inverse().entrySet()) {
					currentRow.getCells().put(headerEntry.getValue(), new Cell(cellHeaderMap.get(headerEntry.getKey()), headerEntry.getKey()));
				}
			}
		}
	}
	
	public static <Integer, Row extends Comparable<? super AnnotationGrid.Row>> Map<Integer, AnnotationGrid.Row> sortByValue(Map<Integer, AnnotationGrid.Row> map) {
		List<Map.Entry<Integer, AnnotationGrid.Row>> list = new LinkedList<>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<Integer, AnnotationGrid.Row>>() {
			@Override
			public int compare(Map.Entry<Integer, AnnotationGrid.Row> e1, Map.Entry<Integer, AnnotationGrid.Row> e2) {
				int e1Size = e1.getValue().getCells().size();
				int e2Size = e2.getValue().getCells().size();
				return e2Size - e1Size;
			}
		});

		Map<Integer, AnnotationGrid.Row> result = new LinkedHashMap<>();
		for (Map.Entry<Integer, AnnotationGrid.Row> entry : list) {
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	}
	
}
