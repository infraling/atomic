package org.corpus_tools.search.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.corpus_tools.search.service.SearchService;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ANNISSearch {

	@Inject
	private SearchService search;
	
	private Button reindexButton;
	
	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {

		RowLayout layout = new RowLayout();
		parent.setLayout(layout);
		
		reindexButton = new Button(parent, SWT.PUSH);
		reindexButton.setText("Re-index corpus");
		reindexButton.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	


}
