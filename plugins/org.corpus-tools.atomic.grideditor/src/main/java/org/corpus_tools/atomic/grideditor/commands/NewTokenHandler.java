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
		@SuppressWarnings("rawtypes")
		List<SRelation> clickedTokenOutRelations = clickedToken.getOutRelations();
		STextualRelation clickedTokenTextRel = null;
		relLoop:
		for (@SuppressWarnings("rawtypes") SRelation rel : clickedTokenOutRelations) {
			if (rel instanceof STextualRelation) {
				clickedTokenTextRel = (STextualRelation) rel;
				break relLoop;
			}
		}
		// Change STextualDS
		STextualDS ds = clickedTokenTextRel.getTarget(); 
		int clickedTokenEndIndex = clickedTokenTextRel.getEnd();
		String originalText = ds.getText();
		// Is the clicked token followed by a whitespace?
		boolean clickedTokenFollowedByWhitespace = originalText.substring(clickedTokenEndIndex).startsWith(" ");
		// Is the new token to be the new last token?
		boolean createNewLastToken = originalText.length() == clickedTokenEndIndex;
		/*
		 * FIXME: Move to JavaDoc for method Unless the new token is to be the
		 * new first token, where the start index will always be 0, the
		 * following applies. If the clicked token is followed by a whitespace,
		 * this whitespace is preserved in the source text, but will not be
		 * covered by the new token, i.e., the start index of the new token is
		 * moved to the end index of the clicked token + 1.
		 */
		int newTokenStartIndex = 0;
		if (!addBeforeFirst) {
			newTokenStartIndex = clickedTokenEndIndex + (clickedTokenFollowedByWhitespace ? 1 : 0);
			/*
			 * FIXME Move to JavaDoc If the new token should be surrounded by
			 * whitespaces (as per dialog setting), and the clicked token is
			 * already followed by a whitespace, the start index is left as is,
			 * otherwise, it is incremented by 1, and the new token text
			 * prefixed with a whitespace.
			 */
			if (addWhitespace && !clickedTokenFollowedByWhitespace) {
				newTokenStartIndex = newTokenStartIndex + 1;
				newTokenText = " " + newTokenText;
			}
		}
		/*
		 * FIXME Move to JavaDoc If the new token will be the new last token,
		 * i.e., will cover the end of the data source text, no whitespace will
		 * be appended, independent of the `addWhiteSpace` value. Else, the new
		 * token text will be appended with a whitespace if it should be
		 * surrounded with whitespaces.
		 */
		if (!createNewLastToken && addWhitespace) {
			newTokenText = newTokenText + " ";
		}
		// Calculate the end index of the new token
		int newTokenTextLength = newTokenText.length();
		// Avoid StringIndexOutOfBoundsException for newTokenStartIndex on ds text
		if (createNewLastToken && addWhitespace) {
			newTokenStartIndex = newTokenStartIndex - 1;
		}
		String newText = new StringBuilder(originalText).insert(newTokenStartIndex, newTokenText).toString();
		ds.setText(newText);
		// Change indices for following tokens
		List<SToken> sortedTokens = graph.getSortedTokenByText();
		List<SToken> tokenChangeList = addBeforeFirst ? sortedTokens : sortedTokens.subList(clickedCell.getRowIndex() + 1, sortedTokens.size());
		tokenChangeList.stream().forEach(t -> {
			t.getOutRelations().forEach(r -> {
				if (r instanceof STextualRelation) {
					Integer oldStart = ((STextualRelation) r).getStart();
					Integer oldEnd = ((STextualRelation) r).getEnd();
					((STextualRelation) r).setStart(oldStart + newTokenTextLength);
					((STextualRelation) r).setEnd(oldEnd + newTokenTextLength);
				}
			});
		});
		// Create new token
		SToken newToken = createToken(newTokenStartIndex, newTokenTextLength, ds, graph, addBeforeFirst, clickedToken);
		// Update grid
		int key = addBeforeFirst ? 0 : clickIndex + 1;
		Map<Integer, Row> tempRowMap = new HashMap<>();
		for (Iterator<Entry<Integer, Row>> iterator = grid.getRowMap().entrySet().iterator(); iterator.hasNext();) {
			Entry<Integer, Row> entry = iterator.next();
			if (entry.getKey() >= key) {
				tempRowMap.put(entry.getKey() + 1, entry.getValue());
				iterator.remove();
			}
		}
		grid.getRowMap().remove(key);
		grid.record(key, 0, "Token", newToken);
		tempRowMap.entrySet().stream().forEach(e -> grid.getRowMap().put(e.getKey(), e.getValue()));
		
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}

	private SToken createToken(int startIndex, int realTokenLength, STextualDS ds, SDocumentGraph graph, boolean addBeforeFirst, SToken clickedToken) {
		SToken token = SaltFactory.createSToken();
		graph.addNode(token);
		STextualRelation textRel = SaltFactory.createSTextualRelation();
		textRel.setSource(token);
		textRel.setTarget(ds);
		textRel.setStart(startIndex);
		int end = startIndex + realTokenLength;
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
