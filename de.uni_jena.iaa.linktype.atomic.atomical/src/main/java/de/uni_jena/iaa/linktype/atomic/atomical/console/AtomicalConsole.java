/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.GraphEditor;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicalConsole extends IOConsole implements Runnable {

	private IOConsoleOutputStream out;
	private SDocumentGraph graph;

	public AtomicalConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		addEditorListener();
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		out = newOutputStream();
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				out.setColor(new Color(null, 234, 123, 195));
			}
		});
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));
		for (;;) {
			try {
				String input = br.readLine();
				if (input == null) {
					break;
				}
				else {
					processInput(input);
				}
			} catch (IOException e) {
				// Assume that the console has been closed
				break;
			}
		}
		
	}

	private void processInput(String input) {
		// TODO Auto-generated method stub
		
	}
	
	private void addEditorListener() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
			
			AtomicalConsole console = AtomicalConsole.this;
			
			@Override
			public void partOpened(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				if (part instanceof GraphEditor) {
					IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					EditPartViewer editPartViewer = ((GraphEditor) editor).getEditPartViewer();
					EditPart contentEditPart = editPartViewer.getContents();
					console.setGraph((SDocumentGraph) contentEditPart.getModel());
					try {
						console.out.write("Working on " + editor.getEditorInput().getName() + ".\n");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
		});
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		return graph;
	}

	/**
	 * @param graph the graph to set
	 */
	public void setGraph(SDocumentGraph graph) {
		this.graph = graph;
	}

}
