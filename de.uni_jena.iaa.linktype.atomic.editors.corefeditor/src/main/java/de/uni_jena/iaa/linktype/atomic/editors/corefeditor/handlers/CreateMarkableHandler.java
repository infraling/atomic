/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.handlers;

import java.util.regex.Pattern;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.uni_jena.iaa.linktype.atomic.core.registries.Registries;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.CoreferenceEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document.SDocumentModel;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.document.SDocumentProvider;

/**
 * @author Stephan Druskat
 *
 */
public class CreateMarkableHandler extends AbstractHandler {

	protected SSpan span;

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IEditorPart activeEditor = HandlerUtil.getActiveEditor(event);
		CoreferenceEditor editor = null;
		if (activeEditor instanceof CoreferenceEditor) {
			editor = (CoreferenceEditor) activeEditor;
		}
		else {
			return null;
		}
		TextSelection selection = getSelection(event);
		selection = resetSelection(selection, event);
//		changeColorForSelection(editor, selection);
//		createSpanOverSelection(editor, selection);
		return null;
	}

	/**
	 * @param editor
	 * @param selection
	 */
	private void createSpanOverSelection(CoreferenceEditor editor, TextSelection selection) {
		SDocumentModel model = ((SDocumentProvider) editor.getDocumentProvider()).getModel();
		int tokenOffset = selection.getOffset();
		int tokenSelectionEnd = (tokenOffset + selection.getLength());
		EList<SToken> tokenListForSpan = new BasicEList<SToken>();
		EList<STYPE_NAME> textualRelations= new BasicEList<STYPE_NAME>();
		textualRelations.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
		for (SToken token : model.getTokens()) {
			SDataSourceSequence sequence = model.getGraph().getOverlappedDSSequences(token, textualRelations).get(0);
			if (sequence.getSStart() >= tokenOffset && sequence.getSEnd() <= (tokenSelectionEnd + 1)) {
				tokenListForSpan.add(token);
			}
		}
		setSpan(model.getGraph().createSSpan(tokenListForSpan));
	}

	/**
	 * @param editor
	 * @param selection
	 */
	private void changeColorForSelection(CoreferenceEditor editor, TextSelection selection) {
		TextPresentation style = new TextPresentation();
		Color red = Registries.getInstance().getColor("red", "FF0000");
		style.addStyleRange(new StyleRange(selection.getOffset(), selection.getLength(), red, null));
		editor.getViewer().changeTextPresentation(style, true);
	}

	private TextSelection resetSelection(TextSelection selection, ExecutionEvent event) {
		int offset = -1, length = -1;
		offset = selection.getOffset();
		length = selection.getLength();
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		SourceViewer viewer = null;
		if (editor instanceof CoreferenceEditor) {
			viewer = ((CoreferenceEditor) editor).getViewer();
		}
		String text = null;
		IDocument document = ((SourceViewer) viewer).getDocument();
		text = document.get();
		// Find bordering whitespaces
		String textBeforeOffset = text.substring(0, offset);
		int positionWhitespaceBeforeOffset = 0; // Re-usable in case the selection includes the first word, i.e., no whitespace is found
		for (int i = positionWhitespaceBeforeOffset; i < textBeforeOffset.length(); i++) {
			Pattern p = Pattern.compile("\\p{Punct}");
		    if (Character.isWhitespace(textBeforeOffset.charAt(i)) || Pattern.matches("\\p{Punct}", String.valueOf(textBeforeOffset.charAt(i)))) {
		        positionWhitespaceBeforeOffset = i;
		    }
		}
		int positionWhitespaceAfterSelection = offset + length;
		for (int i = positionWhitespaceAfterSelection; i < text.length(); i++) {
		    if ((text.charAt(i) != "'".charAt(0)) && (Character.isWhitespace(text.charAt(i)) || Pattern.matches("\\p{Punct}", String.valueOf(text.charAt(i))))) {
		        positionWhitespaceAfterSelection = i;
		        break;
		    }
		}
		if (positionWhitespaceBeforeOffset != 0) {
			((ITextEditor) editor).selectAndReveal(positionWhitespaceBeforeOffset + 1, (positionWhitespaceAfterSelection - positionWhitespaceBeforeOffset) -1);
		}
		else {
			((ITextEditor) editor).selectAndReveal(0, (positionWhitespaceAfterSelection - positionWhitespaceBeforeOffset));
		}
		return (TextSelection) ((CoreferenceEditor) editor).getSelectionProvider().getSelection();
	}

	private TextSelection getSelection(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		TextSelection textSelection = null;
		if (selection instanceof ITextSelection) {
			textSelection = (TextSelection) selection;
		}
		return textSelection;
	}

	/**
	 * @return the span
	 */
	public SSpan getSpan() {
		return span;
	}

	/**
	 * @param span the span to set
	 */
	public void setSpan(SSpan span) {
		this.span = span;
	}

}
