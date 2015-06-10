/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.views.layerview;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.ViewPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SLayer;
import de.uni_jena.iaa.linktype.atomic.core.model.ModelRegistry;
import de.uni_jena.iaa.linktype.atomic.views.layerview.util.NewLayer;

/**
 * @author Stephan Druskat
 * 
 */
public class LayerView extends ViewPart implements ISelectionProvider, IPartListener2 {

	private Combo layerCombo;
	private CheckboxTableViewer layerTableViewer;
	private ListenerList listeners = new ListenerList();
	private SDocumentGraph graph;
	private IWorkbenchPartReference oldPartRef;
	private Map<IWorkbenchPartReference, String> lastActiveLayerMap = new HashMap<IWorkbenchPartReference, String>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		getSite().setSelectionProvider(this);
		final IWorkbenchWindow workbenchWindow = getSite().getWorkbenchWindow();
		workbenchWindow.getPartService().addPartListener(this);
		parent.setLayout(new GridLayout(1, true));

		createButtons(parent);

		addSelectionButtons(parent);

		// ///////////
		setLayerTableViewer(CheckboxTableViewer.newCheckList(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL));
		getLayerTableViewer().getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		getLayerTableViewer().setContentProvider(new LayerContentProvider());
		getLayerTableViewer().setLabelProvider(new LayerLabelProvider(this));
		getLayerTableViewer().setInput(getInput());

		TableColumn column = new TableColumn(getLayerTableViewer().getTable(), SWT.FILL);
		column.setText("Levels");
		column.pack();

