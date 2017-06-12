/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Event;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class MergeTokensHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		Collection<ILayerCell> selectedCells = (Collection<ILayerCell>) ((Object[]) ((Event) event.getTrigger()).data)[0];
		List<ILayerCell> cells = new ArrayList<>(selectedCells);
		TreeSet<Integer> indices = new TreeSet<>();
		for (ILayerCell c : cells) {
			indices.add(c.getRowIndex());
		}
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		// FIXME Use try catch for ClassCastExceptions for above code
		Collections.sort(cells, new Comparator<ILayerCell>() {
			@Override
			public int compare(ILayerCell o1, ILayerCell o2) {
				return Integer.valueOf(o1.getRowIndex()).compareTo(o2.getRowIndex());
			}
		});
		List<SToken> tokensToMerge = new ArrayList<>();
		cells.stream().forEach(c -> {
			tokensToMerge.add((SToken) c.getDataValue());
		});
		int start, end;
		// Get smallest index
		SToken first = (SToken) cells.get(0).getDataValue();
		STextualRelation rel = (STextualRelation) first.getOutRelations().stream().filter(r -> r instanceof STextualRelation).findFirst().get();
		STextualDS ds = rel.getTarget();
		start = rel.getStart();
		// Get highest index
		SToken last = (SToken) cells.get(cells.size() - 1).getDataValue();
		rel = (STextualRelation) last.getOutRelations().stream().filter(r -> r instanceof STextualRelation).findFirst().get();
		end = rel.getEnd();
		// Create token
		SToken mergedToken = SaltFactory.createSToken();
		grid.getGraph().addNode(mergedToken);
		STextualRelation textRel = SaltFactory.createSTextualRelation();
		textRel.setSource(mergedToken);
		textRel.setTarget(ds);
		textRel.setStart(start);
		textRel.setEnd(end);
		grid.getGraph().addRelation(textRel);
		tokensToMerge.stream().forEach(t -> {
			grid.getGraph().removeNode(t);
		});
		for (ILayerCell cell : cells) {
			grid.getRowMap().remove(cell.getRowIndex());
			grid.record(cell.getRowIndex(), 0, grid.getHeaderMap().get(0), mergedToken);
		}
//		int lastMergedIndex = cells.get(cells.size() - 1).getRowIndex();
//		int lastIndex = grid.getRowMap().size() - 1;
//		for (int i = 1; i < cells.size(); i++) {
//			grid.getRowMap().remove(i);
//		}
		// Update grid
//		int secondCellIndex = cells.get(1).getRowIndex();
//		int lastCellIndex = cells.get(cells.size() - 1).getRowIndex();
//		int noOfCellsTouched = lastCellIndex - secondCellIndex;
//		
//		Map<Integer, Row> tempRowMap = new HashMap<>();
//		for (Iterator<Entry<Integer, Row>> iterator = grid.getRowMap().entrySet().iterator(); iterator.hasNext();) {
//			Entry<Integer, Row> entry = iterator.next();
//			if (entry.getKey() >= secondCellIndex + 1) {
//				tempRowMap.put(entry.getKey() - noOfCellsTouched, entry.getValue());
//				iterator.remove();
//			}
//		}
//		grid.getRowMap().remove(cells.get(0).getRowIndex());
//		grid.record(cells.get(0).getRowIndex(), 0, "Token", mergedToken);
//		tempRowMap.entrySet().stream().forEach(e -> grid.getRowMap().put(e.getKey(), e.getValue()));
//
//		
		table.refresh();
		System.err.println(grid.getGraph().getTokens().size());
		return null;
	}

}
