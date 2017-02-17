package org.corpus_tools.atomic.visjs.editors;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class SaltVisualizer extends  DocumentGraphEditor {


	public SaltVisualizer() {
		super();
	}
	
	
	
	@Override
	public void dispose() {
		super.dispose();
	}



	@Override
	public void createEditorPartControl(Composite parent) {
		Browser browser = new Browser(parent, PROP_TITLE);
		browser.setUrl("http://corpus-tools.org/atomic/");
		
	}

}
