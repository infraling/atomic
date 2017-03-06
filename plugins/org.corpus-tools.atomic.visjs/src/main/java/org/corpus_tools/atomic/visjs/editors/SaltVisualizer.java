package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.api.editors.SaltGraphUpdatable;
import org.corpus_tools.atomic.api.editors.SaltNodeSelectable;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.EditorPart;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import com.google.common.collect.TreeMultimap;

public class SaltVisualizer extends DocumentGraphEditor implements SaltNodeSelectable, SaltGraphUpdatable {

	private static final Logger log = LogManager.getLogger(SaltVisualizer.class);
	
	private Path tmpDir;
	private Browser browser;
	private Button btnIncludeSpans;
	private Table textRangeTable;
	private Text txtSegmentFilter;
	private Button btnIncludePointingRelations;

	public SaltVisualizer() {
		super();
	}

	@Override
	public void dispose() {

		if (tmpDir != null) {
			try {
				FileUtils.deleteDirectory(tmpDir.toFile());
			} catch (IOException ex) {
				log.error("Could not delete temporary directory {}", tmpDir.toString(), ex);
			}
		}

		super.dispose();
	}

	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout(2, false));

		browser = new Browser(parent, SWT.NONE);
		browser.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, true, 1, 1));
		composite.setLayout(new GridLayout(1, false));

		Label lblFilterByAnnotation = new Label(composite, SWT.NONE);
		lblFilterByAnnotation.setText("Filter by annotation type");

		btnIncludeSpans = new Button(composite, SWT.CHECK);
		btnIncludeSpans.setSelection(true);
		btnIncludeSpans.setText("Include Spans");
		btnIncludeSpans.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {

			}
		});

		btnIncludePointingRelations = new Button(composite, SWT.CHECK);
		btnIncludePointingRelations.setSelection(true);
		btnIncludePointingRelations.setText("Include pointing relations");
		btnIncludePointingRelations.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView(true);
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		txtSegmentFilter = new Text(composite, SWT.BORDER);
		txtSegmentFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		txtSegmentFilter.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				updateView(true);
			}
		});

		Label seperator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		seperator.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));

		textRangeTable = new Table(composite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		textRangeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		textRangeTable.setHeaderVisible(true);
		textRangeTable.setLinesVisible(true);

		TableColumn tblclmnFilterBySegment = new TableColumn(textRangeTable, SWT.NONE);
		tblclmnFilterBySegment.setWidth(100);
		tblclmnFilterBySegment.setText("Filter by segment");

		textRangeTable.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView(false);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				updateView(false);

			}
		});

		updateView(true);

		parent.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (tmpDir != null) {
					try {
						FileUtils.deleteDirectory(tmpDir.toFile());
					} catch (IOException ex) {
						log.error("Could not delete temporary directory {}", tmpDir.toString(), ex);
					}
				}

			}
		});

	}
	
	private Set<Integer> getSelectedSegmentIdxForNodes(List<String> nodeIDs) {
		// sort the available segments by their size (smallest first)
		List<Integer> segmentIdx = new LinkedList<>();
		for(int i=0; i < textRangeTable.getItemCount(); i++) {
			segmentIdx.add(i);
		}
		segmentIdx.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				TableItem item1 = textRangeTable.getItem(o1);
				TableItem item2 = textRangeTable.getItem(o2);
				
				Range<Long> range1 = (Range<Long>) item1.getData("range");
				Range<Long> range2 = (Range<Long>) item2.getData("range");
				
				return ComparisonChain.start()
						.compare(range1.upperEndpoint() - range1.lowerEndpoint(), range2.upperEndpoint() - range2.lowerEndpoint())
						.result();
			}
		});
		
		Set<Integer> selectedIdx = new HashSet<>();
		
		// find all segments that include the nodes
		for(String id : nodeIDs) {
			SNode n = getGraph().getNode(id);
			if(n != null) {
				
				List<DataSourceSequence> overlappedDS = graph.getOverlappedDataSourceSequence(n,
						SALT_TYPE.STEXT_OVERLAPPING_RELATION);
				if (overlappedDS != null) {
					for (DataSourceSequence seq : overlappedDS) {
						if (seq.getDataSource() instanceof STextualDS) {
							Range<Long> selectedNodeRange = Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue());
							
							for(int idx : segmentIdx) {
								Range<Long> rangeSegment = (Range<Long>) textRangeTable.getItem(idx).getData("range");
								if(rangeSegment.encloses(selectedNodeRange)) {
									selectedIdx.add(idx);
									break;
								}
							}

						}
					}
				}
			}
		}
		
		return selectedIdx;
	}
	
	@Override
	public void setSelection(List<String> nodeIDs) {
		
		Set<Integer> selectedIdx = getSelectedSegmentIdxForNodes(nodeIDs); 
		
		if(selectedIdx.isEmpty()) {
			// select all segments since we could not find out which are overlapping
			textRangeTable.selectAll();
		} else {
			// unselect all old segments and only select the overlapping ones
			textRangeTable.deselectAll();
			for(int idx : selectedIdx) {
				textRangeTable.select(idx);
			}
		}
		updateView(false);
		
		
	}
	
	@Override
	public void updateSDocumentGraph(SDocumentGraph newGraph) {
		this.graph = newGraph;
		this.dirty = true;
		firePropertyChange(EditorPart.PROP_DIRTY);
		updateView(true);
		
	}
	
	private List<Integer> getSegmentIdxSortedByLength() {
		List<Integer> result = IntStream.range(0, textRangeTable.getItemCount()).boxed().collect(Collectors.toList());
		
		result.sort(new Comparator<Integer>() {
			@Override
			public int compare(Integer o1, Integer o2) {
				
				// get both ranges from the container
				Range<Long> r1 = (Range<Long>) textRangeTable.getItem(o1).getData("range");
				Range<Long> r2 = (Range<Long>) textRangeTable.getItem(o2).getData("range");
				
				return ComparisonChain.start()
						.compare(r1.upperEndpoint() - r1.lowerEndpoint(), r2.upperEndpoint() - r2.lowerEndpoint())
						.result();
			}
		});
		
		return result;
	}

	private void updateView(boolean recalculateSegments) {

		if(recalculateSegments) {
			// store the old segment selection
			List<Range<Long>> oldSelectedRanges = new LinkedList<>();
			for(TableItem item : textRangeTable.getSelection()) {
				oldSelectedRanges.add((Range<Long>) item.getData("range"));
			}
			
			calculateSegments(getGraph());
			
			textRangeTable.deselectAll();
			
			// sort the segments by their length
			List<Integer> sortedIdx = getSegmentIdxSortedByLength();
			
			// for each old segment select the first (and thus smallest) segment in the new list
			boolean selectedSomeOld = false;
			for(Range<Long> oldRange : oldSelectedRanges) {
				for(int idx : sortedIdx) {
					Range<Long> itemRange = (Range<Long>) textRangeTable.getItem(idx).getData("range");
					if(itemRange.encloses(oldRange)) {
						textRangeTable.select(idx);
						selectedSomeOld = true;
						// only select the first one
						break;
					}
				}
			}
			if(!selectedSomeOld && textRangeTable.getItemCount() > 0) {
				textRangeTable.setSelection(0);
			}
		}
		
		browser.setText("please wait while visualization is loading...");

		Job j = new Job("Create Salt visualization") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				if (tmpDir != null) {
					try {
						FileUtils.deleteDirectory(tmpDir.toFile());
					} catch (IOException ex) {
						log.error("Could not delete temporary directory {}", tmpDir.toString(), ex);
					}
				}

				try {
					tmpDir = Files.createTempDirectory("atomic-visjs-visualizer-");

					final CustomVisJsVisualizer visjs = new CustomVisJsVisualizer(getGraph().getDocument(),
							new Filter(), null);

					Display.getDefault().syncExec(() -> {
						try {
							visjs.visualize(org.eclipse.emf.common.util.URI.createFileURI(tmpDir.toString()));
						} catch (SaltException | IOException | XMLStreamException ex) {
							log.error("Something went wrong when creating the HTML for the Salt visualization", ex);
						}
						browser.setUrl(tmpDir.resolve("saltVisJs.html").toString());
					});

				} catch (IOException ex) {
					log.error("Something went wrong when creating the HTML for the Salt visualization", ex);
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

	private static class RangeStartComparator<C extends Comparable> implements Comparator<Range<C>> {

		@Override
		public int compare(Range<C> o1, Range<C> o2) {

			return ComparisonChain.start().compare(o1.lowerEndpoint(), o2.lowerEndpoint())
					.compare(o1.upperBoundType(), o2.upperBoundType()).result();
		}
	}

	private void calculateSegments(SDocumentGraph graph) {
		textRangeTable.removeAll();

		ExportFilter currentFilter = new RootFilter();

		Multimap<STextualDS, Range<Long>> sortedDS = TreeMultimap.create(new STextualDSComparator(),
				new RangeStartComparator<>());

		List<SNode> roots = graph.getRoots();
		if (roots != null) {
			for (SNode r : roots) {

				if (currentFilter.includeNode(r)) {
					List<DataSourceSequence> overlappedDS = graph.getOverlappedDataSourceSequence(r,
							SALT_TYPE.STEXT_OVERLAPPING_RELATION);
					if (overlappedDS != null) {
						for (DataSourceSequence seq : overlappedDS) {
							if (seq.getDataSource() instanceof STextualDS) {
								sortedDS.put((STextualDS) seq.getDataSource(),
										Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue()));

							}
						}
					}
				}
			}
		}

		for (Map.Entry<STextualDS, Range<Long>> e : sortedDS.entries()) {
			TableItem item = new TableItem(textRangeTable, SWT.NONE);
			item.setText(
					e.getKey().getName() + ": " + e.getValue().lowerEndpoint() + ".." + e.getValue().upperEndpoint());
			item.setData("range", e.getValue());
			item.setData("text", e.getKey());
		}
	}

	private class RootFilter implements ExportFilter {

		@Override
		public boolean includeNode(SNode node) {

			boolean include = false;

			if (txtSegmentFilter.getText().isEmpty() || (node instanceof SToken)) {
				include = true;
			} else {
				if (node.getAnnotations() != null) {
					for (SAnnotation anno : node.getAnnotations()) {
						if (anno.getName().contains(txtSegmentFilter.getText())) {
							include = true;
							break;
						}
					}
				}
			}

			if (node instanceof SSpan) {
				include = include && btnIncludeSpans.getSelection();
			}

			return include;
		}

		@Override
		public boolean includeRelation(SRelation arg0) {

			return true;
		}

	}

	private class Filter implements ExportFilter {

		@Override
		public boolean includeNode(SNode node) {

			boolean include = false;

			// check if the node covers a currently selected range
			@SuppressWarnings("rawtypes")
			List<DataSourceSequence> seqList = getGraph().getOverlappedDataSourceSequence(node,
					SALT_TYPE.STEXT_OVERLAPPING_RELATION);
			if (seqList != null) {
				outerLoop: for (DataSourceSequence seq : seqList) {

					Range<Long> nodeRange = Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue());

					for (TableItem item : textRangeTable.getSelection()) {

						Range<Long> itemRange = (Range<Long>) item.getData("range");
						STextualDS itemText = (STextualDS) item.getData("text");

						if (itemText == seq.getDataSource() && nodeRange.isConnected(itemRange)) {
							include = true;
							break outerLoop;
						}

					}

				}
			}

			if (node instanceof SSpan) {
				include = include && btnIncludeSpans.getSelection();
			}
			
			// additionally check for valid annotation
			if (include && !txtSegmentFilter.getText().isEmpty() && !(node instanceof SToken)) {
				if (node.getAnnotations() != null) {
					boolean annoFound = false;
					for (SAnnotation anno : node.getAnnotations()) {
						if (anno.getName().contains(txtSegmentFilter.getText())) {
							annoFound = true;
							break;
						}
					}
					include = annoFound;
				}
			}

			return include;
		}

		@Override
		public boolean includeRelation(SRelation rel) {
			boolean include = true;
			if(rel instanceof SPointingRelation) {
				include = btnIncludePointingRelations.getSelection();
			}
			return include;
		}

	}
}
