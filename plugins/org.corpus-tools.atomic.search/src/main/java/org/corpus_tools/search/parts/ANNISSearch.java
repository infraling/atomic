package org.corpus_tools.search.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.search.service.SearchService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

import annis.service.objects.Match;
import annis.service.objects.MatchGroup;
import swing2swt.layout.BorderLayout;

public class ANNISSearch {

	@Inject
	private SearchService search;
	
	private Button reindexButton;
	
	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {
		parent.setLayout(new BorderLayout(0, 0));
		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(BorderLayout.WEST);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		composite.setLayout(rl_composite);
		
		reindexButton = new Button(composite, SWT.PUSH);
		reindexButton.setText("Re-index corpus");
		
		
		final Text queryField = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		queryField.setLayoutData(new RowData(204, 143));
		
		final Button executeQuery = new Button(composite, SWT.PUSH);
		executeQuery.setText("Execute Query");
		
		ListViewer listViewer = new ListViewer(parent, SWT.BORDER | SWT.V_SCROLL);
		List list = listViewer.getList();
		list.setLayoutData(BorderLayout.CENTER);
		executeQuery.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				MatchGroup result = search.find(queryField.getText());
				list.removeAll();
				for(Match m : result.getMatches()) {
					list.add(m.toString());
				}
				MessageDialog.openInformation(parent.getShell(), "Query result", "Found " + result.getMatches().size() + " matches.");
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
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
		
		
	}
	


}
