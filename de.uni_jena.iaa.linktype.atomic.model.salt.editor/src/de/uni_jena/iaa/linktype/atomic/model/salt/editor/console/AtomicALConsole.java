/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Edge;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.exceptions.GraphInsertException;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.AtomicSaltEditor;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SAnnotationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SDominanceRelationCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SDominanceRelationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SPointingRelationCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SPointingRelationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SStructureCreateCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.commands.SStructureDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.editparts.SDocumentGraphEditPart;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.layout.SimpleLayoutAlgorithm;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.parser.AtomicALParser;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicALConsole extends IOConsole implements Runnable {
	
private IWorkbenchPage page;
private boolean isDisposed;
private SDocumentGraph graph;
private String layerSwitch = "s"; // Default layer, when no layer is set during the command
private IEditorPart editor;
private EditPart topLevelEditPart;
private IOConsoleOutputStream out;

	//	private IOConsoleInputStream inputStream;
//	private IOConsoleOutputStream outputStream;
//
	public AtomicALConsole(String name, String consoleType, ImageDescriptor imageDescriptor, String encoding, boolean autoLifecycle, IWorkbenchPage page) {
		super(name, consoleType, imageDescriptor, encoding, autoLifecycle);
		this.page = page;
		Thread t = new Thread(this);
		t.start();
	}
	
	public static final String CONSOLE_NAME = "AtomicAL Console";

//	public static class ConsoleIOAgent extends IOAgent {
//
//		final PrintWriter out;
//		final PrintWriter err;
//
//		public ConsoleIOAgent(IOConsoleOutputStream out,
//				IOConsoleOutputStream err) {
//			this.out = new PrintWriter(out);
//			this.err = new PrintWriter(err);
//		}
//
//		@Override
//		public Writer getWriter(int fd) {
//			if (fd == CONST_STDERR) {
//				return err;
//			} else if (fd == CONST_STDOUT) {
//				return out;
//			}
//			return super.getWriter(fd);
//		}
//
//	}
	
	@Override
	protected void dispose() {
		super.dispose();
		this.isDisposed = true;
	}
	
	public boolean isDisposed() {
		return isDisposed;
	}

	public AtomicALConsole() {
		super(CONSOLE_NAME, ImageDescriptor.getMissingImageDescriptor());
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public String getName() {
		return "AtomicAL Console";
	}

	@Override
	public void run() {
		try {
			final IOConsoleOutputStream err = newOutputStream();
			out = newOutputStream();
			final IOConsoleOutputStream prompt = newOutputStream();

			Display.getDefault().syncExec(new Runnable() {
				
				@Override
				public void run() {
					err.setColor(new Color(null, 255, 0, 0));
					out.setColor(new Color(null, 234, 123, 195));
					prompt.setColor(new Color(null, 95, 200, 23));
				}
			});
			
//			ConsoleIOAgent ioAgent = new ConsoleIOAgent(out, err);
//			SpoofaxInterpreter intp = new SpoofaxInterpreter(ioAgent);
			BufferedReader br = new BufferedReader(new InputStreamReader(getInputStream()));
			for (;;) {
				try {
					String input = br.readLine();
					if (input == null)
						break;
					else { // FIXME: Implement parsing  logic and creating commands based on text
						String atomicALCommand = AtomicALParser.parseCommand(input);
						String rawParameters = AtomicALParser.parseRawParameters(input);
						HashMap<Object, Object> atomicALParameters = AtomicALParser.parseParameters(rawParameters);
						editor = page.getActiveEditor();
						if (editor instanceof AtomicSaltEditor) {
							EditPartViewer editPartViewer = ((AtomicSaltEditor) editor).getEditPartViewer();
							topLevelEditPart = editPartViewer.getContents();
							graph = (SDocumentGraph) topLevelEditPart.getModel();
							final CommandStack commandStack = ((AtomicSaltEditor) editor).getDomain().getCommandStack();
							executeCommand(commandStack, atomicALCommand, atomicALParameters);
						}
					}
				} catch (IOException e) {
					// Assume that the console has been closed
					break;
				}
			}
		} catch (Exception e) {
//			InterpreterPlugin.logError("Fatal error in interpreter thread", e);
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void executeCommand(final CommandStack commandStack, String atomicALCommand, HashMap<Object, Object> atomicALParameters) {
		// FIXME TODO Refactor for readability/re-usability + check for uncaught exceptions
		if (atomicALCommand.equalsIgnoreCase("--help")) {
			displayHelp();
			return;
		}
		char commandChar = 0;
		try {
			commandChar = atomicALCommand.charAt(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
		switch (commandChar) {
			case 'n': // Create SStructure with or without annotations
				final SStructureCreateCommand createSStructureCommand = new SStructureCreateCommand();
				createSStructureCommand.setGraph(graph);
				createSStructureCommand.setLocation(new Point(100, 100)); // FIXME: Or at other position
				SStructure sStructure = SaltFactory.eINSTANCE.createSStructure();
				// FIXME: Do the following properly
					if (atomicALParameters.get("attributes") != null) {
						LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
						for (Object key : attributes.keySet()) {
							SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
							String val = (String) attributes.get(key);
				            anno.setSName((String) key);
				            anno.setSValue(val);
				            sStructure.addSAnnotation(anno);
						}
					}
		    	createSStructureCommand.setSStructure(sStructure);
		    	Display.getDefault().asyncExec(new Runnable() {
		    		public void run() {
		    	    	commandStack.execute(createSStructureCommand);
		    		}
		    	});
		    	break;
			
			case 'e': // Create a new edge
				// Check for layer switch
				if (((ArrayList<String>) atomicALParameters.get("layer_switch")).size() > 0)
					setLayerSwitch(((ArrayList<String>) atomicALParameters.get("layer_switch")).get(0));
				final Command command;
				SRelation relation;
				if (getLayerSwitch().equals("f")) {
					command = new SPointingRelationCreateCommand();
					((SPointingRelationCreateCommand) command).setGraph(graph);
					((SPointingRelationCreateCommand) command).setSource(getRelationSource(atomicALParameters));
					((SPointingRelationCreateCommand) command).setTarget(getRelationTarget(atomicALParameters));
					relation = SaltFactory.eINSTANCE.createSPointingRelation();
				}
				else {
					command = new SDominanceRelationCreateCommand();
					((SDominanceRelationCreateCommand) command).setGraph(graph);
					((SDominanceRelationCreateCommand) command).setSource(getRelationSource(atomicALParameters));
					((SDominanceRelationCreateCommand) command).setTarget(getRelationTarget(atomicALParameters));
					relation = SaltFactory.eINSTANCE.createSDominanceRelation();
				}
				// FIXME: Do the following properly
				if (atomicALParameters.get("attributes") != null) {
					LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
					for (Object key : attributes.keySet()) {
						SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
						String val = (String) attributes.get(key);
				        anno.setSName((String) key);
				        anno.setSValue(val);
				        relation.addSAnnotation(anno);
					}
				}
				if (getLayerSwitch().equals("f"))
					((SPointingRelationCreateCommand) command).setSPointingRelation((SPointingRelation) relation);
				else
					((SDominanceRelationCreateCommand) command).setSDominanceRelation((SDominanceRelation) relation);
		    	Display.getDefault().asyncExec(new Runnable() {
		    		public void run() {
		    	    	commandStack.execute(command);
		    		}
		    	});
		    	break;
		    	
			case 'a': // Annotate
				if (atomicALParameters.get("attributes") != null) {
					final LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
					final ArrayList<Object> keys = (ArrayList<Object>) atomicALParameters.get("keys");
					ArrayList<String> elementIDs = (ArrayList<String>) atomicALParameters.get("elements");
					ArrayList<SAnnotatableElement> elementsToAnnotate = new ArrayList<SAnnotatableElement>();
					for (int i = 0; i < elementIDs.size(); i++) {
						String iD = elementIDs.get(i).substring(1);
						// Get respective EObject from elementID
						switch (elementIDs.get(i).charAt(0)) {
						case 'N': // SStructure
							for (SStructure structure : graph.getSStructures()) {
								String valueString = structure.getSName().substring(9); // 9 -> "structure"
								if (iD.equals(valueString)) 
									elementsToAnnotate.add(structure);
							}
							break;
						
						case 'T': // SToken
							for (SToken token : graph.getSTokens()) {
								String valueString = token.getSName().substring(4); // 4 -> "sTok"
								if (iD.equals(valueString)) 
									elementsToAnnotate.add(token);
							}
							break;
							
						case 'P': // SPointingRelation
							for (SPointingRelation pointingRelation : graph.getSPointingRelations()) {
								String valueString = pointingRelation.getSName().substring(12); // 12 -> "sPointingRel"
								if (iD.equals(valueString))
									elementsToAnnotate.add(pointingRelation);
							}
							break;
							
						case 'D': // SDominanceRelation
							for (final SDominanceRelation dominanceRelation : graph.getSDominanceRelations()) {
								String valueString = dominanceRelation.getSName().substring(7); // 7 -> "sDomRel"
								if (iD.equals(valueString)) 
									elementsToAnnotate.add(dominanceRelation);
							}
							break;
							
						default:
							break;
						} 
					}
					for (final SAnnotatableElement element : elementsToAnnotate) {
						Display.getDefault().asyncExec(new Runnable() {
							
							@Override
							public void run() {
								for (Object keyObject : attributes.keySet()) {
									SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
									String key = (String) keyObject; 
									String val = (String) attributes.get(key);
							        anno.setSName(key);
							        anno.setSValue(val);
							        try {
							        	element.addSAnnotation(anno);	
									} catch (GraphInsertException e) {
										element.getSAnnotation(key).setSValue(val);
									}
								}
								for (Object keyObject : keys) {
									final SAnnotationDeleteCommand sAnnotationDeleteCommand = new SAnnotationDeleteCommand();
									SAnnotation anno = element.getSAnnotation((String) keyObject);
									sAnnotationDeleteCommand.setSAnnotation(anno);
									Display.getDefault().asyncExec(new Runnable() {
							    		public void run() {
							    	    	commandStack.execute(sAnnotationDeleteCommand);
							    		}
							    	});
								}
							}
						});
					}
				}
				break;
				
			case 'd': // Delete element
				// Determine type of element & create respective command
				ArrayList<String> elementIDs = (ArrayList<String>) atomicALParameters.get("elements");
				for (int i = 0; i < elementIDs.size(); i++) {
					String iD = elementIDs.get(i).substring(1);
					// Get respective EObject from elementID
					switch (elementIDs.get(i).charAt(0)) {
					case 'N': // SStructure
						for (SStructure structure : graph.getSStructures()) {
							String valueString = structure.getSName().substring(9);
							if (iD.equals(valueString)) {
								final SStructureDeleteCommand sStructureDeleteCommand = new SStructureDeleteCommand();
								sStructureDeleteCommand.setSStructure(structure);
								Display.getDefault().asyncExec(new Runnable() {
						    		public void run() {
						    	    	commandStack.execute(sStructureDeleteCommand);
						    		}
						    	});
							}
						}
						break;
						
					case 'T': // SToken
						// TODO: Implement
						break;
					
					case 'P': // SPointingRelation
						for (SPointingRelation pointingRelation : graph.getSPointingRelations()) {
							String valueString = pointingRelation.getSName().substring(12);
							if (iD.equals(valueString)) {
								final SPointingRelationDeleteCommand sPointingRelationDeleteCommand = new SPointingRelationDeleteCommand();
								sPointingRelationDeleteCommand.setSPointingRelation(pointingRelation);
								Display.getDefault().asyncExec(new Runnable() {
						    		public void run() {
						    	    	commandStack.execute(sPointingRelationDeleteCommand);
						    		}
						    	});
							}
						}
						break;
						
					case 'D': // SDominanceRelation
						for (SDominanceRelation dominanceRelation : graph.getSDominanceRelations()) {
							String valueString = dominanceRelation.getSName().substring(7);
							if (iD.equals(valueString)) {
								final SDominanceRelationDeleteCommand sDominanceRelationDeleteCommand = new SDominanceRelationDeleteCommand();
								sDominanceRelationDeleteCommand.setSDominanceRelation(dominanceRelation);
								Display.getDefault().asyncExec(new Runnable() {
						    		public void run() {
						    	    	commandStack.execute(sDominanceRelationDeleteCommand);
						    		}
						    	});
							}
						}
						break;
					}
				}
				
			case 'p': // Create parent node
			case 'c': // Create child node
				// Check for layer switch
				if (((ArrayList<String>) atomicALParameters.get("layer_switch")).size() > 0)
					setLayerSwitch(((ArrayList<String>) atomicALParameters.get("layer_switch")).get(0));
				// Construct command for new parent node
				final SStructureCreateCommand createSStructureCompoundCommand = new SStructureCreateCommand();
				createSStructureCompoundCommand.setGraph(graph);
				boolean newNodeIsParent = true;
				if (commandChar == 'c')
					newNodeIsParent = false;
				Point location = SimpleLayoutAlgorithm.calculateLocation((ArrayList<String>) atomicALParameters.get("elements"), graph, (SDocumentGraphEditPart) topLevelEditPart, newNodeIsParent);
				createSStructureCompoundCommand.setLocation(location); // FIXME: Or at other position
				SStructure parent = SaltFactory.eINSTANCE.createSStructure();
				// FIXME: Do the following properly
					if (atomicALParameters.get("attributes") != null) {
						LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
						for (Object key : attributes.keySet()) {
							SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
							String val = (String) attributes.get(key);
				            anno.setSName((String) key);
				            anno.setSValue(val);
				            parent.addSAnnotation(anno);
						}
					}
			    createSStructureCompoundCommand.setSStructure(parent);
			    // Construct commands for new edges to children
			    ArrayList<String> childElementIDs = (ArrayList<String>) atomicALParameters.get("elements");
			    ArrayList<SNode> children = new ArrayList<SNode>();
				for (int i = 0; i < childElementIDs.size(); i++) {
					String iD = childElementIDs.get(i).substring(1);
					switch (childElementIDs.get(i).charAt(0)) {
					case 'N': // SStructure
						for (SStructure structure : graph.getSStructures()) {
							String valueString = structure.getSName().substring(9);
							if (iD.equals(valueString))
								children.add(structure);
						}
						break;
					
					case 'T': // SToken
						for (SToken token : graph.getSTokens()) {
							String valueString = token.getSName().substring(4);
							if (iD.equals(valueString))
								children.add(token);
						}
						break;
					
					default:
						break;
					}
				}
				Command chainCommand = createSStructureCompoundCommand;
				for (SNode child : children) {
					if (getLayerSwitch().equals("f")) { // Use SPointingRel
						final SPointingRelationCreateCommand createCompoundSPointingRelationCommand = new SPointingRelationCreateCommand();
						createCompoundSPointingRelationCommand.setGraph(graph);
						if (newNodeIsParent) {
							createCompoundSPointingRelationCommand.setSource(parent);
							createCompoundSPointingRelationCommand.setTarget(child);
						}
						else {
							createCompoundSPointingRelationCommand.setSource(child);
							createCompoundSPointingRelationCommand.setTarget(parent);
						}
						SPointingRelation childSPointingRelation = SaltFactory.eINSTANCE.createSPointingRelation();
						createCompoundSPointingRelationCommand.setSPointingRelation(childSPointingRelation);
				    	chainCommand = chainCommand.chain(createCompoundSPointingRelationCommand);
					}
					else { // Use SDomRel
						final SDominanceRelationCreateCommand createCompoundDominanceSRelationCommand = new SDominanceRelationCreateCommand();
						createCompoundDominanceSRelationCommand.setGraph(graph);
						if (newNodeIsParent) {
							createCompoundDominanceSRelationCommand.setSource(parent);
							createCompoundDominanceSRelationCommand.setTarget(child);
						}
						else {
							createCompoundDominanceSRelationCommand.setSource(child);
							createCompoundDominanceSRelationCommand.setTarget(parent);
						}
						SDominanceRelation childSDomainanceRelation = SaltFactory.eINSTANCE.createSDominanceRelation();
						createCompoundDominanceSRelationCommand.setSDominanceRelation(childSDomainanceRelation);
					    chainCommand = chainCommand.chain(createCompoundDominanceSRelationCommand);
					} 
					final Command executableChainCommand = chainCommand;
					Display.getDefault().asyncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(executableChainCommand);
						}
					});
				}
				// Get elements and create compound command
				break;
		    	
			default:
				break;
			}
	}

	private void displayHelp() {
		try {
			out.write("Command                           Arguments                       Syntax example\n"+
					"n (New node)*                     [key]:[value]                   n pos:np\n"+
					"e (New edge)                      [source] [target] [key]:[value] e n1 n2 r:coref\n"+
					"a (Annotate)*                     [element] [key]:[val] / [key]:  a n1 pos:np\n"+
					"d (Delete element) [element]                                      d t1\n"+
					"p (Group under new parent)*       [element] [element] [key]:[val] p t1 t2 pos:np\n"+
					"c (New common child)*             [element] [element] [key]:[val] c t1 t2 pos:np\n"+
					"t (Append new token)              [string]                        t Foobar\n"+
					"l (Switch annotation level)*      [level]                         l -s\n"+
					"j (Jump to sentence)              [0-9]*                          j 1234\n"+
					"x (Set corpus excerpt to display) [[0-9]*]|[[0-9]*]               x 2|1\n"+
					"\n"+
					"*Level switches can be used with this command.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("unchecked")
	private SNode getRelationTarget(HashMap<Object, Object> atomicALParameters) { // FIXME Optimize!
		SNode target = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(1);
			switch (key.charAt(0)) {
			case 'N': // SStructure
				String keyIntsN = key.substring(1);
				for (SStructure targetSStructure : graph.getSStructures()) {
					String valueString = targetSStructure.getSElementId().getValueString().split("#")[1].split("structure")[1];
					if (keyIntsN.equals(valueString))
						target = targetSStructure;
				}
				break;
				
			case 'T': // SToken
				String keyIntsT = key.substring(1);
				for (SToken targetSToken : graph.getSTokens()) {
					String valueString = targetSToken.getSElementId().getValueString().split("#")[1].split("sTok")[1];
					if (keyIntsT.equals(valueString))
						target = targetSToken;
				}
				break;
				
			default:
				break;
			}
		}
		return target;
	}

	@SuppressWarnings("unchecked")
	private SNode getRelationSource(HashMap<Object, Object> atomicALParameters) {
		SNode source = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(0);
			switch (key.charAt(0)) {
			case 'N': // SStructure
				String keyInts = key.substring(1);
				for (SStructure sourceSStructure : graph.getSStructures()) {
					String valueString = sourceSStructure.getSElementId().getValueString().split("#")[1].split("structure")[1];
					if (keyInts.equals(valueString))
						source = sourceSStructure;
				}
				break;
				
			case 'T': // SToken
				String keyIntsT = key.substring(1);
				for (SToken sourceSToken : graph.getSTokens()) {
					String valueString = sourceSToken.getSElementId().getValueString().split("#")[1].split("sTok")[1];
					if (keyIntsT.equals(valueString))
						source = sourceSToken;
				}
				break;
				
			default:
				break;
			}
		}
		return source;
	}

	/**
	 * @return the layerSwitch
	 */
	public String getLayerSwitch() {
		return layerSwitch;
	}

	/**
	 * @param layerSwitch the layerSwitch to set
	 */
	public void setLayerSwitch(String layerSwitch) {
		this.layerSwitch = layerSwitch;
	}

}
