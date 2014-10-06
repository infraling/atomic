/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.atomical.parser.AtomicalAnnotationGraphParser;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.GraphEditor;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.ElementAnnotateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeCreateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicalConsole extends IOConsole implements Runnable {

	private IOConsoleOutputStream out;
	private SDocumentGraph graph;
	private IEditorPart editor;
	private GraphPart graphPart;

	public AtomicalConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		addEditorListener();
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		out = newOutputStream();
		try {
			out.write("To display a list of available commands, type \"help\".\n");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

	private void processInput(String input) throws IOException {
		String atomicALCommand = AtomicalAnnotationGraphParser.parseCommand(input);
		String rawParameters = AtomicalAnnotationGraphParser.parseRawParameters(input);
		HashMap<Object, Object> atomicALParameters = AtomicalAnnotationGraphParser.parseParameters(rawParameters);
		IEditorPart editor = null;
		if (atomicALCommand.equalsIgnoreCase("help")) {
			displayHelp();
			return;
		}
		try {
			editor = getEditor();
		}
		catch (NullPointerException e) {
			out.write("No active editor. Command will be ignored.\n");
		}
		if (editor != null) {
			final CommandStack commandStack = ((GraphEditor) editor).getDomain().getCommandStack();
			executeCommand(commandStack, atomicALCommand, atomicALParameters);
		}
	}
	
	private void executeCommand(final CommandStack commandStack, String atomicALCommand, HashMap<Object, Object> atomicALParameters) throws IOException {
		// FIXME TODO Refactor for readability/re-usability + check for uncaught exceptions
				char commandChar = 0;
				try {
					commandChar = atomicALCommand.charAt(0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				switch (commandChar) {
					case 'n': // Create SStructure with or without annotations
						final NodeCreateCommand createNodeCommand = new NodeCreateCommand();
						createNodeCommand.setGraph(graph);
						createNodeCommand.setLocation(new Point(100, 100)); // FIXME: Or at other position
						SStructure sStructure = SaltFactory.eINSTANCE.createSStructure();
						// FIXME: Do the following properly
							if (atomicALParameters.get("attributes") != null) {
								@SuppressWarnings("unchecked")
								LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
								for (Object key : attributes.keySet()) {
									SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
									String val = (String) attributes.get(key);
						            anno.setSName((String) key);
						            anno.setSValue(val);
						            sStructure.addSAnnotation(anno);
								}
							}
				    	createNodeCommand.setModel(sStructure);
				    	Display.getDefault().asyncExec(new Runnable() {
				    		public void run() {
				    	    	commandStack.execute(createNodeCommand);
				    		}
				    	});
				    	break;
					
//					case 'e': // Create a new edge
//						// Check for layer switch
//						if (((ArrayList<String>) atomicALParameters.get("layer_switch")).size() > 0)
//							setLayerSwitch(((ArrayList<String>) atomicALParameters.get("layer_switch")).get(0));
//						final Command command;
//						SRelation relation;
//						if (getLayerSwitch().equals("f")) {
//							command = new SPointingRelationCreateCommand();
//							((SPointingRelationCreateCommand) command).setGraph(graph);
//							((SPointingRelationCreateCommand) command).setSource(getRelationSource(atomicALParameters));
//							((SPointingRelationCreateCommand) command).setTarget(getRelationTarget(atomicALParameters));
//							relation = SaltFactory.eINSTANCE.createSPointingRelation();
//						}
//						else {
//							command = new SDominanceRelationCreateCommand();
//							((SDominanceRelationCreateCommand) command).setGraph(graph);
//							((SDominanceRelationCreateCommand) command).setSource(getRelationSource(atomicALParameters));
//							((SDominanceRelationCreateCommand) command).setTarget(getRelationTarget(atomicALParameters));
//							relation = SaltFactory.eINSTANCE.createSDominanceRelation();
//						}
//						// FIXME: Do the following properly
//						if (atomicALParameters.get("attributes") != null) {
//							LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
//							for (Object key : attributes.keySet()) {
//								SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
//								String val = (String) attributes.get(key);
//						        anno.setSName((String) key);
//						        anno.setSValue(val);
//						        relation.addSAnnotation(anno);
//							}
//						}
//						if (getLayerSwitch().equals("f"))
//							((SPointingRelationCreateCommand) command).setSPointingRelation((SPointingRelation) relation);
//						else
//							((SDominanceRelationCreateCommand) command).setSDominanceRelation((SDominanceRelation) relation);
//				    	Display.getDefault().asyncExec(new Runnable() {
//				    		public void run() {
//				    	    	commandStack.execute(command);
//				    		}
//				    	});
//				    	break;
//				    	
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
								case 'N':
								case 'n': // SStructure
									SStructure structure = getGraph().getSStructures().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(structure);
									break;
								
								case 'T':	
								case 't': // SToken
									SToken token = getGraph().getSTokens().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(token);
									break;
									
								case 'S':
								case 's': // SSpan
									SSpan span = getGraph().getSSpans().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(span);
									break;
								
								case 'P':	
								case 'p': // SPointingRelation
									SPointingRelation pointingRelation = getGraph().getSPointingRelations().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(pointingRelation);
									break;
									
								case 'D':
								case 'd': // SDominanceRelation
									SDominanceRelation dominanceRelation = getGraph().getSDominanceRelations().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(dominanceRelation);
									break;
								
								case 'R':	
								case 'r': // SSpanningRelation
									SSpanningRelation spanningRelation = getGraph().getSSpanningRelations().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(spanningRelation);
									break;
									
								case 'O':
								case 'o': // SOrderRelation
									SOrderRelation orderRelation = getGraph().getSOrderRelations().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
									elementsToAnnotate.add(orderRelation);
									break;

								default:
									break;
								} 
							}
							for (final SAnnotatableElement element : elementsToAnnotate) {
								Display.getDefault().asyncExec(new Runnable() {
									
									@Override
									public void run() {
										TreeMap<String, Pair<String, String>> annotationsToAdd = new TreeMap<String, Pair<String, String>>();
										String namespace = null, key, val;
										for (SAnnotation existingAnnotation : element.getSAnnotations()) {
											namespace = existingAnnotation.getNamespace();
											key = existingAnnotation.getName();
											val = existingAnnotation.getValue().toString();
											annotationsToAdd.put(key, Pair.of(namespace, val));
										}
										for (Object keyObject : attributes.keySet()) {
											key = (String) keyObject; 
											val = (String) attributes.get(key);
											namespace = null; // FIXME: Remove once namespace is implemented for console
									        annotationsToAdd.put(key, Pair.of(namespace, val));
										}
								        ElementAnnotateCommand elementAnnotateCommand = new ElementAnnotateCommand();
								        elementAnnotateCommand.setModel(element);
								        elementAnnotateCommand.setAnnotations(annotationsToAdd);
								        commandStack.execute(elementAnnotateCommand);
										for (Object keyObject : keys) {
											if (attributes.get(keyObject) == null) { // I.e., no value, i.e., annotation should be deleted
												final AnnotationDeleteCommand sAnnotationDeleteCommand = new AnnotationDeleteCommand();
												SAnnotation anno = element.getSAnnotation((String) keyObject);
												sAnnotationDeleteCommand.setModel(anno);
												sAnnotationDeleteCommand.setModelParent(element);
								    	    	commandStack.execute(sAnnotationDeleteCommand);
											}
										}
									}
								});
							}
						}
						break;
//						
					case 'd': // Delete element
						// Determine type of element & create respective command
						ArrayList<String> elementIDs = (ArrayList<String>) atomicALParameters.get("elements");
						for (int i = 0; i < elementIDs.size(); i++) {
							String iD = elementIDs.get(i).substring(1);
							// Get respective EObject from elementID
							switch (elementIDs.get(i).charAt(0)) {
							case 'n':
							case 'N': // SStructure
								SStructure structure = (SStructure) getGraphPart().getVisualIDMap().get("N" + iD);
								final NodeDeleteCommand nodeDeleteCommand = new NodeDeleteCommand();
								nodeDeleteCommand.setModel(structure);
								nodeDeleteCommand.setGraph(structure.getSGraph());
								nodeDeleteCommand.setCoordinates(((AbstractGraphicalEditPart) getGraphPart().getViewer().getEditPartRegistry().get(structure)).getFigure().getBounds());
								Display.getDefault().syncExec(new Runnable() {
									@Override
									public void run() {
										commandStack.execute(nodeDeleteCommand);
									}
								});
								break;
							case 's':
							case 'S': // SSpan
//							SSpanningRelation spanningRelation = graph.getSSpanningRelations().get(Integer.parseInt(iD) - 1); // -1 because the label shows id index+1
								
//								for (SStructure structure : graph.getSStructures()) {
//									String valueString = structure.getSName().substring(9);
//									if (iD.equals(valueString)) {
//										final SStructureDeleteCommand sStructureDeleteCommand = new SStructureDeleteCommand();
//										sStructureDeleteCommand.setSStructure(structure);
//										Display.getDefault().asyncExec(new Runnable() {
//								    		public void run() {
//								    	    	commandStack.execute(sStructureDeleteCommand);
//								    		}
//								    	});
//									}
//								}
								break;
								
							case 'T': // SToken
								// TODO: Implement
								break;
							
							case 'P': // SPointingRelation
//								for (SPointingRelation pointingRelation : graph.getSPointingRelations()) {
//									String valueString = pointingRelation.getSName().substring(12);
//									if (iD.equals(valueString)) {
//										final SPointingRelationDeleteCommand sPointingRelationDeleteCommand = new SPointingRelationDeleteCommand();
//										sPointingRelationDeleteCommand.setSPointingRelation(pointingRelation);
//										Display.getDefault().asyncExec(new Runnable() {
//								    		public void run() {
//								    	    	commandStack.execute(sPointingRelationDeleteCommand);
//								    		}
//								    	});
//									}
//								}
								break;
								
							case 'D': // SDominanceRelation
//								for (SDominanceRelation dominanceRelation : graph.getSDominanceRelations()) {
//									String valueString = dominanceRelation.getSName().substring(7);
//									if (iD.equals(valueString)) {
//										final SDominanceRelationDeleteCommand sDominanceRelationDeleteCommand = new SDominanceRelationDeleteCommand();
//										sDominanceRelationDeleteCommand.setSDominanceRelation(dominanceRelation);
//										Display.getDefault().asyncExec(new Runnable() {
//								    		public void run() {
//								    	    	commandStack.execute(sDominanceRelationDeleteCommand);
//								    		}
//								    	});
//									}
//								}
								break;
							}
						}
//						
//					case 'p': // Create parent node
//					case 'c': // Create child node
//						// Check for layer switch
//						if (((ArrayList<String>) atomicALParameters.get("layer_switch")).size() > 0)
//							setLayerSwitch(((ArrayList<String>) atomicALParameters.get("layer_switch")).get(0));
//						// Construct command for new parent node
//						final SStructureCreateCommand createSStructureCompoundCommand = new SStructureCreateCommand();
//						createSStructureCompoundCommand.setGraph(graph);
//						boolean newNodeIsParent = true;
//						if (commandChar == 'c')
//							newNodeIsParent = false;
//						Point location = SimpleLayoutAlgorithm.calculateLocation((ArrayList<String>) atomicALParameters.get("elements"), graph, (SDocumentGraphEditPart) topLevelEditPart, newNodeIsParent);
//						createSStructureCompoundCommand.setLocation(location); // FIXME: Or at other position
//						SStructure parent = SaltFactory.eINSTANCE.createSStructure();
//						// FIXME: Do the following properly
//							if (atomicALParameters.get("attributes") != null) {
//								LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
//								for (Object key : attributes.keySet()) {
//									SAnnotation anno = SaltFactory.eINSTANCE.createSAnnotation();
//									String val = (String) attributes.get(key);
//						            anno.setSName((String) key);
//						            anno.setSValue(val);
//						            parent.addSAnnotation(anno);
//								}
//							}
//					    createSStructureCompoundCommand.setSStructure(parent);
//					    // Construct commands for new edges to children
//					    ArrayList<String> childElementIDs = (ArrayList<String>) atomicALParameters.get("elements");
//					    ArrayList<SNode> children = new ArrayList<SNode>();
//						for (int i = 0; i < childElementIDs.size(); i++) {
//							String iD = childElementIDs.get(i).substring(1);
//							switch (childElementIDs.get(i).charAt(0)) {
//							case 'N': // SStructure
//								for (SStructure structure : graph.getSStructures()) {
//									String valueString = structure.getSName().substring(9);
//									if (iD.equals(valueString))
//										children.add(structure);
//								}
//								break;
//							
//							case 'T': // SToken
//								for (SToken token : graph.getSTokens()) {
//									String valueString = token.getSName().substring(4);
//									if (iD.equals(valueString))
//										children.add(token);
//								}
//								break;
//							
//							default:
//								break;
//							}
//						}
//						Command chainCommand = createSStructureCompoundCommand;
//						for (SNode child : children) {
//							if (getLayerSwitch().equals("f")) { // Use SPointingRel
//								final SPointingRelationCreateCommand createCompoundSPointingRelationCommand = new SPointingRelationCreateCommand();
//								createCompoundSPointingRelationCommand.setGraph(graph);
//								if (newNodeIsParent) {
//									createCompoundSPointingRelationCommand.setSource(parent);
//									createCompoundSPointingRelationCommand.setTarget(child);
//								}
//								else {
//									createCompoundSPointingRelationCommand.setSource(child);
//									createCompoundSPointingRelationCommand.setTarget(parent);
//								}
//								SPointingRelation childSPointingRelation = SaltFactory.eINSTANCE.createSPointingRelation();
//								createCompoundSPointingRelationCommand.setSPointingRelation(childSPointingRelation);
//						    	chainCommand = chainCommand.chain(createCompoundSPointingRelationCommand);
//							}
//							else { // Use SDomRel
//								final SDominanceRelationCreateCommand createCompoundDominanceSRelationCommand = new SDominanceRelationCreateCommand();
//								createCompoundDominanceSRelationCommand.setGraph(graph);
//								if (newNodeIsParent) {
//									createCompoundDominanceSRelationCommand.setSource(parent);
//									createCompoundDominanceSRelationCommand.setTarget(child);
//								}
//								else {
//									createCompoundDominanceSRelationCommand.setSource(child);
//									createCompoundDominanceSRelationCommand.setTarget(parent);
//								}
//								SDominanceRelation childSDomainanceRelation = SaltFactory.eINSTANCE.createSDominanceRelation();
//								createCompoundDominanceSRelationCommand.setSDominanceRelation(childSDomainanceRelation);
//							    chainCommand = chainCommand.chain(createCompoundDominanceSRelationCommand);
//							} 
//							final Command executableChainCommand = chainCommand;
//							Display.getDefault().asyncExec(new Runnable() {
//								@Override
//								public void run() {
//									commandStack.execute(executableChainCommand);
//								}
//							});
//						}
//						// Get elements and create compound command
//						break;
				    	
					default:
						break;
					}
			}		
	private void displayHelp() {
		try {
			out.write("Command                           Arguments                       Syntax example\n"+
					"n (New structure node)"+/***/"            [key]:[value]                   n pos:np\n"+
					"s (New span node)"+/***/"                 [element] [element] [key]:[val] s t1 t2 type:np\n"+
					"e (New edge)                      [source] [target] [key]:[value] e n1 n2 r:coref\n"+
					"a (Annotate)"+/***/"                      [element] [key]:[val] / [key]:  a n1 pos:np\n"+
					"d (Delete element)                [element]                       d t1\n"+
					"p (Group under new parent)"+/***/"        [element] [element] [key]:[val] p t1 t2 pos:np\n"/*+
					"c (New common child)*             [element] [element] [key]:[val] c t1 t2 pos:np\n"+
					"t (Append new token)              [string]                        t Foobar\n"+
					"l (Switch annotation level)*      [level]                         l -s\n"+
					"j (Jump to sentence)              [0-9]*                          j 1234\n"+
					"x (Set corpus excerpt to display) [[0-9]*]|[[0-9]*]               x 2|1\n"+
					"\n"+
					"*Level switches can be used with this command."*/);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addEditorListener() {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(new IPartListener() {
			
			AtomicalConsole console = AtomicalConsole.this;
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				activateAtomicalForEditorInput(part);
			}

			/**
			 * @param part
			 */
			private void activateAtomicalForEditorInput(IWorkbenchPart part) {
				if (part instanceof GraphEditor) {
					IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
					EditPartViewer editPartViewer = ((GraphEditor) editor).getEditPartViewer();
					console.setGraphPart((GraphPart) editPartViewer.getContents());
					console.setGraph(console.getGraphPart().getModel());
					console.setEditor(editor);
					try {
						console.out.write("Working on " + editor.getEditorInput().getName() + ".\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (part instanceof IEditorPart) {
					try {
						console.out.write("AtomicAL is not (yet) available for this editor type");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
			}

			@Override public void partBroughtToTop(IWorkbenchPart part) {}
			@Override public void partClosed(IWorkbenchPart part) {}
			@Override public void partDeactivated(IWorkbenchPart part) {}
			@Override public void partOpened(IWorkbenchPart part) {}
			
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

	/**
	 * @return the editor
	 */
	public IEditorPart getEditor() {
		return editor;
	}

	/**
	 * @param editor the editor to set
	 */
	public void setEditor(IEditorPart editor) {
		this.editor = editor;
	}

	/**
	 * @return the graphPart
	 */
	public GraphPart getGraphPart() {
		return graphPart;
	}

	/**
	 * @param graphPart the graphPart to set
	 */
	public void setGraphPart(GraphPart graphPart) {
		this.graphPart = graphPart;
	}

}
