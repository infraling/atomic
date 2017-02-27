package org.corpus_tools.atomic.visjs.editors;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

import javax.swing.plaf.FileChooserUI;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.util.VisJsVisualizer;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;

public class SaltVisualizer extends  DocumentGraphEditor {

	private Path tmpDir;

	public SaltVisualizer() {
		super();
	}
	
	
	
	@Override
	public void dispose() {
		
		if(tmpDir != null) {
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
		Browser browser = new Browser(parent, PROP_TITLE);
		
		if(tmpDir != null) {
			try {
				FileUtils.deleteDirectory(tmpDir.toFile());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			
			
			tmpDir = Files.createTempDirectory("atomic-visjs-visualizer-");
			
			new VisJsVisualizer(getGraph().getDocument()).visualize(org.eclipse.emf.common.util.URI.createFileURI(tmpDir.toString()));
			
			browser.setUrl(tmpDir.resolve("saltVisJs.html").toString());

		} catch (IOException | XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
