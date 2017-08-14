/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.gui.TokenSplitDialog;
import org.corpus_tools.atomic.grideditor.gui.TokenTextInputDialog;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SSequentialRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class SplitTokenHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		final int clickIndex = clickedCell.getRowIndex();
		/* 
		 * Must be a token, otherwise the command couldn't have
		 * been fired due to MenuItemState restrictions (checks
		 * whether clicked cell contains a token).
		 */
		SToken clickedToken = (SToken) clickedCell.getDataValue();
		final String tokenText = grid.getGraph().getText(clickedToken);
		TreeSet<Integer> splitPositions = null;
		TokenSplitDialog splitDial = new TokenSplitDialog(Display.getDefault().getActiveShell(), tokenText);
		if (splitDial.open() == Window.OK) {
			splitPositions = splitDial.getSplitPositions();
		}
		else {
			return null;
		}
		// Calculate length of segments
		List<Integer> segmentLengths = new ArrayList<>();
		int lastSegmentIndex = 0;
		for (Integer i : splitPositions) {
			segmentLengths.add(i - lastSegmentIndex);
			lastSegmentIndex = i;
		}
		segmentLengths.add(tokenText.length() - lastSegmentIndex);
		STextualRelation originalTextualRelation = ((STextualRelation) clickedToken.getOutRelations().stream().filter(r -> (r instanceof STextualRelation)).findFirst().get());
		final STextualDS ds = originalTextualRelation.getTarget();
		Integer startIndex = originalTextualRelation.getStart();
		List<SToken> tokensToAdd = new ArrayList<>();
		SToken token = null;
		// Create tokens
		for (int length : segmentLengths) {
			token = SaltFactory.createSToken();
			grid.getGraph().addNode(token);
			STextualRelation rel = SaltFactory.createSTextualRelation();
			rel.setSource(token);
			rel.setTarget(ds);
			rel.setStart(startIndex);
			rel.setEnd(startIndex + length);
			grid.getGraph().addRelation(rel);
			startIndex = startIndex + length;
			tokensToAdd.add(token);
		}
		grid.getGraph().removeNode(clickedToken);
		// Update grid
		Map<Integer, Row> tempRowMap = new HashMap<>();
		for (Iterator<Entry<Integer, Row>> iterator = grid.getRowMap().entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Row> entry = iterator.next();
			if (entry.getKey() >= clickIndex + 1) {
				tempRowMap.put(entry.getKey() + segmentLengths.size() - 1, entry.getValue());
				iterator.remove();
			}
		}
		grid.getRowMap().remove(clickIndex);
		for (int i = 0; i < tokensToAdd.size(); i++) {
			grid.record(clickIndex + i, 0, grid.getColumnHeaderMap().get(0), tokensToAdd.get(i));
		}
		tempRowMap.entrySet().stream().forEach(e -> grid.getRowMap().put(e.getKey(), e.getValue()));
		// Refresh and set dirty
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

}
