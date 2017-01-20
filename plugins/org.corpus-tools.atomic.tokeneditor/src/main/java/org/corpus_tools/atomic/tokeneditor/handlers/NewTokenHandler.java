/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.handlers;

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
		SToken selectedToken = null;
		Event trigger = (Event) event.getTrigger();
		Widget widget = trigger.widget;
		Object selectionElement = ((StructuredSelection) HandlerUtil.getCurrentSelectionChecked(event)).getFirstElement();
		if (getGraph() != null) {
			if (widget instanceof NatTable) {
				if (selectionElement instanceof int[]) {
					int[] selection = (int[]) selectionElement;
					selectedToken = getGraph().getSortedTokenByText().get(selection[0]);
				}
				else if (trigger.data instanceof SToken){ // Command executed from menu 
					selectedToken = (SToken) trigger.data;
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
			}
		}
		// FIXME
		System.err.println("HANDLE " + getGraph().getText(selectedToken));
		return null;
	}
}
