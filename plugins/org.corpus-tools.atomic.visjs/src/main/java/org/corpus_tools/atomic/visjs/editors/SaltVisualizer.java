package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.xml.stream.XMLStreamException;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.util.VisJsVisualizer;
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

		try {
			Path tmpDir = Files.createTempDirectory("atomic-visjs-visualizer");
			
			new VisJsVisualizer(getGraph().getDocument()).visualize(org.eclipse.emf.common.util.URI.createFileURI(tmpDir.toString()));
			
			browser.setUrl(tmpDir.resolve("saltVisJs.html").toString());

		} catch (IOException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
