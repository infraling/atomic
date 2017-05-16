package org.corpus_tools.atomic.grideditor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.grideditor.data.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.data.AnnotationGridDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridColumnHeaderDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridRowHeaderDataProvider;
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

import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeBasedTable;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.AutomaticSpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
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

public class GridEditor extends DocumentGraphEditor {

	private AnnotationGridDataProvider dataProvider = null;
	private AnnotationGrid annotationTable;
//	private Map<Integer, String> keyMap = new HashMap<>();
	private Map<String, Integer> indexMap = new HashMap<>();
	
	public GridEditor() {
		super();
//		colorManager = new ColorManager();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		annotationTable = compileAnnotationTable(graph);
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
//		final DataLayer bodyDataLayer = new DataLayer(dataProvider);
		AutomaticSpanningDataProvider prov = new AutomaticSpanningDataProvider(dataProvider, false, true);
		SpanningDataLayer bodyDataLayer = new SpanningDataLayer(prov);
		final SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer, false);
//		selectionLayer.addConfiguration(new GridEditorSelectionConfiguration(annotationTable));
		final ISelectionModel selectionModel = selectionLayer.getSelectionModel();
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
		
		// Column header layer stack
		IDataProvider colHeaderDataProvider = new GridColumnHeaderDataProvider(annotationTable);
		DataLayer colHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(colHeaderDataLayer, viewportLayer, selectionLayer);

		// Row header layer stack
		IDataProvider rowHeaderDataProvider = new GridRowHeaderDataProvider(annotationTable);
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
		natTable.configure();
	}
	
	private AnnotationGridDataProvider createDataProvider() {
		return new AnnotationGridDataProvider(annotationTable);
	}
	
	/**
	 * Compiles a table set up like this:
	 * 
	 * | Row index = index of token in list of graph's sorted tokens | Token text | Token annotation key  1 (random order) | Token annotation key 2 | Token annotation key n | Span annotation key 1 (random order) | Span annotation  key 2 | Span annotation key n |
	 * |-------------------------------------------------------------|------------|----------------------------------------|------------------------|------------------------|--------------------------------------|------------------------|-----------------------|
	 * | Integer | String | SAnnotation | SAnnotation | SAnnotation | SAnnotation | SAnnotation | SAnnotation |
	 * 
	 * @param graph
	 * @return
	 */
	private AnnotationGrid compileAnnotationTable(SDocumentGraph graph) {
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
		return grid;
	}

}
