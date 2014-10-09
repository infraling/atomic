/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorSashContainer;
import org.eclipse.ui.internal.EditorStack;
import org.eclipse.ui.internal.ILayoutContainer;
import org.eclipse.ui.internal.LayoutPart;
import org.eclipse.ui.internal.PartPane;
import org.eclipse.ui.internal.PartSashContainer;
import org.eclipse.ui.internal.PartSite;
import org.eclipse.ui.internal.PartStack;
import org.eclipse.ui.internal.WorkbenchPage;
import org.eclipse.ui.part.EditorPart;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDataSourceSequence;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STYPE_NAME;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.STextualDS;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SToken;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SAnnotation;
import de.uni_jena.iaa.linktype.atomic.core.registries.Registries;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.dnd.ReferenceViewDropListener;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceCellEditor;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceTreeContentProvider;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.ReferenceTreeLabelProvider;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.Reference;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel;
import de.uni_jena.iaa.linktype.atomic.editors.corefeditor.utils.RandomColorGenerator;

/**
 * @author Stephan Druskat
 * 
 */
public class ReferenceEditor extends EditorPart {

	public static final String ID = "de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceeditor";
	private ReferenceModel model;
	private SDocumentGraph graph;
	private CheckboxTreeViewer treeViewer;
	private boolean dirty;
	private SDocument document;
	private final IPartListener partListener = new IPartListener() {
		public void partActivated(IWorkbenchPart part) {
		}

		public void partBroughtToTop(IWorkbenchPart part) {
		}

		public void partClosed(IWorkbenchPart part) {
			if (part == ReferenceEditor.this) {
				IWorkbenchPage page = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage();
				IEditorReference[] editorRefs = page.getEditorReferences();
				for (int i = 0; i < editorRefs.length; i++) {
					if (editorRefs[i]
							.getId()
							.equals("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.editor")) {
						part = editorRefs[i].getPart(false);
						page.closeEditor((IEditorPart) part, false);
					}
				}
				getSite().getWorkbenchWindow().getPartService()
						.removePartListener(this);
			}
		}

		public void partDeactivated(IWorkbenchPart part) {
		}

		public void partOpened(IWorkbenchPart part) {
		}
	};

