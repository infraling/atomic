package org.corpus_tools.search.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.search.service.SearchService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ANNISSearch {

	@Inject
	private SearchService search;
	
	private Button reindexButton;
	
	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {

		RowLayout layout = new RowLayout(SWT.VERTICAL);
		parent.setLayout(layout);
		
		reindexButton = new Button(parent, SWT.PUSH);
		reindexButton.setText("Re-index corpus");
		reindexButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				search.reindexAllDocuments();
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		final Text queryField = new Text(parent, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL);
		RowData queryFieldRowData = new RowData();
		queryFieldRowData.width=200;
		queryFieldRowData.height=200;
		queryField.setLayoutData(queryFieldRowData);
		
		final Button executeQuery = new Button(parent, SWT.PUSH);
		executeQuery.setText("Execute Query");
		executeQuery.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				long result = search.count(queryField.getText());
				MessageDialog.openInformation(parent.getShell(), "Query result", "Found " + result + " matches.");
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
	}
	


}
