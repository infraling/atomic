/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class DeleteTokenHandler extends AbstractHandler {
	
	private static final Logger log = LogManager.getLogger(DeleteTokenHandler.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		SToken tokenToDelete = (SToken) clickedCell.getDataValue();
		SDocumentGraph graph = grid.getGraph();
		// Update grid
		grid.getRowMap().remove(clickedCell.getRowIndex());
		List<Entry<Integer, Row>> remainingTokens = grid.getRowMap().entrySet().stream().filter(e -> e.getKey() > clickedCell.getRowIndex()).collect(Collectors.toList());
		grid.getRowMap().entrySet().removeIf(row -> {return row.getKey() > clickedCell.getRowIndex();});
		remainingTokens.stream().forEach(e -> {
			grid.getRowMap().put(e.getKey() - 1, e.getValue());
		});
		// Update remaining tokens
		List<SToken> sortedTokens = graph.getSortedTokenByText();
		int tokenIndex = sortedTokens.indexOf(tokenToDelete);
		STextualDS ds = null;
		Integer start = null;
		Integer end = null;
		relLoop:
		for (SRelation rel : tokenToDelete.getOutRelations()) {
			if (rel instanceof STextualRelation) {
				ds = (STextualDS) rel.getTarget();
				start = ((STextualRelation) rel).getStart();
				end = ((STextualRelation) rel).getEnd();
				break relLoop;
			}
		}
		final int tokenLength = end - start;
		IntStream.range(tokenIndex + 1, sortedTokens.size()).mapToObj(i -> sortedTokens.get(i)).forEach(i -> {
			STextualRelation rel = (STextualRelation) i.getOutRelations().stream().filter(r -> r instanceof STextualRelation).findFirst().get();
			// FIXME: Correct by getting all STextualRels and check that size of list is 1
			Integer oldStart = rel.getStart();
			Integer oldEnd = rel.getEnd();
			rel.setStart(oldStart - tokenLength);
			rel.setEnd(oldEnd - tokenLength);
		});
		// Update STextualDS
		StringBuffer buf = new StringBuffer(ds.getText());
        buf.replace(start, end, ""); 
        ds.setText(buf.toString());
		grid.getGraph().removeNode((SNode) clickedCell.getDataValue());
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
