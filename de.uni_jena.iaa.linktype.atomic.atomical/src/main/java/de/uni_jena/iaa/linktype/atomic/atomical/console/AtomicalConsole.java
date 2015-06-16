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
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.graph.Node;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDominanceRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SOrderRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SPointingRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpanningRelation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructure;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SStructuredNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotatableElement;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SNode;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SRelation;
import de.uni_jena.iaa.linktype.atomic.atomical.parser.AtomicalAnnotationGraphParser;
import de.uni_jena.iaa.linktype.atomic.atomical.utils.AtomicalConsoleUtils;
import de.uni_jena.iaa.linktype.atomic.core.corpus.GraphElementRegistry;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.GraphEditor;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.AnnotationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.ElementAnnotateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeCreateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.NodeDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationCreateCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.commands.RelationDeleteCommand;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.parts.GraphPart;
import de.uni_jena.iaa.linktype.atomic.views.layerview.LayerView;

/**
 * @author Stephan Druskat
 * 
 */
public class AtomicalConsole extends IOConsole implements Runnable, ISelectionProvider {

	private IOConsoleOutputStream out;
	private SDocumentGraph graph;
	private IEditorPart editor;
	private GraphPart graphPart;
	private String edgeSwitch;
	private IOConsoleOutputStream err;
	private ListenerList listeners = new ListenerList();
	private Combo combo;
	private SLayer layer;

