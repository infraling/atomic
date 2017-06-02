package org.corpus_tools.atomic.grideditor;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.grideditor.configuration.GridEditConfiguration;
import org.corpus_tools.atomic.grideditor.configuration.GridEditorSelectionConfiguration;
import org.corpus_tools.atomic.grideditor.configuration.GridSpanningDataProvider;
import org.corpus_tools.atomic.grideditor.selection.MultiCellSelection;
import org.corpus_tools.atomic.grideditor.selection.SingleCellSelection;
import org.corpus_tools.atomic.grideditor.data.AnnotationGridDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridColumnHeaderDataProvider;
import org.corpus_tools.atomic.grideditor.data.GridRowHeaderDataProvider;
import org.corpus_tools.atomic.grideditor.data.annotationgrid.AnnotationGrid;
import org.corpus_tools.atomic.grideditor.menu.GridPopupMenuConfiguration;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.SpanningDataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.event.ISelectionEvent;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;

/**
 * @author Stephan Druskat
 * 
 * // TODO Add description
 *
 */
public class GridEditor extends DocumentGraphEditor implements ISelectionProvider {

	private static final Logger log = LogManager.getLogger(GridEditor.class);
	private AnnotationGridDataProvider dataProvider = null;
	private AnnotationGrid annotationGrid;
	
	private ListenerList<ISelectionChangedListener> selectionListeners = new ListenerList<>();
	
	
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
		getSite().setSelectionProvider(null);
	}
	
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		/* ############################################
		 * Grid
		 * ############################################
		 */
		dataProvider = createDataProvider();
		GridSpanningDataProvider spanningProvider = new GridSpanningDataProvider(dataProvider, false, true);
		SpanningDataLayer bodyDataLayer = new SpanningDataLayer(spanningProvider);
		final SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer, false);
		selectionLayer.addConfiguration(new GridEditorSelectionConfiguration(annotationGrid));
//		final ISelectionModel selectionModel = selectionLayer.getSelectionModel();
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

		
		natTable.addConfiguration(new GridPopupMenuConfiguration(natTable, annotationGrid, selectionLayer));
//		natTable.addConfiguration(new TokenEditorKeyConfiguration(text));
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new GridEditConfiguration());
		natTable.configure();
		
		/* #################################
		 * Selection
		 * #################################
		 */
		selectionLayer.addLayerListener(new ILayerListener() {
			
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof ISelectionEvent) {
					Collection<ILayerCell> selectedCells = ((ISelectionEvent) event).getSelectionLayer().getSelectedCells();
					if (selectedCells.size() == 1) {
						setSelection(new SingleCellSelection(selectedCells.iterator().next()));
					}
					else {
						setSelection(new MultiCellSelection(new ArrayList<ILayerCell>(selectedCells)));
					}
					setSelection(new StructuredSelection(((ISelectionEvent) event).getSelectionLayer().getSelectedCells()));
				}
			}
		});

		getSite().setSelectionProvider(this);
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
		AnnotationGridCompilation compilationRunnable = new AnnotationGridCompilation(graph.getSortedTokenByText());
		try {
			new ProgressMonitorDialog(Display.getDefault().getActiveShell()).run(true, true, compilationRunnable);
		}
		catch (InvocationTargetException | InterruptedException e) {
			GridEditor.log.error("An error occurred during the compilation of the annotation grid.", e);
		}
		return compilationRunnable.getGrid();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		return new StructuredSelection();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		selectionListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

	public class AnnotationGridCompilation implements IRunnableWithProgress {
	
		private final AnnotationGrid grid;
		private final List<SToken> orderedTokens;
	
		public AnnotationGridCompilation(List<SToken> sortedTokenByText) {
			this.orderedTokens = sortedTokenByText;
			this.grid = new AnnotationGrid(graph);
		}
	
		public AnnotationGrid getGrid() {
			return grid;
		}
	
		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			
			monitor.beginTask("Compiling annotation grid", this.orderedTokens.size() + 1);
			monitor.subTask("Compiling rows per token");
	
			for (int rowIndex = 0; rowIndex < orderedTokens.size(); rowIndex++) {
				int colIndex = 0;
	
				SToken t = orderedTokens.get(rowIndex);
				grid.record(rowIndex, 0, "Token", graph.getText(t));
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
				monitor.worked(1);
			}
			monitor.subTask("Laying out grid");
			grid.layout();
			monitor.worked(1);
			monitor.done();
			if (monitor.isCanceled()) {
				throw new InterruptedException("Annotation grid compilation has been cancelled.");
			}
		}
	}

}