		getLayerTableViewer().addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				// System.err.println("CHECKSTATECHANGED");
				// getLinkSourceSentences().clear();
				// getLinkedSentencesForSentence().clear();
				// getLinkedSentences().clear();
				// for (Object checkedElement :
				// getLayerTableViewer().getCheckedElements()) {
				// HashSet<SSpan> linkedSentencesForCurrentElement =
				// GraphService.getLinkedSentences((SSpan) checkedElement);
				// if (!linkedSentencesForCurrentElement.isEmpty()) {
				// getLinkSourceSentences().add((SSpan) checkedElement);
				// }
				// for (SSpan span : linkedSentencesForCurrentElement) {
				// getLinkedSentencesForSentence().put(span, (SSpan)
				// checkedElement);
				// }
				// getLinkedSentences().addAll(linkedSentencesForCurrentElement);
				// }
				getLayerTableViewer().refresh();
				if (getLayerTableViewer().getCheckedElements().length == 0) {
					notifySelectionListeners(ModelRegistry.NO_LAYERS_SELECTED);
				}
				else {
					notifySelectionListeners();
				}
			}
		});

		getLayerTableViewer().getTable().setHeaderVisible(true);
		getLayerTableViewer().getTable().setLinesVisible(true);

		// ///////////
	}

	/**
	 * @param parent
	 */
	private void createButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));

		createLayerCombo(buttonComposite);

		Button button = new Button(buttonComposite, SWT.PUSH);
		button.setText("[+] Add new level");
		// button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		button.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String layerName = null;
				SLayer layer = null;
				if (getGraph() != null) {
					layer = SaltFactory.eINSTANCE.createSLayer();
					InputDialog createLayerDialog = new InputDialog(Display.getCurrent().getActiveShell(), "Create new level", "Please enter a name for the new level.", "", null);
					if (createLayerDialog.open() == Window.OK) {
						layerName = createLayerDialog.getValue();
						layer.setSName(createLayerDialog.getValue());
					}
					getGraph().addSLayer(layer);
				}
				getLayerTableViewer().refresh();
				getLayerCombo().add(layerName, (getLayerCombo().getItemCount() - 1));
				for (int i = 0; i < listeners.getListeners().length; i++) {
					((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(new NewLayer(layer))));
				}
			}
		});

	}

	private void addSelectionButtons(Composite parent) {
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayout(new GridLayout(2, false));
		buttonComposite.setData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		Button selectButton = createButton(buttonComposite, "&Select all", GridData.HORIZONTAL_ALIGN_FILL);
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getLayerTableViewer().setAllChecked(true);
				getLayerTableViewer().refresh();
				notifySelectionListeners();
			}
		};
		selectButton.addSelectionListener(listener);

		Button deselectButton = createButton(buttonComposite, "&Deselect all", GridData.HORIZONTAL_ALIGN_FILL);
		listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getLayerTableViewer().setAllChecked(false);
				notifySelectionListeners(ModelRegistry.NO_LAYERS_SELECTED);
				getLayerTableViewer().refresh();
			}
		};
		deselectButton.addSelectionListener(listener);
	}

	/**
	 * @param buttonComposite
	 * @param string
	 * @param horizontalAlignFill
	 * @return
	 */
	private Button createButton(Composite parent, String label, int style) {
		Button button = new Button(parent, SWT.PUSH);
		button.setText(label);
		GridData data = new GridData(style);
		button.setLayoutData(data);
		return button;
	}

	/**
	 * @param noLayersSelected
	 */
	protected void notifySelectionListeners(String emptySelectionType) {
		for (int i = 0; i < listeners.getListeners().length; i++) {
			((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(emptySelectionType)));
		}
	}

	/**
	 * 
	 */
	protected void notifySelectionListeners() {
		for (int i = 0; i < listeners.getListeners().length; i++) {
			((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(getLayerTableViewer().getCheckedElements())));
		}
	}

	/**
	 * @param parent
	 */
	private void createLayerCombo(Composite parent) {
		setLayerCombo(new Combo(parent, SWT.NONE | SWT.READ_ONLY));
		getLayerCombo().setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		addLayersToCombo(getLayerCombo());
		getLayerCombo().add("-- Set active level --", 0);
		getLayerCombo().select(0);
		SelectionListener listener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.equals(getLayerCombo())) {
					if (getGraph() != null) {
						EList<SLayer> layerToActivate = getGraph().getSLayerByName(getLayerCombo().getText());
						if (getLayerCombo().getText().equals("\u269B NO ACTIVE LAYER \u269B")) {
							System.err.println("NO ACTIVE LAYER selected");
							for (int i = 0; i < listeners.getListeners().length; i++) {
								((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(new NewLayer(null))));
							}
						}
						else {
							for (int i = 0; i < listeners.getListeners().length; i++) {
								((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(new NewLayer(layerToActivate.get(0)))));
							}
						}
					}
					System.err.println("NEW SELECTION " + getLayerCombo().getText());
					lastActiveLayerMap.put(oldPartRef, getLayerCombo().getText());
					
				}
			}
		};
		getLayerCombo().addSelectionListener(listener);
	}

	/**
	 * @param layerCombo
	 */
	private void addLayersToCombo(Combo layerCombo) {
		SDocumentGraph graph = getInput();
		for (SLayer layer : graph.getSLayers()) {
			layerCombo.add(layer.getSName());
		}
		layerCombo.add("\u269B NO ACTIVE LAYER \u269B");
		getLayerCombo().add("-- Set active level --", 0);
		getLayerCombo().select(0);
	}

	/**
	 * @return
	 */
	private SDocumentGraph getInput() {
		if (getGraph() == null) {
			IEditorInput editorInput = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getEditorInput();
			if (editorInput instanceof FileEditorInput) {
				IFile file = ((FileEditorInput) editorInput).getFile();
				System.err.println("LAYER VIEW calling ModelRegistry.getModel()");
				setGraph(ModelRegistry.getModel(file));
			}
		}
		return getGraph();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partActivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partActivated(IWorkbenchPartReference partRef) {
		if (partRef.getPart(false) instanceof EditorPart) {
			if (oldPartRef == null) {
				// Initiate view, as editor has been opened for the first time
				oldPartRef = partRef;
				initializeView();
			}
			else if (partRef == oldPartRef) {
				// Do nothing, as the editor hasn't changed.
			}
			else {
				// Re-initiate view, as another editor has been opened/activated
				lastActiveLayerMap.put(oldPartRef, getLayerCombo().getText());
				oldPartRef = partRef;
				initializeView();
				if (lastActiveLayerMap.get(partRef) != null) {
					for (int itemCount = 0; itemCount < getLayerCombo().getItemCount(); itemCount++) {
						if (getLayerCombo().getItem(itemCount).equals(lastActiveLayerMap.get(partRef))) {
							getLayerCombo().select(itemCount);
							System.err.println(">>>>>>>>>>>>>>>>>>>>> ");
							if (!getLayerCombo().getText().equals("\u269B NO ACTIVE LAYER \u269B")) {
							for (int i = 0; i < listeners.getListeners().length; i++) {
								System.err.println(getLayerCombo().getItem(itemCount));
								((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(new NewLayer(getGraph().getSLayerByName(getLayerCombo().getItem(itemCount)).get(0)))));
							}}
							else {
								for (int i = 0; i < listeners.getListeners().length; i++) {
									System.err.println(getLayerCombo().getItem(itemCount));
									((ISelectionChangedListener) listeners.getListeners()[i]).selectionChanged(new SelectionChangedEvent(LayerView.this, new StructuredSelection(new NewLayer(null))));
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 
	 */
	private void initializeView() {
		setGraph(null);
		// EditorPart editor = (EditorPart) partRef.getPart(false);
		// if (editor.getEditorInput() instanceof FileEditorInput) {
		// FileEditorInput input = (FileEditorInput)
		// editor.getEditorInput();
		// if
		// (input.getFile().getName().endsWith(SaltFactory.FILE_ENDING_SALT)
		// &&
		// !input.getFile().getName().equals(SaltFactory.FILE_SALT_PROJECT))
		// {
		// if (getLayerTableViewer() != null &&
		// !getLayerTableViewer().getControl().isDisposed()) {
		// getLayerTableViewer().setInput(getInput());
		// getLayerTableViewer().refresh();
		// }
		// }
		// }
		if (getLayerTableViewer() != null && !getLayerTableViewer().getControl().isDisposed()) {
			getLayerTableViewer().setInput(getInput());
			getLayerTableViewer().refresh();
			getLayerCombo().removeAll();
			addLayersToCombo(getLayerCombo());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partBroughtToTop(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partBroughtToTop(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partClosed(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partDeactivated(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partDeactivated(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partOpened(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partOpened(IWorkbenchPartReference partRef) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partHidden(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partHidden(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partVisible(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IPartListener2#partInputChanged(org.eclipse.ui.
	 * IWorkbenchPartReference)
	 */
	@Override
	public void partInputChanged(IWorkbenchPartReference partRef) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#addSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ISelectionProvider#getSelection()
	 */
	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#removeSelectionChangedListener
	 * (org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionProvider#setSelection(org.eclipse
	 * .jface.viewers.ISelection)
	 */
	@Override
	public void setSelection(ISelection selection) {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the layerCombo
	 */
	public Combo getLayerCombo() {
		return layerCombo;
	}

	/**
	 * @param layerCombo
	 *            the layerCombo to set
	 */
	public void setLayerCombo(Combo layerCombo) {
		this.layerCombo = layerCombo;
	}

	/**
	 * @return the layerTableViewer
	 */
	public CheckboxTableViewer getLayerTableViewer() {
		return layerTableViewer;
	}

	/**
	 * @param layerTableViewer
	 *            the layerTableViewer to set
	 */
	public void setLayerTableViewer(CheckboxTableViewer layerTableViewer) {
		this.layerTableViewer = layerTableViewer;
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

}
