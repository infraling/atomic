/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public final class TokenHandlerUtil {

	private static final Logger log = LogManager.getLogger(TokenHandlerUtil.class);

	private final AnnotationGrid grid;
	private final int clickedIndex;
	private final SToken tokenToDelete;
	private final SDocumentGraph graph;

	/**
	 * // TODO Add description
	 * 
	 * @param purge
	 * @param cell
	 * @param grid
	 */
	public TokenHandlerUtil(ILayerCell cell, AnnotationGrid grid) {
		this.grid = grid;
		this.clickedIndex = cell.getRowIndex();
		this.tokenToDelete = (SToken) cell.getDataValue();
		this.graph = grid.getGraph();
	}

	/**
	 * // TODO Add description
	 * 
	 * @param purge
	 */
	public void deleteToken(boolean purge) {
		// Remove Row that has been selected for deletion
		grid.getRowMap().remove(clickedIndex);
		// Get a list of rows in the grid below the clicked cell
		List<Entry<Integer, Row>> remainingRows = collectRowsAfterRemoved(clickedIndex, grid.getRowMap());
		if (remainingRows.size() > 0) {
			log.trace("Deleting token {} in {}.", clickedIndex, graph.getDocument().getName());
			// Remove all rows after the clicked cell
			grid.getRowMap().entrySet().removeIf(entry -> entry.getKey() > clickedIndex);
			// Re-add remaining rows to grid with new index (-=1)
			remainingRows.stream().forEach(e -> {
				grid.getRowMap().put(e.getKey() - 1, e.getValue());
			});
		}
		else {
			log.trace("Deleting last token in {}.", graph.getDocument().getName());
		}
		if (purge) {
			List<SToken> sortedTokens = graph.getSortedTokenByText();
			// Get properties of deleted token
			int deletedTokenIndex = sortedTokens.indexOf(tokenToDelete);
			STextualDS dataSource = null;
			Integer deleteStart = null;
			Integer deleteEnd = null;
			relLoop: for (SRelation<?, ?> rel : tokenToDelete.getOutRelations()) {
				if (rel instanceof STextualRelation) {
					dataSource = (STextualDS) rel.getTarget();
					deleteStart = ((STextualRelation) rel).getStart();
					deleteEnd = ((STextualRelation) rel).getEnd();
					break relLoop;
				}
			}
			boolean first = false, last = false;
			// Token to delete is the first token
			if (deleteStart == 0) {
				first = true;
			}
			// Token to delete is the last token
			if (deleteEnd == dataSource.getText().length()) {
				last = true;
			}
			String wrappedTokenText = dataSource.getText().substring(deleteStart - (first ? 0 : 1), deleteEnd + (last ? 0 : 1));
			// If first token is followed by a whitespace, extend the text area to delete to token end index + 1
			if (first) {
				if (wrappedTokenText.endsWith(" ")) {
					deleteEnd += 1;
				}
			}
			// If last token is preceded by a whitespace, extend the text area to delete to token start index - 1
			else if (last) {
				if (wrappedTokenText.startsWith(" ")) {
					deleteStart -= 1;
				}
			}
			// If token is wrapped by whitespaces, extend the text area to delete to token start index - 1
			else {
				if (wrappedTokenText.startsWith(" ") && wrappedTokenText.endsWith(" ")) {
					deleteStart -= 1;
				}
			}
			final int deletedTokenLength = deleteEnd - deleteStart;
			// Update tokens in remaining rows with new indices
			/*
			 * Get all tokens following the deleted token in the ordered list of
			 * tokens, and for each of those, reset the start and end indices
			 * minus the length of the deleted token. TODO: What does this mean
			 * for the first/last token?
			 */
			IntStream.range(deletedTokenIndex + 1, sortedTokens.size()).mapToObj(i -> sortedTokens.get(i))
					.forEach(i -> {
						// Assert that each token only has exactly one textual
						// relation to a data source.
						@SuppressWarnings("rawtypes")
						List<SRelation> textualRels = i.getOutRelations().stream()
								.filter(r -> r instanceof STextualRelation).collect(Collectors.toList());
						assert textualRels.size() == 1;
						// Get the only one
						STextualRelation rel = (STextualRelation) textualRels.stream().findFirst().get();
						Integer oldStart = rel.getStart();
						Integer oldEnd = rel.getEnd();
						rel.setStart(oldStart - deletedTokenLength);
						rel.setEnd(oldEnd - deletedTokenLength);
					});
			// Update STextualDS
			StringBuffer buf = new StringBuffer(dataSource.getText());
			buf.replace(deleteStart, deleteEnd, "");
			dataSource.setText(buf.toString());
		}
		// Delete the SToken from the graph
		grid.getGraph().removeNode(tokenToDelete);
	}

	/**
	 * Collects all {@link Row}s with indices > the one of the deleted row.
	 * 
	 * @param clickedIndex
	 * @param map
	 * @return TODO
	 */
	private List<Entry<Integer, Row>> collectRowsAfterRemoved(int clickedIndex, Map<Integer, Row> map) {
		return map.entrySet().stream().filter(e -> e.getKey() > clickedIndex).collect(Collectors.toList());
	}

}
