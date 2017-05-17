package org.corpus_tools.atomic.grideditor;

import java.util.List;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.grideditor.config.GridEditConfiguration;
import org.corpus_tools.atomic.grideditor.data.AnnotationGridDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridColumnHeaderDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridRowHeaderDataProvider;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.selection.GridEditorSelectionConfiguration;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.selection.ISelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditor extends DocumentGraphEditor {

	private AnnotationGridDataProvider dataProvider = null;
	private AnnotationGrid annotationGrid;
	
	public GridEditor() {
		super();
//		colorManager = new ColorManager();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		annotationGrid = compileAnnotationGrid(graph);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		/* ############################################
		 * Grid
		 * ############################################
		 */
		dataProvider = createDataProvider();
		AutomaticSpanningDataProvider spanningProvider = new AutomaticSpanningDataProvider(dataProvider, false, true);
		SpanningDataLayer bodyDataLayer = new SpanningDataLayer(spanningProvider);
		final SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer, false);
		selectionLayer.addConfiguration(new GridEditorSelectionConfiguration(annotationGrid));
		final ISelectionModel selectionModel = selectionLayer.getSelectionModel();
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		
		// Column header layer stack
		IDataProvider colHeaderDataProvider = new GridColumnHeaderDataProvider(annotationGrid);
		DataLayer colHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(colHeaderDataLayer, viewportLayer, selectionLayer);

		// Row header layer stack
		IDataProvider rowHeaderDataProvider = new GridRowHeaderDataProvider(annotationGrid);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);
//
		// Corner layer
		ILayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer, columnHeaderLayer);

		GridLayer compositeLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		final NatTable natTable = new NatTable(parent, SWT.NO_BACKGROUND
	            | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL
	            | SWT.H_SCROLL | SWT.BORDER, compositeLayer, false);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

//		natTable.addConfiguration(new EditorPopupMenuConfiguration(natTable, graph));
//		natTable.addConfiguration(new TokenEditorKeyConfiguration(text));
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new GridEditConfiguration());
		natTable.configure();
	}
	
	private AnnotationGridDataProvider createDataProvider() {
		return new AnnotationGridDataProvider(annotationGrid);
	}
	
	/**
	 * Compiles and lays out an instance of {@link AnnotationGrid}
	 * for the current {@link SDocumentGraph}.
	 * 
	 * Row indices are thereby got from the index of a specific
	 * token in the list of tokens ordered by text returned from
	 * {@link SDocumentGraph#getSortedTokenByText()}. Cell values
	 * are, apart from the first column which is the token's text
	 * returned by {@link SDocumentGraph#getText(SNode)}, of type
	 * {@link SAnnotation}, and for each token, the token's annotations
	 * are added to the grid first, then the annotations for each
	 * span that is governing the token, etc.
	 * 
	 * @param graph
	 * @return
	 */
	private AnnotationGrid compileAnnotationGrid(SDocumentGraph graph) {
		// FIXME TODO: In order to only display values, I probably need a custom implementation of SAnnotation, whose 
		// toString returns another custom String wrapper, so that the annotation objects are the same, but the
		// grid views them as differently
		AnnotationGrid grid = new AnnotationGrid();
		final List<SToken> orderedTokens = graph.getSortedTokenByText();
		for (int rowIndex = 0; rowIndex < orderedTokens.size(); rowIndex++) {
			int colIndex = 0;
		
			SToken t = orderedTokens.get(rowIndex);
			grid.record(rowIndex, 0, "Text", graph.getText(t));
			for (SAnnotation a : t.getAnnotations()) {
				grid.record(rowIndex, ++colIndex, a.getQName(), a);
			}
			List<SRelation<SNode, SNode>> rels = graph.getInRelations(t.getId());
			for (SRelation<SNode, SNode> r : rels) {
				SNode src = null;
				if ((src = r.getSource()) instanceof SSpan) {
					for (SAnnotation a : src.getAnnotations()) {
						grid.record(rowIndex, ++colIndex, a.getQName(), a);
					}
				}
			}
		}
		grid.layout();
		return grid;
	}

}