	public AtomicalConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor);
		addEditorListener();
		out = newOutputStream();
		err = newOutputStream();
		Thread t = new Thread(this);
		t.start();
	}

	@Override
	public void run() {
		try {
			out.write("To display a list of available commands, type \"help\".\n");
		}
		catch (IOException e1) {
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
				}
				else {
					processInput(input);
				}
			}
			catch (IOException e) {
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
		else if (atomicALCommand.equalsIgnoreCase("clear")) {
			clearConsole();
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
			// boolean canExecute = validateInput(atomicALCommand,
			// atomicALParameters);
			// if (canExecute) {
			try {
				executeCommand(commandStack, atomicALCommand, atomicALParameters);
			}
			catch (Exception e) {
				e.printStackTrace();
				err.write("Command could not be executed due to an error in the syntax.\n");
			}
			// }
			// else {
			// err.write("Command could not be executed due to an error in the syntax.\n");
			// }
		}
	}

	private boolean validateInput(String atomicALCommand, HashMap<Object, Object> atomicALParameters) {
		// FIXME: Implement
		return true;
	}

	private void executeCommand(final CommandStack commandStack, String atomicALCommand, HashMap<Object, Object> atomicALParameters) throws IOException {
		// FIXME TODO Refactor for readability/re-usability + check for uncaught
		// exceptions
		char commandChar = 0;
		try {
			commandChar = atomicALCommand.charAt(0);
		}
		catch (StringIndexOutOfBoundsException e) { // Thrown when only return
													// is pressed in the
													// console
			return;
		}
		catch (Exception e1) {
			e1.printStackTrace();
		}
		switch (commandChar) {
		case 'l': // Switch levels
			if (((ArrayList<String>) atomicALParameters.get("integer")).size() > 0) {
				int layerInt = Integer.parseInt(((ArrayList<String>) atomicALParameters.get("integer")).get(0));
				if (getGraph().getSLayers().size() == layerInt) {
					// NO ASSIGNED LEVEL is to be activated
					// getGraphPart().setActiveLayer("\u269B NO ASSIGNED LEVEL \u269B");
					getGraphPart().setActiveLayer(null);
					out.write("Active layer is now \"\u269B NO ASSIGNED LEVEL \u269B\".\r\n");
					// FIXME Breaks loose coupling BIG TIME!
					IViewPart layerView = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView("de.uni_jena.iaa.linktype.atomic.views.layerview");
					if (layerView instanceof LayerView) {
						combo = ((LayerView) layerView).getLayerCombo();
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								for (int i = 0; i < combo.getItems().length; i++) {
									if (combo.getItems()[i].equals("\u269B NO ACTIVE LEVEL \u269B")) {
										combo.select(i);
									}
								}
							}
						});
					}
				}
				else {
					layer = getGraph().getSLayers().get(Integer.parseInt(((ArrayList<String>) atomicALParameters.get("integer")).get(0)));
					getGraphPart().setActiveLayer(layer);
					out.write("Active layer is now \"" + layer.getSName() + "\".\r\n");
					// FIXME Breaks loose coupling BIG TIME!
					IViewPart layerView = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView("de.uni_jena.iaa.linktype.atomic.views.layerview");
					if (layerView instanceof LayerView) {
						combo = ((LayerView) layerView).getLayerCombo();
						Display.getDefault().syncExec(new Runnable() {
							
							@Override
							public void run() {
								for (int i = 0; i < combo.getItems().length; i++) {
									if (combo.getItems()[i].equals(layer.getSName())) {
										combo.select(i);
									}
								}
							}
						});
					}
				}
			}
			break;

		case 'n': // Create SStructure with or without annotations
			final NodeCreateCommand createNodeCommand = new NodeCreateCommand();
			createNodeCommand.setGraph(graph);
			createNodeCommand.setLocation(new Point(100, 100)); // FIXME: Or at
																// other
																// position
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

		case 'e': // Create a new edge
			// Check for edge switch
			SaltFactory sf = SaltFactory.eINSTANCE;
			if (((ArrayList<String>) atomicALParameters.get("switch")).size() > 0)
				setEdgeSwitch(((ArrayList<String>) atomicALParameters.get("switch")).get(0));
			final RelationCreateCommand command = new RelationCreateCommand();
			SRelation relation = null;
			command.setGraph(getGraph());
			command.setSource(getRelationSource(atomicALParameters));
			command.setTarget(getRelationTarget(atomicALParameters));
			if (getEdgeSwitch().equals("d")) {
				relation = sf.createSDominanceRelation();
			}
			else if (getEdgeSwitch().equals("r")) {
				relation = sf.createSSpanningRelation();
			}
			else if (getEdgeSwitch().equals("p")) {
				relation = sf.createSPointingRelation();
			}
			else if (getEdgeSwitch().equals("o")) {
				relation = sf.createSOrderRelation();
			}
			command.setRelation(relation);
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
					String id = elementIDs.get(i);
					elementsToAnnotate.add((SAnnotatableElement) GraphElementRegistry.getElementForID(id, getGraph()));
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
								namespace = null; // FIXME: Remove once
													// namespace is implemented
													// for console
								annotationsToAdd.put(key, Pair.of(namespace, val));
							}
							ElementAnnotateCommand elementAnnotateCommand = new ElementAnnotateCommand();
							elementAnnotateCommand.setModel(element);
							elementAnnotateCommand.setAnnotations(annotationsToAdd);
							commandStack.execute(elementAnnotateCommand);
							for (Object keyObject : keys) {
								if (attributes.get(keyObject) == null) { // I.e.,
																			// no
																			// value,
																			// i.e.,
																			// annotation
																			// should
																			// be
																			// deleted
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
					SStructure structure = (SStructure) GraphElementRegistry.getElementForID("N" + iD, getGraph());
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
					SSpan span = (SSpan) GraphElementRegistry.getElementForID("S" + iD, getGraph());
					final NodeDeleteCommand spanDeleteCommand = new NodeDeleteCommand();
					spanDeleteCommand.setModel(span);
					spanDeleteCommand.setGraph(span.getSGraph());
					spanDeleteCommand.setCoordinates(((AbstractGraphicalEditPart) getGraphPart().getViewer().getEditPartRegistry().get(span)).getFigure().getBounds());
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(spanDeleteCommand);
						}
					});
					break;
				case 't':
				case 'T': // SToken
					out.write("This operation is currently unsupported.\n");
					break;
				case 'p':
				case 'P': // SPointingRelation
					SPointingRelation pointingRelation = (SPointingRelation) GraphElementRegistry.getElementForID("P" + iD, getGraph());
					final RelationDeleteCommand pointingRelDeleteCommand = new RelationDeleteCommand();
					pointingRelDeleteCommand.setRelation(pointingRelation);
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(pointingRelDeleteCommand);
						}
					});
					break;
				case 'd':
				case 'D': // SDominanceRelation
					SDominanceRelation dominanceRelation = (SDominanceRelation) GraphElementRegistry.getElementForID("D" + iD, getGraph());
					final RelationDeleteCommand dominanceRelDeleteCommand = new RelationDeleteCommand();
					dominanceRelDeleteCommand.setRelation(dominanceRelation);
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(dominanceRelDeleteCommand);
						}
					});
					break;
				case 'o':
				case 'O': // SDominanceRelation
					SOrderRelation orderRelation = (SOrderRelation) GraphElementRegistry.getElementForID("O" + iD, getGraph());
					final RelationDeleteCommand orderRelDeleteCommand = new RelationDeleteCommand();
					orderRelDeleteCommand.setRelation(orderRelation);
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(orderRelDeleteCommand);
						}
					});
					break;
				case 'r':
				case 'R': // SSpanningRelation
					SSpanningRelation spanningRelation = (SSpanningRelation) GraphElementRegistry.getElementForID("R" + iD, getGraph());
					final RelationDeleteCommand spanningRelDeleteCommand = new RelationDeleteCommand();
					spanningRelDeleteCommand.setRelation(spanningRelation);
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							commandStack.execute(spanningRelDeleteCommand);
						}
					});
					break;
				}
			}
			break;

		case 'p': // Create parent node
		case 's':
			SStructuredNode parent;
			final NodeCreateCommand createParentNodeCommand = new NodeCreateCommand();
			createParentNodeCommand.setGraph(getGraph());
			if (commandChar == 'p') {
				parent = SaltFactory.eINSTANCE.createSStructure();
			}
			else {
				parent = SaltFactory.eINSTANCE.createSSpan();
			}
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
			createParentNodeCommand.setModel(parent);
			// Construct commands for new edges to children
			ArrayList<String> childElementIDs = (ArrayList<String>) atomicALParameters.get("elements");
			ArrayList<SNode> children = new ArrayList<SNode>();
			for (int i = 0; i < childElementIDs.size(); i++) {
				String id = childElementIDs.get(i);
				children.add((SNode) GraphElementRegistry.getElementForID(id, getGraph()));
			}
			Map registry = getGraphPart().getViewer().getEditPartRegistry();
			List<EditPart> childrenEPs = new ArrayList<EditPart>();
			for (SNode child : children) {
				EditPart childEP = (EditPart) registry.get(child);
				childrenEPs.add(childEP);
			}
			createParentNodeCommand.setSelectedEditParts(childrenEPs);
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					commandStack.execute(createParentNodeCommand);
				}
			});
			// }
			// Get elements and create compound command
			break;

		default:
			err.write("Unknown command. Please type \"help\" to see a list of the available commands.\n");
			break;
		}
	}

	private Node getRelationTarget(HashMap<Object, Object> atomicALParameters) {
		SNode target = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(1); // FIXME:
																							// Add
																							// check
			key = key.toUpperCase();
			target = (SNode) GraphElementRegistry.getElementForID(key, getGraph());
			return target;
		}
		return target;
	}

	private Node getRelationSource(HashMap<Object, Object> atomicALParameters) {
		SNode source = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(0);
			key = key.toUpperCase();
			source = (SNode) GraphElementRegistry.getElementForID(key, getGraph());
		}
		return source;
	}

	private void displayHelp() {
		try {
			out.write("Command                           Arguments                               Syntax example\n" + "n (New structure node)" + /***/
			"            [key]:[value]                           n pos:np\n" + "s (New span node)" + /***/
			"                 [element] [element] [key]:[val]         s t1 t2 type:np\n" + "e (New edge)                      -[type] [source] [target] [key]:[value] e -d n1 n2 r:coref\n" + "a (Annotate)" + /***/
			"                      [element] [key]:[val] / [key]:          a n1 pos:np\n" + "d (Delete element)                [element] [element]                     d t1 n2\n" + "p (Group under new parent)        [element] [element] [key]:[val]         p t1 t2 pos:np\n" + 
			"l (Switch levels)                 [level index]                           l 1               \n" +
			"help (Displays this command overview)\n" + "clear (Clears the console)\n"
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
		}
		catch (IOException e) {
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
					if (getGraphPart().getModel() == getGraph()) {
						return;
					}
					console.setGraph(getGraphPart().getModel());
					console.setEditor(editor);
					String excerpt = null;
					String text = getGraph().getSTextualDSs().get(0).getSText();
					if (text.length() >= 50) {
						excerpt = getGraph().getSTextualDSs().get(0).getSText().substring(0, 49) + "...";
					}
					else {
						excerpt = text;
					}
					try {
						console.out.write("Working on " + editor.getEditorInput().getName() + " (\"" + excerpt + "\").\n");
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else if (part instanceof IEditorPart) {
					try {
						console.out.write("AtomicAL is not (yet) available for this editor type.\n");
					}
					catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					activePage.hideView(activePage.findView(IConsoleConstants.ID_CONSOLE_VIEW));
				}
				catch (NullPointerException e) {
					// Do nothing. Will throw an NPE when exiting the
					// application with the Console View open, as
					// activePage will be null.
				}
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
			}

			@Override
			public void partOpened(IWorkbenchPart part) {
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
	 * @param graph
	 *            the graph to set
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
	 * @param editor
	 *            the editor to set
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
	 * @param graphPart
	 *            the graphPart to set
	 */
	public void setGraphPart(GraphPart graphPart) {
		this.graphPart = graphPart;
	}

	/**
	 * @return the edgeSwitch
	 */
	public String getEdgeSwitch() {
		return edgeSwitch;
	}

	/**
	 * @param edgeSwitch
	 *            the edgeSwitch to set
	 */
	public void setEdgeSwitch(String edgeSwitch) {
		this.edgeSwitch = edgeSwitch;
	}

	/**
	 * @return the out
	 */
	public IOConsoleOutputStream getOut() {
		return out;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub
		
	}

}
