package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.corpus_tools.salt.util.ExportFilter;
import org.corpus_tools.salt.util.VisJsVisualizer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import swing2swt.layout.BorderLayout;

public class SaltVisualizer extends DocumentGraphEditor {

	private Path tmpDir;
	private Browser browser;
	private Button btnIncludeSpans;

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
		btnIncludeSpans.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateView();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});

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
