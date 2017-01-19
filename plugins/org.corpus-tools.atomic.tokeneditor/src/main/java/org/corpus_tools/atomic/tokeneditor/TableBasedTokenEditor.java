/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.List;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.tokeneditor.accessors.TokenRowPropertyAccessor;
import org.corpus_tools.atomic.tokeneditor.configuration.EditorPopupMenuConfiguration;
import org.corpus_tools.atomic.tokeneditor.configuration.TokenEditorKeyConfiguration;
import org.corpus_tools.atomic.tokeneditor.configuration.TokenEditorSelectionConfiguration;
import org.corpus_tools.atomic.tokeneditor.data.TokenListDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenColumnHeaderDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenRowHeaderDataProvider;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.selection.ISelectionModel;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.selection.event.ISelectionEvent;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.contexts.IContextActivation;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TableBasedTokenEditor extends DocumentGraphEditor implements ISelectionProvider {

	private boolean tableFocus = true;
	private boolean offsetSet = false;
	ISpanningDataProvider dataProvider;
	protected int offset = 0;
	protected String newText = "";
	protected StyleRange boldSection;
	protected int highestOffset = 0;
	protected IContextActivation actication;
	private ListenerList<ISelectionChangedListener> selectionListeners = new ListenerList<>();
	private int[] selectedColumns = new int[0];

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.corpus_tools.atomic.editors.DocumentGraphEditor#createEditorPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());

		// build the body layer stack
		dataProvider = createDataProvider();
		final DataLayer bodyDataLayer = new DataLayer(dataProvider);
		final SelectionLayer selectionLayer = new SelectionLayer(bodyDataLayer, false);
		selectionLayer.addConfiguration(new TokenEditorSelectionConfiguration(graph));
		final ISelectionModel selectionModel = selectionLayer.getSelectionModel();
		ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);

		// build the column header layer stack
		IDataProvider colHeaderDataProvider = new TokenColumnHeaderDataProvider(graph);
		DataLayer colHeaderDataLayer = new DataLayer(colHeaderDataProvider);
		ILayer columnHeaderLayer = new ColumnHeaderLayer(colHeaderDataLayer, viewportLayer, selectionLayer);

		// build the row header layer stack
		IDataProvider rowHeaderDataProvider = new TokenRowHeaderDataProvider(dataProvider);
		DataLayer rowHeaderDataLayer = new DataLayer(rowHeaderDataProvider);
		ILayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, viewportLayer, selectionLayer);
		ILayer cornerLayer = new CornerLayer(new DataLayer(new DefaultCornerDataProvider(colHeaderDataProvider, rowHeaderDataProvider)), rowHeaderLayer, columnHeaderLayer);

		GridLayer compositeLayer = new GridLayer(viewportLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);

		final NatTable natTable = new NatTable(parent, SWT.NO_BACKGROUND
	            | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED | SWT.V_SCROLL
	            | SWT.H_SCROLL | SWT.BORDER, compositeLayer, false);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(natTable);

		// StyledText
		final StyledText text = new StyledText(parent, SWT.H_SCROLL| SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(text);
		FontDescriptor largeFontDescriptor = FontDescriptor.createFrom(text.getFont()).setHeight(22);
		Font largeFont = getFont(largeFontDescriptor.createFont(text.getDisplay()).getFontData()[0]);
		text.setFont(largeFont);
		
		String sourceText = graph.getTextualDSs().get(0).getText();
		text.setText(sourceText);

		text.addKeyListener(new KeyAdapter() {
			/**
			 * Arrow key actions must be processed here,
			 * as the events are verified in the text's
			 * verifyListener *before* the events are *done*.
			 */
			@Override
			public void keyPressed(KeyEvent e) {
				/*
				 * CTRL + G selects the column in the table that has
				 * the same index as the token in the graph's list
				 * of ordered tokens by text whose overlapped text
				 * (inclusively) contains the current caret offset. 
				 */
				if ((e.stateMask & SWT.CTRL) != 0 && (e.keyCode == 'g' || e.keyCode == 't')) {
					int index = -1;
					int offset = text.getCaretOffset();
					// Find the STextualRelation for the offset
					relations:
					for (STextualRelation rel : graph.getTextualRelations()) {
						// TODO: Benchmark this vs. single if with <= and >= (3rd if here)
						if (rel.getStart() == offset || rel.getEnd() == offset) {
							index = graph.getSortedTokenByText().indexOf(rel.getSource());
							break relations;
						}
						else if (rel.getEnd() > offset) {
							if (rel.getStart() < offset) {
								index = graph.getSortedTokenByText().indexOf(rel.getSource());
								break relations;
							}
						}
					}
					if (index != -1) {
						int pos = selectionLayer.getColumnPositionByIndex(index);
						selectionLayer.selectColumn(pos, 0, false, false);
					}
					/*
					 * CTRL + T selects the table control.
					 */
					if (e.keyCode == 't') {
						natTable.setFocus();
					}
				}
				
			}
		});
		
		
		/*
		 * When control loses focus and offset and newText
		 * are still set to something other than default values, 
		 * all changes must be nullified! 
		 */
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				tableFocus = false;
			}
			@Override
			public void focusLost(FocusEvent e) {
				tableFocus = true;
			}
		});
		
		text.addExtendedModifyListener(new ExtendedModifyListener() {
			
			@Override
			public void modifyText(ExtendedModifyEvent event) {
//				setDirty(true);
				System.err.println("TEXT HAS CHANGEDD: " + event.start + "-" + (event.start + event.length) + ": " + event.replacedText);
			}
		});
		
		
		// Handle selection - TODO Check if a scroll listener would be faster
		natTable.addLayerListener(new ILayerListener() {

			// Default selection behavior selects cells by default.
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				if (event instanceof ISelectionEvent) {
					setSelection(new StructuredSelection(selectionModel.getSelectedColumnPositions()));
				}
			}
		});

		natTable.addConfiguration(new EditorPopupMenuConfiguration(natTable, graph));
		natTable.addConfiguration(new TokenEditorKeyConfiguration(text));
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.configure();
		
		// Select the first cell on opening the editor
		natTable.addPaintListener(new PaintListener() {
		    @Override
		    public void paintControl(PaintEvent e) {
		        natTable.setFocus();
		        natTable.doCommand(new SelectCellCommand(selectionLayer, 0, 0, false, false));
		        natTable.removePaintListener(this);
		    }
		});
		
		// Set editor as selection provider
		getSite().setSelectionProvider(this);
		
	}
			
	/**
	 * TODO: Description
	 *
	 * @param fd
	 * @return
	 */
	public static Font getFont(FontData fd) {
		fd.setHeight(22);
		FontRegistry r = JFaceResources.getFontRegistry();
		if (!r.hasValueFor(fd.toString())) {
			r.put(fd.toString(), new FontData[] { fd });
		}
		return r.get(fd.toString());
	}

	/**
	 * TODO: Description
	 *
	 * @return
	 */
	private ISpanningDataProvider createDataProvider() {
		final List<SToken> tokens = graph.getSortedTokenByText();
		return new TokenListDataProvider(tokens, new TokenRowPropertyAccessor(graph));
	}

	private void resetText(StyledText text) {
		// FIXME Called too often, deletes text randomly after CTRL + G, etc.
		// Redo logic
		if (!newText.isEmpty() && offsetSet) {
			String oldText = text.getText();
			int newTextLength = newText.length();
			int afterOffset = offset + newTextLength;
			System.err.println("OFFSET " + offset);
			System.err.println("AFTER OFFSET " + afterOffset);
			String resetString = oldText.substring(0, offset) + oldText.substring(afterOffset);
			text.setText(resetString);
			text.setCaretOffset(afterOffset - newTextLength);
		}
	}

	/**
	 * TODO: Description
	 *
	 */
	private void reset() {
		newText = "";
		offsetSet = false;
		offset = 0;
		highestOffset = 0;
	}
	
	private boolean isEdited() {
		return !(offset == 0 && highestOffset == 0);
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
		return new StructuredSelection(selectedColumns);
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
		selectedColumns = (int[]) ((StructuredSelection) selection).getFirstElement();
//		this.selectedTokenIndex = (Integer) ((StructuredSelection) selection).getFirstElement();
		Object[] listeners = selectionListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, selection));
		}
	}

}
