package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.exceptions.SaltException;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.corpus_tools.salt.util.ExportFilter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.TreeMultimap;

import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.TableColumn;

public class SaltVisualizer extends DocumentGraphEditor {

	private Path tmpDir;
	private Browser browser;
	private Button btnIncludeSpans;
	private Table textRangeTable;

	
	public SaltVisualizer() {
		super();
	}

	@Override
	public void dispose() {

		if (tmpDir != null) {
			try {
				FileUtils.deleteDirectory(tmpDir.toFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		super.dispose();
	}

	@Override
	public void createEditorPartControl(Composite parent) {

		parent.setLayout(new BorderLayout());
		
		browser = new Browser(parent, SWT.NONE);
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(BorderLayout.EAST);
		composite.setLayout(new GridLayout(1, false));

		Label lblFilterByAnnotation = new Label(composite, SWT.NONE);
		lblFilterByAnnotation.setText("Filter by annotation type");

		btnIncludeSpans = new Button(composite, SWT.CHECK);
		btnIncludeSpans.setSelection(true);
		btnIncludeSpans.setText("Include Spans");
		
		Label seperator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		
		textRangeTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		textRangeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textRangeTable.setHeaderVisible(true);
		textRangeTable.setLinesVisible(true);
		
		TableColumn tblclmnFilterBySegment = new TableColumn(textRangeTable, SWT.NONE);
		tblclmnFilterBySegment.setWidth(100);
		tblclmnFilterBySegment.setText("Filter by segment");
		btnIncludeSpans.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateView();
				
			}
		});
		
		calculateSegments(graph);
		updateView();
		
		textRangeTable.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateView();
				
			}
		});
		
		parent.addDisposeListener(new DisposeListener() {
			
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (tmpDir != null) {
					try {
						FileUtils.deleteDirectory(tmpDir.toFile());
					} catch (IOException ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
				
			}
		});

	}

	private void updateView() {
		
		browser.setText("please wait while visualization is loading...");
		
		Job j = new Job("Create Salt visualization") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				if (tmpDir != null) {
					try {
						FileUtils.deleteDirectory(tmpDir.toFile());
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				try {
					tmpDir = Files.createTempDirectory("atomic-visjs-visualizer-");

					final CustomVisJsVisualizer visjs = new CustomVisJsVisualizer(getGraph().getDocument(), new Filter(), null);
					
					Display.getDefault().syncExec(() -> {
						try {
							visjs.visualize(org.eclipse.emf.common.util.URI.createFileURI(tmpDir.toString()));
						} catch (SaltException | IOException | XMLStreamException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						browser.setUrl(tmpDir.resolve("saltVisJs.html").toString());
					});
					
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				return Status.OK_STATUS;
			}
		};
		j.setSystem(true);
		j.schedule();
		
		

		
	}
	
	private static class STextualDSComparator implements Comparator<STextualDS> {

		@Override
		public int compare(STextualDS o1, STextualDS o2) {
			return ComparisonChain.start().compare(o1.getName(), o2.getName()).result();
		}
	}
	
	private static class RangeComparator<C extends Comparable> implements Comparator<Range<C>> {

		@Override
		public int compare(Range<C> o1, Range<C> o2) {
			
			return ComparisonChain.start()
					.compare(o1.lowerEndpoint(), o2.lowerEndpoint())
					.compare(o1.upperBoundType(), o2.upperBoundType())
					.result();
		}
	}
	
	private void calculateSegments(SDocumentGraph graph) {
		textRangeTable.clearAll();
		
		Multimap<STextualDS, Range<Long>> sortedDS = TreeMultimap.create(new STextualDSComparator(), new RangeComparator<>());
		
		
		List<SNode> roots = graph.getRoots();
		if(roots != null) {
			for(SNode r : roots) {
				List<DataSourceSequence> overlappedDS = 
						graph.getOverlappedDataSourceSequence(r, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
				if(overlappedDS != null) {
					for(DataSourceSequence seq : overlappedDS) {
						if(seq.getDataSource() instanceof STextualDS) {
							sortedDS.put((STextualDS) seq.getDataSource(), 
									Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue()));

						}
					}
				}
			}
		}
		
		for(Map.Entry<STextualDS, Range<Long>> e : sortedDS.entries()) {
			TableItem item = new TableItem(textRangeTable, SWT.NONE);
			item.setText(e.getKey().getName() + ": " + e.getValue().lowerEndpoint() + ".." + e.getValue().upperEndpoint());
			item.setData("range", e.getValue());
			item.setData("text", e.getKey());
		}
		
		
		if(textRangeTable.getItemCount() > 0) {
			textRangeTable.setSelection(0);
		}
	}
	
	private class Filter implements ExportFilter {

		@Override
		public boolean includeNode(SNode node) {
			
			boolean include = false;
			
			
			// check if the node covers a currently selected range
			@SuppressWarnings("rawtypes")
			List<DataSourceSequence> seqList = getGraph().getOverlappedDataSourceSequence(node, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
			if(seqList != null) {
				outerLoop:
				for(DataSourceSequence seq : seqList ) {
					
					Range<Long> nodeRange = Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue());
					
					for(TableItem item : textRangeTable.getSelection()) {
						
						Range<Long> itemRange = (Range<Long>) item.getData("range");
						STextualDS itemText = (STextualDS) item.getData("text");
						
						if(itemText == seq.getDataSource()
								&& nodeRange.isConnected(itemRange)) {
							include = true;
							break outerLoop;
						}
					
					}
				
				}
			}
			
			if(node instanceof SSpan) {
				include = include && btnIncludeSpans.getSelection();
			}
			
			return include;
		}

		@Override
		public boolean includeRelation(SRelation arg0) {
			
			return true;
		}
		
	}
}
