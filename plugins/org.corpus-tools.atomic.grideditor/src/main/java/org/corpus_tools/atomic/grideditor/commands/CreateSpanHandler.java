/**
 * 
 */
package org.corpus_tools.atomic.grideditor.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.grideditor.GridEditor;
import org.corpus_tools.atomic.grideditor.configuration.GridSpanningDataProvider;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.graph.LabelableElement;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.cell.TranslatedLayerCell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * A handler for the menu item "Create new span" which is available
 * on selection of 2 or more visually empty cells in the NatTable.
 * 
 * Note that {@link #execute(ExecutionEvent)} handles two cases of which
 * one may not be obvious from the onset.
 * 
 * 1. A span is being created from selected cells whose `dataValue`
 * is `null`, which is the obvious case. These cells `dataValue` is
 * set to an {@link SAnnotation} on a newly created {@link SSpan}.
 * The {@link SSpan} spans those tokens in the {@link SDocumentGraph}'s
 * list of sorted token by text which correspond to the row indices of the
 * selected cells. The `namespace` and `name` of the {@link SAnnotation}
 * correponds to the value set in the {@link AnnotationGrid}'s header
 * for the column the selected cells belong to. The `value` of the
 * {@link SAnnotation} is set to `null`. A {@link GridSpanningDataProvider}
 * takes care of the visual spanning of the cells.
 * 2. The less obvious case includes selected cells whose `dataValue`
 * is in fact *not* `null`, but an {@link SAnnotation} as described
 * in 1., i.e., one with a `value` of `null`. While these cells *appear*
 * empty, they are not. Still, this class handles this event as well by
 * *deleting* all {@link SSpan}s containing any "`null` {@link SAnnotation}s"
 * in the selected cells and creating a new {@link SSpan} over the
 * respective range of {@link SToken}s, again annotation them with a
 * "`null` {@link SAnnotation}" as described above.  
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class CreateSpanHandler extends AbstractHandler {
	
	private static final Logger log = LogManager.getLogger(CreateSpanHandler.class);

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		AnnotationGrid grid = (AnnotationGrid) ((Event) event.getTrigger()).data;
//		Set<Integer> tokenIndices = new HashSet<>();
//		ISelection currentSelection = HandlerUtil.getCurrentSelectionChecked(event);
//		String annotationName = null;
//		String annotationNamespace = null;
//		int colIndex = -1;
//		if (currentSelection instanceof StructuredSelection) {
//			Object firstElement = ((StructuredSelection) currentSelection).getFirstElement(); 
//			if (firstElement instanceof HashSet<?>) {
//				for (Object e : (HashSet<?>) firstElement) {
//					if (e instanceof TranslatedLayerCell) {
//						
//						tokenIndices.add(((TranslatedLayerCell) e).getRowIndex());
//						if (annotationName == null && colIndex == -1) {
//							colIndex = ((TranslatedLayerCell) e).getColumnIndex();
//							String[] headerSplit = grid.getColumnHeaderMap().get(colIndex).split("::");
//							if (headerSplit.length == 2) {
//								annotationNamespace = headerSplit[0].equals("null") ? null : headerSplit[0];
//								annotationName = headerSplit[1];
//							}
//							else if (headerSplit.length == 1) {
//								annotationName = headerSplit[0];
//							}
//						}
//						else {
//							String[] headerSplit = grid.getColumnHeaderMap().get(colIndex).split("::");
//							String headerName = null;
//							String headerNamespace = null;
//							if (headerSplit.length == 2) {
//								headerNamespace = headerSplit[0].equals("null") ? null : headerSplit[0];
//								headerName = headerSplit[1];
//							}
//							else if (headerSplit.length == 1) {
//								headerName = headerSplit[0];
//							}
//							if (!annotationName.equals(headerName) && !annotationNamespace.equals(headerNamespace)) {
//								MessageDialog.openError(Display.getDefault().getActiveShell(), "Error", "Can not create span over more than one column.\nSelect cells from one column only");
//								return null;
//							}
//						}
//					}
//				}
//			}
//		}
//		List<SToken> sortedTokens = grid.getGraph().getSortedTokenByText();
//		List<SToken> spanTokens = new ArrayList<>();
//		tokenIndices.stream().forEach(i -> {
//			spanTokens.add(sortedTokens.get(i));
//		});
//		SSpan span = grid.getGraph().createSpan(spanTokens);
//		SAnnotation annotation = span.createAnnotation(annotationNamespace, annotationName, null);
//		int col = colIndex;
//		String colName = (annotationNamespace == null ? "null" : annotationNamespace.toString()).concat("::").concat(annotationName);
//		tokenIndices.stream().forEach(i -> {
//			grid.record(i, col, colName, annotation);
//		});
//		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
//		return null;
		NatTable table = (NatTable) ((Event) event.getTrigger()).widget;
		Collection<ILayerCell> selectedCells = (Collection<ILayerCell>) ((Object[]) ((Event) event.getTrigger()).data)[0];
		AnnotationGrid grid = (AnnotationGrid) ((Object[]) ((Event) event.getTrigger()).data)[1];
		// FIXME Use try catch for ClassCastExceptions for above code
		SDocumentGraph graph = grid.getGraph();
		final List<SToken> sortedTokens = graph.getSortedTokenByText();
		ArrayList<ILayerCell> sortedSelectedCells = new ArrayList<ILayerCell>(selectedCells);
		sortedSelectedCells.sort(Comparator.comparing(ILayerCell::getRowIndex));
		String header = grid.getColumnHeaderMap().get(sortedSelectedCells.get(0).getColumnIndex());
		int columnIndex = sortedSelectedCells.get(0).getColumnIndex();
		/*
		 * Collect covered tokens, 
		 * then remove all spans and create a new span with an empty annotation.
		 */
		Set<SSpan> spansToDelete = new HashSet<>();
		List<Integer> tokenIndices = new ArrayList<>();
		for (ILayerCell cell : sortedSelectedCells) {
			Object dataValue = cell.getDataValue();
			if (dataValue == null) {
				tokenIndices.add(cell.getRowIndex());
			}
			else if (dataValue instanceof SSpan) {
				SSpan span = (SSpan) dataValue;
				List<SToken> spanTokens = graph.getOverlappedTokens(span);
				for (SToken token : spanTokens) {
					tokenIndices.add(sortedTokens.indexOf(token));
				}
				spansToDelete.add(span);
			}
			else if (dataValue instanceof SAnnotation) {
				SAnnotation annotation = (SAnnotation) dataValue;
				LabelableElement container = annotation.getContainer();
				// TODO In future versions which support token annotations, catch STokens as well
				if (container instanceof SSpan) {
					SSpan span = (SSpan) container;
					List<SToken> spanTokens = graph.getOverlappedTokens(span);
					for (SToken token : spanTokens) {
						tokenIndices.add(sortedTokens.indexOf(token));
					}
					spansToDelete.add(span);
				}
				else {
					log.warn("Container of annotation {} in cell in row {} is not an instance of {}, but of {}: {}.", annotation.toString(), cell.getRowIndex(), SSpan.class.getSimpleName(), container.getClass().getSimpleName());
				}
			}
			else {
				log.warn("Encountered an invalid cell value. Cell values should be an instance of {}, {} or null. Instead, I've found an object of type {}: {}.", SSpan.class.getSimpleName(), SAnnotation.class.getSimpleName(), dataValue.getClass().getSimpleName(), dataValue.toString());
			}
		}
		String[] headerSplit = header.split("::");
		String annotationNamespace = headerSplit.length == 2 ? headerSplit[0] : null;
		String annotationName = headerSplit.length == 2 ? headerSplit[1] : headerSplit[0];
		final SAnnotation finalValue = SaltFactory.createSAnnotation();
		finalValue.setNamespace(annotationNamespace);
		finalValue.setName(annotationName);
		List<SToken> tokenList = new ArrayList<>();
		tokenIndices.stream().forEach(i -> {
			tokenList.add(sortedTokens.get(i));
			grid.record(i, columnIndex, header, finalValue);
		});
		for (SSpan span : spansToDelete) {
			grid.getGraph().removeNode(span);
		}
		SSpan newSpan = graph.createSpan(tokenList);
		newSpan.addAnnotation(finalValue);
		table.refresh();
		((GridEditor) HandlerUtil.getActiveEditor(event)).setDirty(true);
		return null;
	}
	
}
