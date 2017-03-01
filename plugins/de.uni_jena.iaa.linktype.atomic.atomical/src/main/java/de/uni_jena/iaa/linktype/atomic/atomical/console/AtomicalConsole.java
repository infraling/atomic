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
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SDominanceRelation;
import org.corpus_tools.salt.common.SOrderRelation;
import org.corpus_tools.salt.common.SPointingRelation;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SSpanningRelation;
import org.corpus_tools.salt.common.SStructure;
import org.corpus_tools.salt.common.SStructuredNode;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SLayer;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;


import de.uni_jena.iaa.linktype.atomic.atomical.parser.AtomicalAnnotationGraphParser;
import de.uni_jena.iaa.linktype.atomic.atomical.utils.AtomicalConsoleUtils;


/**
 * @author Stephan Druskat
 * 
 */
public class AtomicalConsole extends IOConsole implements Runnable, ISelectionProvider {

	private IOConsoleOutputStream out;
	private DocumentGraphEditor docGraphEditor;
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
		if (atomicALCommand.equalsIgnoreCase("help")) {
			displayHelp();
			return;
		}
		else if (atomicALCommand.equalsIgnoreCase("clear")) {
			clearConsole();
			return;
		}

		DocumentGraphEditor editor = getEditor();
		if(editor == null) {
			out.write("No active editor. Command will be ignored.\n");
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
				if (getGraph().getLayers().size() == layerInt) {
					// NO ASSIGNED LEVEL is to be activated
					// getGraphPart().setActiveLayer("\u269B NO ASSIGNED LEVEL \u269B");
					out.write("Active layer is now \"\u269B NO ASSIGNED LEVEL \u269B\".\r\n");
					updateLayerView("\u269B NO ACTIVE LEVEL \u269B");
				}
				else {
					List<SLayer> layerList = new ArrayList<SLayer>(getGraph().getLayers());
					layer = layerList.get(Integer.parseInt(((ArrayList<String>) atomicALParameters.get("integer")).get(0)));
					out.write("Active layer is now \"" + layer.getName() + "\".\r\n");
					updateLayerView(layer.getName());
				}
			}
			break;

		case 'n': // Create SStructure with or without annotations

			SStructure sStructure = SaltFactory.createSStructure();
			// FIXME: Do the following properly
			if (atomicALParameters.get("attributes") != null) {
				@SuppressWarnings("unchecked")
				LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
				for (Object key : attributes.keySet()) {
					SAnnotation anno = SaltFactory.createSAnnotation();
					String val = (String) attributes.get(key);
					anno.setName((String) key);
					anno.setValue(val);
					sStructure.addAnnotation(anno);
				}
			}
			break;

		case 'e': // Create a new edge
			// Check for edge switch
			if (((ArrayList<String>) atomicALParameters.get("switch")).size() > 0)
				setEdgeSwitch(((ArrayList<String>) atomicALParameters.get("switch")).get(0));
			SRelation relation = null;
			
			if (getEdgeSwitch().equals("d")) {
				relation = SaltFactory.createSDominanceRelation();
			}
			else if (getEdgeSwitch().equals("r")) {
				relation = SaltFactory.createSSpanningRelation();
			}
			else if (getEdgeSwitch().equals("p")) {
				relation = SaltFactory.createSPointingRelation();
			}
			else if (getEdgeSwitch().equals("o")) {
				relation = SaltFactory.createSOrderRelation();
			}
			// FIXME: Do the following properly
			if (atomicALParameters.get("attributes") != null) {
				LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
				for (Object key : attributes.keySet()) {
					SAnnotation anno = SaltFactory.createSAnnotation();
					String val = (String) attributes.get(key);
					anno.setName((String) key);
					anno.setValue(val);
					relation.addAnnotation(anno);
				}
			}
			break;

