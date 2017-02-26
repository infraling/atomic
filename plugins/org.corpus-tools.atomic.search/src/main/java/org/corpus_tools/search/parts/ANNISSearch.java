package org.corpus_tools.search.parts;

import java.net.URI;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.search.service.SearchService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import annis.service.objects.Match;
import annis.service.objects.MatchGroup;
import swing2swt.layout.BorderLayout;

public class ANNISSearch {

	@Inject
	private SearchService search;
	
	private Button reindexButton;
	private Table table;
	
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
		
		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new TableColumnLayout());
		
		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		executeQuery.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				MatchGroup result = search.find(queryField.getText());
				table.removeAll();
				
				// find the maximal number of nodes per match and add a column for each
				int maxNumNodes = 0;
				for(Match m : result.getMatches()) {
					maxNumNodes = Math.max(maxNumNodes, m.getSaltIDs().size());
				}
				for(int i=1; i <= maxNumNodes; i++) {
					TableColumn c = new TableColumn(table, SWT.NULL);
					c.setText("" + i);
				}
				
				for(Match m : result.getMatches()) {
					TableItem item = new TableItem(table, SWT.NULL);
					int nodeIdx = 0;
					for(URI u : m.getSaltIDs()) {
						item.setText(nodeIdx, u.getPath() + " " + u.getFragment());
						nodeIdx++;
					}
				}
				
				for(int i=0; i < maxNumNodes; i++) {
					table.getColumn(i).pack();
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
