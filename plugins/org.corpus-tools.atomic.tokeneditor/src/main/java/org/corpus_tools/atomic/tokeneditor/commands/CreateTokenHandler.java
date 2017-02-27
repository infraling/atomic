/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.commands;

import org.corpus_tools.atomic.api.commands.DocumentGraphAwareHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class CreateTokenHandler extends DocumentGraphAwareHandler {

	/**
	 * 
	 */
	private static final String PARAM_ID__IS_LAST_TOKEN = "org.corpus_tools.atomic.tokeneditor.commands.createToken.isLastToken";
	/**
	 * 
	 */
	private static final String PARAM_ID__IS_NULL_TOKEN = "org.corpus_tools.atomic.tokeneditor.commands.createToken.isNullToken";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String nullTokenParam = event.getParameter(PARAM_ID__IS_NULL_TOKEN);
		System.err.println(nullTokenParam);
		String lastTokenParam = event.getParameter(PARAM_ID__IS_LAST_TOKEN);
		try {
			Boolean isNullToken = (Boolean) event.getCommand().getParameterType(PARAM_ID__IS_NULL_TOKEN).getValueConverter().convertToObject(nullTokenParam);
			Boolean isLastToken = (Boolean) event.getCommand().getParameterType(PARAM_ID__IS_LAST_TOKEN).getValueConverter().convertToObject(lastTokenParam);
			System.out.println("IS NULL TOKEN " + isNullToken);
			System.out.println("IS LAST TOKEN " + isLastToken);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
//		SToken selectedToken = null;
//		List<SToken> sortedTokens = getGraph().getSortedTokenByText();
//		int sortedTokensSize = sortedTokens.size();
//		Event trigger = (Event) event.getTrigger();
//		Widget widget = trigger.widget;
//		Object selectionElement = ((StructuredSelection) HandlerUtil.getCurrentSelectionChecked(event)).getFirstElement();
//		if (getGraph() != null) {
//			if (widget instanceof NatTable) {
//				if (selectionElement instanceof int[]) {
//					int[] selection = (int[]) selectionElement;
//					int index = selection[0];
//					selectedToken = sortedTokens.get(index);
//					if (index == sortedTokens.size() - 1) {
//						isLastToken = true;
//					}
//				}
//				else if (trigger.data instanceof SToken){ // Command executed from menu 
//					selectedToken = (SToken) trigger.data;
//					if (sortedTokens.get(sortedTokensSize - 1).equals(selectedToken)) {
//						isLastToken = true;
//					}
//				}
//			}
//			/*
//			 * It's expensive to get the respective token for a StyledText's
//			 * caret position with every navigation step in the text, so instead,
//			 * execute the necessary logic here.  
//			 */
//			else if (widget instanceof StyledText) {
//				StyledText text = (StyledText) widget;
//				int caretPosition = text.getCaretOffset();
//				relations: for (STextualRelation rel : getGraph().getTextualRelations()) {
//					if (rel.getEnd() == caretPosition || rel.getEnd() == caretPosition) {
//						selectedToken = rel.getSource();
//						break relations;
//					}
//					else if (rel.getEnd() > caretPosition) {
//						if (rel.getStart() <= caretPosition) {
//							selectedToken = rel.getSource();
//							break relations;
//						}
//					}
//				}
//				if (sortedTokens.get(sortedTokensSize - 1).equals(selectedToken)) {
//					isLastToken = true;
//				}
//			}
//		}
//		// FIXME
//		if (!isLastToken) {
//			System.err.println("HANDLE " + getGraph().getText(selectedToken));
//		}
//		else {
//			System.err.println("HANDLE LAST " + getGraph().getText(selectedToken));
//		}
		return null;
	}
}
