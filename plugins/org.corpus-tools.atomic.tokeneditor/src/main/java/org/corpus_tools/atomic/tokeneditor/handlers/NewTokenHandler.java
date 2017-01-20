/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.handlers;

import java.util.List;

import org.corpus_tools.atomic.api.commands.DocumentGraphAwareHandler;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class NewTokenHandler extends DocumentGraphAwareHandler {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		boolean isLastToken = false;
		SToken selectedToken = null;
		List<SToken> sortedTokens = getGraph().getSortedTokenByText();
		int sortedTokensSize = sortedTokens.size();
		Event trigger = (Event) event.getTrigger();
		Widget widget = trigger.widget;
		Object selectionElement = ((StructuredSelection) HandlerUtil.getCurrentSelectionChecked(event)).getFirstElement();
		if (getGraph() != null) {
			if (widget instanceof NatTable) {
				if (selectionElement instanceof int[]) {
					int[] selection = (int[]) selectionElement;
					int index = selection[0];
					selectedToken = sortedTokens.get(index);
					if (index == sortedTokens.size() - 1) {
						isLastToken = true;
					}
				}
				else if (trigger.data instanceof SToken){ // Command executed from menu 
					selectedToken = (SToken) trigger.data;
					if (sortedTokens.get(sortedTokensSize - 1).equals(selectedToken)) {
						isLastToken = true;
					}
				}
			}
			/*
			 * It's expensive to get the respective token for a StyledText's
			 * caret position with every navigation stepin the text, so instead,
			 * execute the necessary logic here.  
			 */
			else if (widget instanceof StyledText) {
				StyledText text = (StyledText) widget;
				int caretPosition = text.getCaretOffset();
				relations: for (STextualRelation rel : getGraph().getTextualRelations()) {
					if (rel.getEnd() == caretPosition || rel.getEnd() == caretPosition) {
						selectedToken = rel.getSource();
						break relations;
					}
					else if (rel.getEnd() > caretPosition) {
						if (rel.getStart() <= caretPosition) {
							selectedToken = rel.getSource();
							break relations;
						}
					}
				}
				if (sortedTokens.get(sortedTokensSize - 1).equals(selectedToken)) {
					isLastToken = true;
				}
			}
		}
		// FIXME
		if (!isLastToken) {
			System.err.println("HANDLE " + getGraph().getText(selectedToken));
		}
		else {
			System.err.println("HANDLE LAST " + getGraph().getText(selectedToken));
		}
		return null;
	}
}
