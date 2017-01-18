/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor;

import java.util.List;

import org.corpus_tools.atomic.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.tokeneditor.accessors.TokenRowPropertyAccessor;
import org.corpus_tools.atomic.tokeneditor.configuration.EditorPopupMenuConfiguration;
import org.corpus_tools.atomic.tokeneditor.configuration.TokenEditorKeyConfiguration;
import org.corpus_tools.atomic.tokeneditor.configuration.TokenEditorSelectionConfiguration;
import org.corpus_tools.atomic.tokeneditor.data.TokenListDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenColumnHeaderDataProvider;
import org.corpus_tools.atomic.tokeneditor.providers.TokenRowHeaderDataProvider;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
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
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
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
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TableBasedTokenEditor extends DocumentGraphEditor  {

	public static final String DATA_GRAPH = "graph";
	public static final String DATA_DATALAYER = "dataLayer";
	public static final String DATA_SELECTIONLAYER = "selectionLayer";
	private boolean tableFocus = true;
	private boolean offsetSet = false;
	ISpanningDataProvider dataProvider;
	protected int offset = 0;
	protected String newText = "";
	protected StyleRange boldSection;
	protected int highestOffset = 0;

	/**
	 * 
	 */
	public TableBasedTokenEditor() {
	}

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

		// Set graph for retrieval via NatTable#getData() method later on.
		natTable.setData(DATA_GRAPH, graph);
		// Set dataLayer for retrieval via NatTable#getData() method later on.
		natTable.setData(DATA_DATALAYER, bodyDataLayer);
		// Set dataLayer for retrieval via NatTable#getData() method later on.
		natTable.setData(DATA_SELECTIONLAYER, selectionLayer);

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
				if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == 'g') {
					int index = -1;
					int offset = text.getCaretOffset();
					// Find the STextualRelation for the offset
					relations:
					for (STextualRelation rel : graph.getTextualRelations()) {
						// TODO: Benchmark this vs. single if with <= and >= (3rd if here)
						if (rel.getEnd() == offset) {
							index = graph.getSortedTokenByText().indexOf(rel.getSource());
							break relations;
						}
						else if (rel.getStart() == offset) {
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
				}
				
				/*
				 * CTRL + T selects the table control.
				 */
				if ((e.stateMask & SWT.CTRL) != 0 && e.keyCode == 't') {
					natTable.setFocus();
				}
			}
		});
		
//		/*
//		 * Handle key events in the text widget
//		 */
//		text.addVerifyKeyListener(new VerifyKeyListener() {
//			@Override
//			public void verifyKey(VerifyEvent event) {
//				// Go to table cell
//				if ((event.stateMask & SWT.CTRL) != 0 && event.keyCode == 'g') {
//					System.err.println(">" + text.getSelectionText() + "<");
//					for (STextualRelation stextualres : graph.getTextualRelations()) {
//						if (stextualres.getStart() < text.getCaretOffset()) {
//							if (stextualres.getEnd() > text.getCaretOffset()) {
//								SToken token = stextualres.getSource();
//								int index = graph.getSortedTokenByText().indexOf(token);
//								selectionLayer.selectColumn(index, 1, false, false);
//							}
//						}
//					}
//				}
//				
//				switch (event.keyCode) {
//
//				/* 
//				 * Anything that potentially moves the cursor
//				 * should reset everything! 	
//				 */
//				case SWT.ESC:
//				case SWT.END:
//				case SWT.HOME:
//				case SWT.ARROW_DOWN:
//				case SWT.ARROW_UP:
//				case SWT.PAGE_DOWN:
//				case SWT.PAGE_UP:
//					
//					// Delete previously entered text!
//					if (isEdited()) {
//						resetText(text);
//						reset();
//					}
//					break;
//				
//				/*
//				 * Handle arrow keys when editing,
//				 * i.e., don't process as the event
//				 * isn't processed yet and the offsets
//				 * are wrong!
//				 */
//				case SWT.ARROW_RIGHT:
//				case SWT.ARROW_LEFT:
//					break;
//
//				/* 
//				 * Backspace, Delete: Handle as usual.
//				 */
//				case SWT.BS:
//				// Delete	
//				case SWT.DEL:
//					break;
//				/* 
//				 * Return: Write and reset!
//				 */
//				case SWT.CR:
//					event.doit = false;
//					if (offset != 0 && highestOffset != 0) {
//						System.err.println("Writing text:" + text.getText().substring(offset, highestOffset + 1));
//					}
//					reset();
//					break;
//					
//				default:
//					int currentOfsset = text.getCaretOffset();
//					if (!offsetSet) {
//						offset = currentOfsset;
//					}
//					else {
//						if (currentOfsset > highestOffset) {
//							highestOffset  = currentOfsset;
//						}
//					}
//					offsetSet = true;
//					newText += event.character;
//					break;
//				}
//			}
//		});
		
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
		
		
		
//		// Handle selection
//		natTable.addLayerListener(new ILayerListener() {
//
//			// Default selection behavior selects cells by default.
//			@Override
//			public void handleLayerEvent(ILayerEvent event) {
//				if (event instanceof CellSelectionEvent) {
////					System.err.println("CELL EVENT");
//					CellSelectionEvent cellEvent = (CellSelectionEvent) event;
//					setSelectedToken(cellEvent.getColumnPosition());
//				}
//				else if (event instanceof ColumnSelectionEvent) {
////					System.err.println("COL EVENT");
//					ColumnSelectionEvent colEvent = (ColumnSelectionEvent) event;
//					List<Range> rangesList = new ArrayList<>(colEvent.getColumnPositionRanges());
//					Range range = rangesList.get(0);
//					Set<Integer> members = range.getMembers();
//					if (members.size() == 1) {
//						setSelectedToken(new ArrayList<>(members).get(0));
//					}
//				}
//			}
//
//			private void setSelectedToken(int colPos) {
//				// transform the NatTable column position to the row position
//				// of the body layer stack
//				int absoluteColPos = LayerUtil.convertColumnPosition(natTable, colPos, bodyDataLayer);
//				SToken token = graph.getSortedTokenByText().get(absoluteColPos);
//				// FIXME: HERE IS SELECTION IN TEXT VIEWER
//				if (tableFocus) {
//					List<DataSourceSequence> seq = graph.getOverlappedDataSourceSequence(token, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
//					// Tokens should only overlap one single sequence
//					int textIndex = seq.get(0).getStart().intValue();
//					text.setCaretOffset(textIndex - 20);
//					text.setSelection(textIndex);
//				}
//				natTable.setData("selectedToken", graph.getSortedTokenByText().get(absoluteColPos));
//			}
//		});
		natTable.addConfiguration(new EditorPopupMenuConfiguration(natTable));
		natTable.addConfiguration(new TokenEditorKeyConfiguration(text));
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.configure();
		
		selectionLayer.selectCell(0, 0, false, false);
		natTable.forceFocus();
		natTable.doCommand(new SelectCellCommand(selectionLayer, 0, 0, false, false));
		System.err.println(natTable.isFocusControl());
	}
		
//		natTable.addFocusListener(new FocusAdapter() {
//			@Override
//			public void focusGained(FocusEvent e) {
//				tableFocus = true;
//			}
//			@Override
//			public void focusLost(FocusEvent e) {
//				tableFocus = false;
//			}
//		});}
	
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

}
