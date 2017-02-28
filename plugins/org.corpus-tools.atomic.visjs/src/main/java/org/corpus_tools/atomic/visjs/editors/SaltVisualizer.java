package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.SALT_TYPE;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.STextualDS;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.util.DataSourceSequence;
import org.corpus_tools.salt.util.ExportFilter;
import org.corpus_tools.salt.util.VisJsVisualizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.google.common.collect.Range;

import swing2swt.layout.BorderLayout;

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

	}

	private void updateView() {
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

			VisJsVisualizer visjs = new VisJsVisualizer(getGraph().getDocument(), new Filter(), null);
			
			visjs.visualize(org.eclipse.emf.common.util.URI.createFileURI(tmpDir.toString()));
			
			browser.setUrl(tmpDir.resolve("saltVisJs.html").toString());
			
		} catch (IOException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void calculateSegments(SDocumentGraph graph) {
		textRangeTable.clearAll();
		
		List<SNode> roots = graph.getRoots();
		if(roots != null) {
			for(SNode r : roots) {
				List<DataSourceSequence> overlappedDS = 
						graph.getOverlappedDataSourceSequence(r, SALT_TYPE.STEXT_OVERLAPPING_RELATION);
				if(overlappedDS != null) {
					for(DataSourceSequence seq : overlappedDS) {
						if(seq.getDataSource() instanceof STextualDS) {
							TableItem item = new TableItem(textRangeTable, SWT.NONE);
							item.setText(seq.getDataSource().getName() + ": " + seq.getStart() + ".." + seq.getEnd());
							Range<Long> textRange = Range.closedOpen(seq.getStart().longValue(), seq.getEnd().longValue());
							item.setData(textRange);
						}
					}
				}
			}
		}
		
		if(textRangeTable.getItemCount() > 0) {
			textRangeTable.setSelection(0);
		}
	}
	
	private class Filter implements ExportFilter {

		@Override
		public boolean includeNode(SNode node) {
			
			if(node instanceof SSpan) {
				return btnIncludeSpans.getSelection();
			}
			
			// default to true
			return true;
		}

		@Override
		public boolean includeRelation(SRelation arg0) {
			
			return true;
		}
		
	}
}