		case 'a': // Annotate
			if (atomicALParameters.get("attributes") != null) {
				final LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
				final ArrayList<Object> keys = (ArrayList<Object>) atomicALParameters.get("keys");
				ArrayList<String> elementIDs = (ArrayList<String>) atomicALParameters.get("elements");
				ArrayList<SNode> elementsToAnnotate = new ArrayList<>();
				for (int i = 0; i < elementIDs.size(); i++) {
					String id = elementIDs.get(i);
					elementsToAnnotate.add(getGraph().getNode(id));
				}
				for (final SNode element : elementsToAnnotate) {
					Display.getDefault().asyncExec(new Runnable() {

						@Override
						public void run() {
							TreeMap<String, Pair<String, String>> annotationsToAdd = new TreeMap<String, Pair<String, String>>();
							String namespace = null, key, val;
							for (SAnnotation existingAnnotation : element.getAnnotations()) {
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
							
							for (Object keyObject : keys) {
								if (attributes.get(keyObject) == null) { // I.e.,
																			// no
																			// value,
																			// i.e.,
																			// annotation
																			// should
																			// be
																			// deleted
									SAnnotation anno = element.getAnnotation((String) keyObject);
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
					SStructure structure = (SStructure) getGraph().getNode("N" + iD);

					break;
				case 's':
				case 'S': // SSpan
					SSpan span = (SSpan) getGraph().getNode("S" + iD);

					break;
				case 't':
				case 'T': // SToken
					out.write("This operation is currently unsupported.\n");
					break;
				case 'p':
				case 'P': // SPointingRelation
					SPointingRelation pointingRelation = (SPointingRelation) getGraph().getNode("P" + iD);
					
					break;
				case 'd':
				case 'D': // SDominanceRelation
					SDominanceRelation dominanceRelation = (SDominanceRelation) getGraph().getNode("D" + iD);
					
					break;
				case 'o':
				case 'O': // SDominanceRelation
					SOrderRelation orderRelation = (SOrderRelation) getGraph().getNode("O" + iD);
					
					break;
				case 'r':
				case 'R': // SSpanningRelation
					SSpanningRelation spanningRelation = (SSpanningRelation) getGraph().getNode("R" + iD);

					break;
				}
			}
			break;

		case 'p': // Create parent node
		case 's':
			SStructuredNode parent;
			if (commandChar == 'p') {
				parent = SaltFactory.createSStructure();
			}
			else {
				parent = SaltFactory.createSSpan();
			}
			// FIXME: Do the following properly
			if (atomicALParameters.get("attributes") != null) {
				LinkedHashMap<Object, Object> attributes = (LinkedHashMap<Object, Object>) atomicALParameters.get("attributes");
				for (Object key : attributes.keySet()) {
					SAnnotation anno = SaltFactory.createSAnnotation();
					String val = (String) attributes.get(key);
					anno.setName((String) key);
					anno.setValue(val);
					parent.addAnnotation(anno);
				}
			}
			// Construct commands for new edges to children
			ArrayList<String> childElementIDs = (ArrayList<String>) atomicALParameters.get("elements");
			ArrayList<SNode> children = new ArrayList<SNode>();
			for (int i = 0; i < childElementIDs.size(); i++) {
				String id = childElementIDs.get(i);
				children.add((SNode) getGraph().getNode(id));
			}

			// }
			// Get elements and create compound command
			break;

		default:
			err.write("Unknown command. Please type \"help\" to see a list of the available commands.\n");
			break;
		}
	}

	/**
	 * 
	 */
	private void updateLayerView(final String layerName) {
		// FIXME Breaks loose coupling BIG TIME!
//		IViewPart layerView = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().findView("de.uni_jena.iaa.linktype.atomic.views.layerview");
//		if (layerView instanceof LayerView) {
//			final LayerView lv = (LayerView) layerView;
//			combo = lv.getLayerCombo();
//			Display.getDefault().syncExec(new Runnable() {
//				@Override
//				public void run() {
//					for (int i = 0; i < combo.getItems().length; i++) {
//						if (combo.getItems()[i].equals(layerName)) {
//							combo.select(i);
//						}
//					}
//					for (TableItem item : lv.getLayerTableViewer().getTable().getItems()) {
//						String tmpLayerName;
//						if (layerName.equals("\u269B NO ACTIVE LEVEL \u269B")) {
//							tmpLayerName = "\u269B NO ASSIGNED LEVEL \u269B";
//						}
//						else {
//							tmpLayerName = layerName;
//						}
//						if (item.getData().equals(tmpLayerName)) {
//							item.setChecked(true);
//							lv.notifySelectionListeners();
//						}
//					}
//				}
//			});
//		}
	}

	private SNode getRelationTarget(HashMap<Object, Object> atomicALParameters) {
		SNode target = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(1); // FIXME:
																							// Add
																							// check
			key = key.toUpperCase();
			target = getGraph().getNode(key);
			return target;
		}
		return target;
	}

	private SNode getRelationSource(HashMap<Object, Object> atomicALParameters) {
		SNode source = null;
		if (atomicALParameters.get("all_nodes") != null) {
			String key = ((ArrayList<String>) atomicALParameters.get("all_nodes")).get(0);
			key = key.toUpperCase();
			source =  getGraph().getNode(key);
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
			
			@Override
			public void partActivated(IWorkbenchPart part) {
				activateAtomicalForEditorInput(part);
			}

			/**
			 * @param part
			 */
			private void activateAtomicalForEditorInput(IWorkbenchPart part) {
				
				if(part instanceof DocumentGraphEditor) {
					docGraphEditor = (DocumentGraphEditor) part;	
				} else {
					docGraphEditor = null;
				}
//				if (part instanceof GraphEditor) {
//					IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
//					EditPartViewer editPartViewer = ((GraphEditor) editor).getEditPartViewer();
//					console.setGraphPart((GraphPart) editPartViewer.getContents());
//					if (getGraphPart().getModel() == getGraph()) {
//						return;
//					}
//					console.setGraph(getGraphPart().getModel());
//					console.setEditor(editor);
//					String excerpt = null;
//					String text = getGraph().getSTextualDSs().get(0).getSText();
//					if (text.length() >= 50) {
//						excerpt = getGraph().getSTextualDSs().get(0).getSText().substring(0, 49) + "...";
//					}
//					else {
//						excerpt = text;
//					}
//					try {
//						console.out.write("Working on " + editor.getEditorInput().getName() + " (\"" + excerpt + "\").\n");
//					}
//					catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//				else if (part instanceof IEditorPart) {
//					try {
//						console.out.write("AtomicAL is not (yet) available for this editor type.\n");
//					}
//					catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
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
	public synchronized SDocumentGraph getGraph() {
		return docGraphEditor == null ? null : docGraphEditor.getGraph();
	}


	/**
	 * @return the editor
	 */
	public DocumentGraphEditor getEditor() {
		return docGraphEditor;
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
