/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.layer.cell.TranslatedLayerCell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CreateSpanHandler extends AbstractHandler {

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		AnnotationGrid grid = (AnnotationGrid) ((Event) event.getTrigger()).data;
		Set<Integer> tokenIndices = new HashSet<>();
		ISelection currentSelection = HandlerUtil.getCurrentSelectionChecked(event);
		String annotationName = null;
		int colIndex = -1;
		if (currentSelection instanceof StructuredSelection) {
			Object firstElement = ((StructuredSelection) currentSelection).getFirstElement(); 
			if (firstElement instanceof HashSet<?>) {
				for (Object e : (HashSet<?>) firstElement) {
					if (e instanceof TranslatedLayerCell) {
						tokenIndices.add(((TranslatedLayerCell) e).getRowIndex());
						if (annotationName == null && colIndex == -1) {
							colIndex = ((TranslatedLayerCell) e).getColumnIndex();
							annotationName = grid.getHeaderMap().get(colIndex);
						}
						else {
							if (!annotationName.equals(grid.getHeaderMap().get(colIndex))) {
								MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Can not create span over more than one column.\nSelect cells from one column only");
								return null;
							}
						}
					}
				}
			}
		}
		List<SToken> sortedTokens = grid.getGraph().getSortedTokenByText();
		List<SToken> spanTokens = new ArrayList<>();
		tokenIndices.stream().forEach(i -> {
			spanTokens.add(sortedTokens.get(i));
		});
		SSpan span = grid.getGraph().createSpan(spanTokens);
		SAnnotation annotation = span.createAnnotation(null, annotationName, null);
		int col = colIndex;
		String colName = annotationName;
		tokenIndices.stream().forEach(i -> {
			grid.record(i, col, colName, annotation);
		});
		return null;
	}
	
}
