/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.console.ConsoleCommandLexer;
import org.corpus_tools.atomic.console.ConsoleCommandParser;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.uni_jena.iaa.linktype.atomic.atomical.utils.AtomicalConsoleUtils;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicalConsole extends IOConsole implements Runnable {

	private IOConsoleOutputStream out;
	private IOConsoleOutputStream err;
	
	public AtomicalConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);

		out = newOutputStream();
		err = newOutputStream();
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			out.write("To display a list of available commands, type \"help\".\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				out.setColor(AtomicalConsoleUtils.getColor(AtomicalConsoleUtils.OUT_GREEN));
				err.setColor(AtomicalConsoleUtils.getColor(AtomicalConsoleUtils.ERR_RED));
			}
		});
		BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));
		for (;;) {
			try {
				String input = br.readLine();
				if (input == null) {
					break;
				} else {
					processInput(input + "\n");
				}
			} catch (IOException e) {
				// Assume that the console has been closed
				break;
			}
		}

	}

	private void processInput(String input) throws IOException {

		ConsoleCommandLexer lexer = new ConsoleCommandLexer(new ANTLRInputStream(input));
		ConsoleCommandParser parser = new ConsoleCommandParser(new CommonTokenStream(lexer));

		parser.addErrorListener(new BaseErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line,
					int charPositionInLine, String msg, RecognitionException e) {
				Display.getDefault().syncExec(() -> {
					try {
						err.write(msg + "\n");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				});
			}
		});
		
		ConsoleCommandParser.StartContext parsedTree = parser.start();

		// execute the rest of the command execution inside the main thread
		Display.getDefault().syncExec(() -> {
			if(parser.getNumberOfSyntaxErrors() > 0) {
				try {
					err.write("Could not parse command. Enter \"help\" to get a list of all valid commands.\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				ParseTreeWalker walker = new ParseTreeWalker();
				walker.walk(new CommandExecutor(this), parsedTree);
			}

		});

	}
	
	void displayHelp() {
		try {
			out.write("Command                           Arguments                               Syntax example\n"
					+ "n (New structure node)" + /***/
					"            [key]:[value]                           n pos:np\n" + "s (New span node)" + /***/
					"                 [element] [element] [key]:[val]         s t1 t2 type:np\n"
					+ "e (New edge)                      -[type] [source] [target] [key]:[value] e -d n1 n2 r:coref\n"
					+ "a (Annotate)" + /***/
					"                      [element] [key]:[val] / [key]:          a n1 pos:np\n"
					+ "d (Delete element)                [element] [element]                     d t1 n2\n"
					+ "help (Displays this command overview)\n" + "clear (Clears the console)\n"
			/*
			 * +
			 * "c (New common child)*             [element] [element] [key]:[val] c t1 t2 pos:np\n"
			 * +
			 * "t (Append new token)              [string]                        t Foobar\n"
			 * +
			 * "l (Switch annotation level)*      [level]                         l -s\n"
			 * +
			 * "j (Jump to sentence)              [0-9]*                          j 1234\n"
			 * +
			 * "x (Set corpus excerpt to display) [[0-9]*]|[[0-9]*]               x 2|1\n"
			 * + "\n" + "*Level switches can be used with this command."
			 */);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the graph
	 */
	public SDocumentGraph getGraph() {
		DocumentGraphEditor editor = getEditor();
		return editor == null ? null : editor.getGraph();
	}

	/**
	 * @return the editor
	 */
	public DocumentGraphEditor getEditor() {

		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IEditorPart editor = window.getActivePage().getActiveEditor();
			if (editor instanceof DocumentGraphEditor) {
				return (DocumentGraphEditor) editor;
			}
		}
		return null;
	}
	
	/**
	 * @return the out
	 */
	public IOConsoleOutputStream getOut() {
		return out;
	}

}
