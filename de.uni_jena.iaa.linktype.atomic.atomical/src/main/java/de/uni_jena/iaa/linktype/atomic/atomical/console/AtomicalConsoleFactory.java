/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import java.io.IOException;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.GraphEditor;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;

/**
 * @author Stephan Druskat
 * Creates and opens an instance of AtomicalConsole in the ConsoleView
 * 
 */
public class AtomicalConsoleFactory implements IConsoleFactory {

	private static AtomicalConsole _console;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
	 */
	@Override
	public void openConsole() {
		AtomicalConsole console = getConsole();
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			IConsole[] existing = manager.getConsoles();
			boolean exists = false;
			// Check if instances of this console already exist
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i]) {
					exists = true;
				}
			}
			if (!exists) {
				// Activate input
				IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
				if (editor instanceof GraphEditor) {
					EditPartViewer editPartViewer = ((GraphEditor) editor).getEditPartViewer();
					console.setGraphPart((GraphPart) editPartViewer.getContents());
					if (console.getGraphPart().getModel() == console.getGraph()) {
						// Do nothing
					}
					else {
						console.setGraph(console.getGraphPart().getModel());
						console.setEditor(editor);
						String excerpt = null; 
						String text = console.getGraph().getSTextualDSs().get(0).getSText();
						if (text.length() >= 50) {
							excerpt = console.getGraph().getSTextualDSs().get(0).getSText().substring(0, 49) + "...";
						}
						else {
							excerpt = text;
						}
						try {
							console.getOut().write("Working on "
									+ editor.getEditorInput().getName()
									+ " (\""
									+ excerpt + "\").\n");
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				manager.addConsoles(new IConsole[] { console });
			}
			manager.showConsoleView(console);
			console.activate();
		}
	}

	private static AtomicalConsole getConsole() {
		if (_console == null) {
			_console = new AtomicalConsole("AtomicAL Console", null);
		}
		return _console;
	}

}