	/**
	 * 
	 */
	public ReferenceEditor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout());
		// SashForm sashForm = new SashForm(parent, SWT.NONE);
		// Composite viewComposite = new Composite(sashForm, SWT.NONE);
		// viewComposite.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		// createViewArea(viewComposite);
		Composite treeComposite = new Composite(parent, SWT.NONE);
		model = (ReferenceModel) getEditorInput();
		document = model.getDocument();
		createContentTree(treeComposite);
		// sashForm.setWeights(new int[] {1, 1});
		treeViewer.setInput(model);
		this.getSite().getWorkbenchWindow().getPartService()
				.addPartListener(this.partListener);
	}

	private void createContentTree(Composite treeComposite) {
		treeComposite.setLayout(new FillLayout());
		treeViewer = new CheckboxTreeViewer(treeComposite, SWT.BORDER);
		treeViewer.setContentProvider(new ReferenceTreeContentProvider(model));
		treeViewer.setLabelProvider(new ReferenceTreeLabelProvider());
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { TextTransfer.getInstance() };
		treeViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		treeViewer.addDropSupport(operations, transferTypes,
				new ReferenceViewDropListener(treeViewer, this));
		treeViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (event.getChecked()) {
					treeViewer.setSubtreeChecked(event.getElement(), true);
					colorElement(event.getElement());
				} else {
					removeColor(event.getElement());
					treeViewer.setSubtreeChecked(event.getElement(), false);
				}
			}

			private void removeColor(Object object) {
				SourceViewer viewer = null;
				StyledText widget = null;
				IEditorReference[] editorRefs = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getEditorReferences();
				for (int i = 0; i < editorRefs.length; i++) {
					if (editorRefs[i]
							.getId()
							.equals("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.editor")) {
						IEditorPart part = (IEditorPart) editorRefs[i]
								.getPart(false);
						viewer = ((CoreferenceEditor) part).getViewer();
						widget = viewer.getTextWidget();
					}
				}
				if (object instanceof Reference) {
					
					Registries registries = Registries.getInstance();
					RandomColorGenerator generator = new RandomColorGenerator();
					List<RGB> colors = null;
					try {
						colors = generator.createRandomizedRgbList(
								1, 0.99f,
								0.99f, RandomColorGenerator.GOLDEN_RATIO);
							registries.putColor("1",
									colors.get(0));
						
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (int j = 0; j < ((Reference) object).getSpans().size(); j++) {
						SSpan span = ((Reference) object).getSpans().get(j);
						Color color = registries.getColor("1",
								registries.toHex(colors.get(0)));
						EList<STYPE_NAME> refList = new BasicEList<STYPE_NAME>();
						refList.add(STYPE_NAME.SSPANNING_RELATION);
						EList<SToken> tokens = graph.getOverlappedSTokens(span,
								refList);
						refList.clear();
						refList.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
						for (SToken token : tokens) {
							SDataSourceSequence sequence = graph
									.getOverlappedDSSequences(token, refList)
									.get(0);
							TextPresentation style = new TextPresentation();
							style.addStyleRange(new StyleRange(sequence
									.getSStart(), sequence.getSEnd()
									- sequence.getSStart(), null, null));
							((SourceViewer) viewer).changeTextPresentation(
									style, true);
						}
					}
				}

			}

			private void colorElement(Object object) {
				SourceViewer viewer = null;
				IEditorReference[] editorRefs = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.getEditorReferences();
				for (int i = 0; i < editorRefs.length; i++) {
					if (editorRefs[i]
							.getId()
							.equals("de.uni_jena.iaa.linktype.atomic.editors.corefeditor.editor")) {
						IEditorPart part = (IEditorPart) editorRefs[i]
								.getPart(false);
						viewer = ((CoreferenceEditor) part).getViewer();
					}
				}
				if (object instanceof Reference) {
					Registries registries = Registries.getInstance();
					RandomColorGenerator generator = new RandomColorGenerator();
					List<RGB> colors = null;
					try {
						colors = generator.createRandomizedRgbList(
								1, 0.99f,
								0.99f, RandomColorGenerator.GOLDEN_RATIO);
							registries.putColor("1",
									colors.get(0));
						
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					for (int j = 0; j < ((Reference) object).getSpans().size(); j++) {
						SSpan span = ((Reference) object).getSpans().get(j);
						Color color = registries.getColor("1",
								registries.toHex(colors.get(0)));
						EList<STYPE_NAME> refList = new BasicEList<STYPE_NAME>();
						refList.add(STYPE_NAME.SSPANNING_RELATION);
						EList<SToken> tokens = graph.getOverlappedSTokens(span,
								refList);
						refList.clear();
						refList.add(STYPE_NAME.STEXT_OVERLAPPING_RELATION);
						for (SToken token : tokens) {
							SDataSourceSequence sequence = graph
									.getOverlappedDSSequences(token, refList)
									.get(0);
							TextPresentation style = new TextPresentation();
							style.addStyleRange(new StyleRange(sequence
									.getSStart(), sequence.getSEnd()
									- sequence.getSStart(), color, null));
							((SourceViewer) viewer).changeTextPresentation(
									style, true);
						}
					}
				}
			}
		});
		addEditingSupport(treeViewer);
	}

	// private void createViewArea(Composite parent) {
	// parent.setLayout(new FillLayout());
	// Composite viewPanel = new Composite(parent, SWT.NONE);
	// viewPanel.setLayout(new FillLayout());
	// }
	//
	//
	private void addEditingSupport(final CheckboxTreeViewer treeViewer) {
		final TreeViewerColumn column = new TreeViewerColumn(treeViewer,
				SWT.NONE);
		column.setLabelProvider(new ReferenceTreeLabelProvider());
		final ReferenceCellEditor cellEditor = new ReferenceCellEditor(
				treeViewer.getTree());

		column.setEditingSupport(new EditingSupport(treeViewer) {

			@Override
			protected void setValue(Object element, Object value) {
				if (element instanceof Reference) {
					Reference data = (Reference) element;
					data.setName(value.toString());
				} else if (element instanceof SAnnotation) {
					((SAnnotation) element).setValue(value);
				}
				treeViewer.update(element, null);
				setDirty(true);
				firePropertyChange(PROP_DIRTY);
			}

			@Override
			protected Object getValue(Object element) {
				if (element instanceof Reference) {
					return ((Reference) element).getName();
				} else if (element instanceof SSpan) {
					EList<STYPE_NAME> reList = new BasicEList<STYPE_NAME>();
					reList.add(STYPE_NAME.SSPANNING_RELATION);
					EList<SToken> tokens = ((SSpan) element)
							.getSDocumentGraph().getOverlappedSTokens(
									((SSpan) element), reList);
					reList.clear();
					reList.add(STYPE_NAME.STEXTUAL_RELATION);
					StringBuilder text = new StringBuilder();
					for (SToken token : tokens) {
						STextualDS ds = token.getSDocumentGraph()
								.getSTextualDSs().get(0);
						SDataSourceSequence sequence = token
								.getSDocumentGraph()
								.getOverlappedDSSequences(token, reList).get(0);
						text.append(ds.getSText().substring(
								sequence.getSStart(), sequence.getSEnd())
								+ " ");
					}
					return text.toString();
				} else if (element instanceof SAnnotation) {
					return ((SAnnotation) element).getValueString();
				}
				return element;
			}

			@Override
			protected TextCellEditor getCellEditor(Object element) {
				return cellEditor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof Reference
						|| element instanceof SAnnotation;
			}
		});

		// To set width of the column to tree width
		treeViewer.getControl().addControlListener(new ControlListener() {

			@Override
			public void controlResized(ControlEvent e) {
				column.getColumn().setWidth(
						((Tree) e.getSource()).getBounds().width);
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		document.setSDocumentGraph(graph);
		document.saveSDocumentGraph(model.getGraphURI());
		setDirty(false);
		firePropertyChange(PROP_DIRTY);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite,
	 * org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if (!(input instanceof ReferenceModel)) {
			throw new RuntimeException(
					"Wrong input! Expecting de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model.ReferenceModel.\n"
							+ "But input is " + input.getClass());
		}
		ReferenceModel model = (ReferenceModel) input;
		this.model = model;
		setSite(site);
		setInput(model);
		graph = model.getDecoratedSDocumentGraph();
		setPartName("Referents");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
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

	/**
	 * @param dirty
	 *            the dirty to set
	 */
	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public void fireDirtyProperty() {
		firePropertyChange(PROP_DIRTY);
	}

}
