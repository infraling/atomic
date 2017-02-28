package org.corpus_tools.search.parts;

import java.net.URI;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.search.service.SearchService;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Splitter;

import annis.service.objects.Match;
import annis.service.objects.MatchGroup;
import swing2swt.layout.BorderLayout;

public class ANNISSearch {

	@Inject
	private SearchService search;

	private Button btReindex;
	private Table table;

	private Label lblStatus;
	
	@Inject
	private UISynchronize uiSync;

	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {
		parent.setLayout(new BorderLayout(0, 0));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(BorderLayout.WEST);
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		composite.setLayout(rl_composite);

		btReindex = new Button(composite, SWT.PUSH);
		btReindex.setText("Re-index corpus");

		final Text txtQuery = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtQuery.setFont(JFaceResources.getTextFont());
		txtQuery.setLayoutData(new RowData(204, 143));
		txtQuery.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.keyCode == SWT.CR && (e.stateMask & SWT.MOD1) != 0) {
					executeSearch(parent, txtQuery.getText());
					e.doit = false;
				}

			}
		});

		final Button btExecute = new Button(composite, SWT.PUSH);
		btExecute.setText("Execute Query");

		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayoutData(BorderLayout.CENTER);
		composite_1.setLayout(new TableColumnLayout());

		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		lblStatus = new Label(parent, SWT.NONE);
		lblStatus.setAlignment(SWT.RIGHT);
		lblStatus.setLayoutData(BorderLayout.NORTH);
		btExecute.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				executeSearch(parent, txtQuery.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
		btReindex.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				search.reindexAllDocuments(true);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void executeSearch(Composite parent, String aql) {
		
		lblStatus.setText("Searching...");
		table.removeAll();
		
		for(int c=table.getColumnCount()-1; c >= 0; c--) {
			table.getColumn(c).dispose();
		}
		
		Job j = new Job("Searching in corpus") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				MatchGroup result = search.find(aql);
				
				uiSync.asyncExec(() -> {
					// find the maximal number of nodes per match and add a column for each
					int maxNumNodes = 0;
					for (Match m : result.getMatches()) {
						maxNumNodes = Math.max(maxNumNodes, m.getSaltIDs().size());
					}
					
					for (int i = 1; i <= maxNumNodes; i++) {
						TableColumn c = new TableColumn(table, SWT.NULL);
						c.setText("" + i);
					}
					
					int displayMatchCount = 0;
					for (Match m : result.getMatches()) {
						TableItem item = new TableItem(table, SWT.NULL);
						int nodeIdx = 0;
						for (URI u : m.getSaltIDs()) {
							List<String> path = Splitter.on('/').trimResults().splitToList(u.getPath());
							item.setText(nodeIdx, path.get(path.size()-1) + " (" + u.getFragment() + ")");
							nodeIdx++;
						}
						displayMatchCount++;
						if(displayMatchCount > 10000) {
							break;
						}
					}

					for (int i = 0; i < maxNumNodes; i++) {
						table.getColumn(i).pack();
					}
					long matchCount = result.getMatches().size();
					if(matchCount > 10000) {
						lblStatus.setText("Found " + matchCount + " matches. (Only displaying first 10.000 matches)");
					} else {
						lblStatus.setText("Found " + matchCount + " matches.");
					}
				});
				
				return Status.OK_STATUS;
			}
		};
		j.setUser(true);
		j.setPriority(Job.LONG);
		j.schedule();
		
	}

}
