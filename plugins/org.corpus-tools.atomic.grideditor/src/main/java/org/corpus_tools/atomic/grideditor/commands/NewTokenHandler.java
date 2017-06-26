/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid.Row;
import org.corpus_tools.atomic.grideditor.gui.TokenTextInputDialog;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.graph.Node;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.window.Window;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ISetSelectionTarget;

/**
 * // TODO Add description
 * Adds a token before the clicked cell, unless addafterlast.
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class NewTokenHandler extends AbstractHandler {

	/**
	 * - Get new token text
	 * - Create token on graph
	 * - Change DS
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		ILayerCell clickedCell = (ILayerCell) ((Object[]) ((Event) event.getTrigger()).data)[0];
		final int clickIndex = clickedCell.getRowIndex();
		/* 
		 * Must be a token, otherwise the command couldn't have
		 * been fired due to MenuItemState restrictions (checks
		 * whether clicked cell contains a token).
		 */
		SToken clickedToken = (SToken) clickedCell.getDataValue();
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		SDocumentGraph graph = grid.getGraph();
		boolean addBeforeFirst = (boolean) ((Object[]) ((Event) event.getTrigger()).data)[2];
		// Query token text
		String newTokenText = null;
		boolean addWhitespace = false;
		TokenTextInputDialog inputDial = new TokenTextInputDialog(Display.getDefault().getActiveShell());
		if (inputDial.open() == Window.OK) {
			newTokenText = inputDial.getValue().trim();
			addWhitespace = inputDial.addWhitespace();
		}
		else {
			return null;
		}
		int realTokenLength = newTokenText.length();
		List<SRelation> tokenOutRelations = clickedToken.getOutRelations();
		STextualRelation textRel = null;
		relLoop:
		for (SRelation rel : tokenOutRelations) {
			if (rel instanceof STextualRelation) {
				textRel = (STextualRelation) rel;
				break relLoop;
			}
		}
		// Change STextualDS
		STextualDS ds = textRel.getTarget(); 
		if (!addBeforeFirst) {
			int startIndex = textRel.getEnd();
			String originalText = ds.getText();
			boolean followedByWhitespace = originalText.substring(startIndex).startsWith(" ");
			int endIndex = startIndex
					+ (addWhitespace ? realTokenLength + (followedByWhitespace ? 1 : 2) : realTokenLength);
			int textToAddLength = endIndex - startIndex;
			String textToAdd = (addWhitespace ? " " : "") + newTokenText
					+ (followedByWhitespace ? "" : (addWhitespace ? " " : ""));
			String newText = new StringBuilder(originalText).insert(startIndex, textToAdd).toString();
			ds.setText(newText);
			// Change indices for following tokens
			List<SToken> sortedTokens = graph.getSortedTokenByText();
			List<SToken> tokenChangeList = sortedTokens.subList(clickedCell.getRowIndex() + 1,
					graph.getSortedTokenByText().size());
			tokenChangeList.stream().forEach(t -> {
				t.getOutRelations().forEach(r -> {
					if (r instanceof STextualRelation) {
						Integer oldStart = ((STextualRelation) r).getStart();
						Integer oldEnd = ((STextualRelation) r).getEnd();
						((STextualRelation) r).setStart(oldStart + textToAddLength);
						((STextualRelation) r).setEnd(oldEnd + textToAddLength);
					}
				});
			});
			// Create new token
			SToken newToken = createToken(startIndex, realTokenLength, ds, graph, addWhitespace, addBeforeFirst, clickedToken);
			// Update grid
			Map<Integer, Row> tempRowMap = new HashMap<>();
			for (Iterator<Entry<Integer, Row>> iterator = grid.getRowMap().entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, Row> entry = iterator.next();
				if (entry.getKey() >= clickIndex + 1) {
					tempRowMap.put(entry.getKey() + 1, entry.getValue());
					iterator.remove();
				}
			}
			grid.getRowMap().remove(clickIndex + 1);
			grid.record(clickIndex + 1, 0, "Token", newToken);
			tempRowMap.entrySet().stream().forEach(e -> grid.getRowMap().put(e.getKey(), e.getValue()));
		}
		else {
			int startIndex = 0;
			String originalText = ds.getText();
			boolean followedByWhitespace = originalText.substring(startIndex).startsWith(" ");
			int endIndex = startIndex + (addWhitespace ? realTokenLength + (followedByWhitespace ? 0 : 1) : realTokenLength);
			int textToAddLength = endIndex - startIndex;
			String textToAdd = newTokenText + (followedByWhitespace ? "" : (addWhitespace ? " " : ""));
			String newText = new StringBuilder(originalText).insert(startIndex, textToAdd).toString();
			ds.setText(newText);
			// Change indices for following tokens
			List<SToken> sortedTokens = graph.getSortedTokenByText();
			// FIXME: Use intStream as in DeleteTokenHandler to use fromm tokenindex + 1!
			sortedTokens.stream().forEach(t -> {
				t.getOutRelations().forEach(r -> {
					if (r instanceof STextualRelation) {
						Integer oldStart = ((STextualRelation) r).getStart();
						Integer oldEnd = ((STextualRelation) r).getEnd();
						((STextualRelation) r).setStart(oldStart + textToAddLength);
						((STextualRelation) r).setEnd(oldEnd + textToAddLength);
					}
				});
			});
			// Create new token
			SToken newToken = createToken(startIndex, realTokenLength, ds, graph, addWhitespace, addBeforeFirst, clickedToken);
			// Update grid
			Map<Integer, Row> tempRowMap = new HashMap<>();
			for (Iterator<Entry<Integer, Row>> iterator = grid.getRowMap().entrySet().iterator(); iterator.hasNext();) {
				Entry<Integer, Row> entry = iterator.next();
				tempRowMap.put(entry.getKey() + 1, entry.getValue());
				iterator.remove();
			}
			grid.record(0, 0, "Token", newToken);
			tempRowMap.entrySet().stream().forEach(e -> grid.getRowMap().put(e.getKey(), e.getValue()));
		}
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

	private SToken createToken(int startIndex, int realTokenLength, STextualDS ds, SDocumentGraph graph, boolean addWhitespace, boolean addBeforeFirst, SToken clickedToken) {
		SToken token = SaltFactory.createSToken();
		graph.addNode(token);
		STextualRelation textRel = SaltFactory.createSTextualRelation();
		textRel.setSource(token);
		textRel.setTarget(ds);
		int start = addWhitespace ? (addBeforeFirst ? 0 : startIndex + 1) : startIndex;
		textRel.setStart(start);
		int end = start + realTokenLength;
		textRel.setEnd(end);
		graph.addRelation(textRel);
		// Change/add order relations
		SToken nextToken = null;
		SOrderRelation oldOrderRelation = null;
		if (!addBeforeFirst) {
			for (SRelation<?, ?> outRel : clickedToken.getOutRelations()) {
				if (outRel instanceof SOrderRelation) {
					Node target = outRel.getTarget();
					if (target instanceof SToken) {
						nextToken = (SToken) target;
						oldOrderRelation = (SOrderRelation) outRel;
					}
				}
			}
			String type = null;
			if (nextToken != null && oldOrderRelation != null) {
				oldOrderRelation.setSource(token);
				if (oldOrderRelation.getType() != null) {
					type = oldOrderRelation.getType();
				}
			}
			SOrderRelation newOrderRelation = SaltFactory.createSOrderRelation();
			newOrderRelation.setSource(clickedToken);
			newOrderRelation.setTarget(token);
			if (type != null) {
				newOrderRelation.setType(type);
			}
			graph.addRelation(newOrderRelation);
		}
		return token;
	}

}
