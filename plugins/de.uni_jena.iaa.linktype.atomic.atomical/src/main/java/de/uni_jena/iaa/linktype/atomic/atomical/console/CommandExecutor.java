package de.uni_jena.iaa.linktype.atomic.atomical.console;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.antlr.v4.runtime.Token;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.api.editors.SaltGraphUpdatable;
import org.corpus_tools.atomic.console.ConsoleCommandBaseListener;
import org.corpus_tools.atomic.console.ConsoleCommandParser;
import org.corpus_tools.atomic.console.ConsoleCommandParser.AnnotateCommandContext;
import org.corpus_tools.atomic.console.ConsoleCommandParser.HelpCommandContext;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;

class CommandExecutor extends ConsoleCommandBaseListener {

	/**
	 * 
	 */
	private final AtomicalConsole atomicalConsole;

	/**
	 * @param atomicalConsole
	 */
	CommandExecutor(AtomicalConsole atomicalConsole) {
		this.atomicalConsole = atomicalConsole;
	}

	private DocumentGraphEditor editor;
	private SDocumentGraph graph;
	
	private void out(String msg) {
		try {
			this.atomicalConsole.out.write(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean checkValidEditor() {
		if (editor != null && graph != null) {					
			return true;
		} else {
			out("No active editor. Command will be ignored.\n");
		}

		return false;
	}
	
	private void updateEditor() {
		if(graph != null && editor instanceof SaltGraphUpdatable) {
			((SaltGraphUpdatable) editor).updateSDocumentGraph(graph);
		}
	}
	
	@Override
	public void enterCommanChain(ConsoleCommandParser.CommanChainContext ctx) {
		editor = atomicalConsole.getEditor();
		graph = atomicalConsole.getGraph();

	}

	@Override
	public void enterHelpCommand(HelpCommandContext ctx) {
		atomicalConsole.displayHelp();
	}

	@Override
	public void enterClearCommand(ConsoleCommandParser.ClearCommandContext ctx) {
		atomicalConsole.clearConsole();
	}

	@Override
	public void enterAnnotateCommand(AnnotateCommandContext ctx) {
		if(checkValidEditor()) {
			List<SNode> elementsToAnnotate = new LinkedList<>();
			
			for(Token element : ctx.elements) {
				List<SNode> nodeList = graph.getNodesByName(element.getText());
				if(nodeList != null) {
					elementsToAnnotate.addAll(nodeList);
				}
			}
			
			// get the annotation information
			String ns = ctx.qname().ns == null ? null : ctx.qname().ns.getText();
			String name = ctx.qname().name.getText();

			if(ns == null && name != null) {
				// find first matching namespace of existing annotation
				ns = "atomic";
				eachElement:
				for(SNode n : elementsToAnnotate) {
					for(SAnnotation anno : n.getAnnotations()) {
						if(name.equals(anno.getName())) {
							ns = anno.getNamespace();
							break eachElement;
						}
					}
				}
			}
			String value = ctx.value == null ? null : ctx.value.getText();
			
			
			// add the annotation to each node in the list
			for(SNode n : elementsToAnnotate) {

				SAnnotation existing = n.getAnnotation(ns, name);
				if(existing == null && value != null) {
					n.createAnnotation(ns, name, value);
				} else {
					if(value == null) {
						// delete the annotation
						n.removeLabel(ns, name);
					} else {
						// change annotation value
						existing.setValue(value);
					}
				}
			}
			updateEditor();
		}
	}


}