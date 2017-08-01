/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.editor;

import java.io.IOException; 
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.atomic.exceptions.AtomicGeneralException;
import org.corpus_tools.atomic.tagset.Tagset;
import org.corpus_tools.atomic.tagset.TagsetValue;
import org.corpus_tools.atomic.tagset.impl.TagsetFactory;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SaltProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ReflectiveColumnPropertyAccessor;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommand;
import org.eclipse.nebula.widgets.nattable.edit.command.UpdateDataCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.CellVisualChangeEvent;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.selection.command.SelectCellCommand;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetEditor extends EditorPart {
	


	private IDataProvider bodyDataProvider;
    private String[] propertyNames;
    private BodyLayerStack bodyLayer;
    private Map<String, String> propertyToLabels;
	
	private static final Logger log = LogManager.getLogger(TagsetEditor.class);
	/**
	 * TODO
	 */
	public static final String ID = "org.corpus_tools.atomic.TagsetEditor"; 
	
	private SCorpus corpus;
	private Tagset tagset;
	private URI tagsetFileURI;
	private boolean dirty;
	private NatTable natTable;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSave(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		tagset.save(tagsetFileURI);
		setDirty(false);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#doSaveAs()
	 */
	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
	 */
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
		
		if (!(input instanceof FileEditorInput)) {
			log.error("Input for editors of type {} must be of type {}, but the provided input is of type {}!", this.getClass().getName(), FileEditorInput.class.getName(), input.getClass().getName(), new AtomicGeneralException());
		}
		else {
			String filePath = ((FileEditorInput) input).getPath().makeAbsolute().toOSString();
			log.trace("Input for editor {} is file '{}'.", this.getClass().getName(), filePath);
			tagsetFileURI = URI.createFileURI(filePath);
			IProject iProject = ((FileEditorInput) input).getFile().getProject();
			
			// Get corpus for tagset via SaltProject
			IFile saltProjectResource = null;
			try {
				for (IResource member : iProject.members()) {
					if (member instanceof IFile && member.getName().equalsIgnoreCase(DocumentGraphEditor.SALT_PROJECT_FILE_NAME)) { // FIXME Externalize and treat for case, etc
						saltProjectResource = (IFile) member;
					}
				}
			}
			catch (CoreException e) {
				log.error("An error occurred getting the Salt project file.", e);
			}
			String projectFilePath = saltProjectResource.getRawLocation().toOSString();
			SaltProject saltProject = SaltFactory.createSaltProject();
			saltProject.loadCorpusStructure(URI.createFileURI(projectFilePath));
			Assert.isTrue(saltProject.getCorpusGraphs().size() == 1, "Atomic cannot currently work with Salt projects that contain more than one corpus graph.");
			Assert.isTrue(saltProject.getCorpusGraphs().get(0).getCorpora().size() == 1, "Atomic cannot currently work with Salt projects containins more than one corpus.");
			corpus = saltProject.getCorpusGraphs().get(0).getCorpora().get(0);
			log.trace("Corpus for tagset has been determined as {}.", corpus);
			
			// Load tagset
			long tagsetFileSize = 0;
			try {
				tagsetFileSize = Files.size(Paths.get(filePath));
				log.trace("Tagset file {} has size {}.", filePath, String.valueOf(tagsetFileSize));
			}
			catch (IOException e) {
				MessageDialog.openError(Display.getCurrent().getActiveShell(), "File error", "Failed to load tagset file " + filePath + " in order to calculate its size.");
				log.warn("Failed to load tagset file {} in order to calculate its size.", filePath, e);
				closeEditor(filePath);
				return;
			}
			if (tagsetFileSize == 0) {
				tagset = TagsetFactory.createTagset(corpus.getIdentifier().getId(), corpus.getName());
			}
			else {
				tagset = TagsetFactory.load(tagsetFileURI);
			}
			if (tagset == null) {
				log.error("Could not read tagset from tagset file {}.", filePath);
				closeEditor(filePath);
				return;
			}
			log.info("Loaded tagset {} ({}) from {}.", tagset, tagset.getName(), filePath);
			setPartName("Tagset \"" + tagset.getName() + "\"");
			// Set up editor for automatic context switches on activation/deactivation 
//			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().addPartListener(new PartContextListener(site.getId(), site.getPluginId()));
		}


	}

	private void closeEditor(String filePath) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				getSite().getPage().closeEditor(TagsetEditor.this, false); 
			}
		});
		log.info("Attempt to open tagset editor for tagset file {} aborted.", filePath);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isDirty()
	 */
	@Override
	public boolean isDirty() {
		return dirty;
	}
	
	/**
	 * Sets {@link #dirty} and fires a property change.
	 * 
	 * @param dirty the dirty to set
	 */
	public final void setDirty(boolean dirty) {
		this.dirty = dirty;
		firePropertyChange(PROP_DIRTY);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.EditorPart#isSaveAsAllowed()
	 */
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout(1, false));
		
		Composite buttonContainer = new Composite(parent, SWT.NONE);
		buttonContainer.setLayout(new GridLayout(2, false));
		
		Button btnNewButton = new Button(buttonContainer, SWT.NONE);
		btnNewButton.setText("&New entry");
		
		Button btnRemoveButton = new Button(buttonContainer, SWT.NONE);
		btnRemoveButton.setText("&Remove entry");
		
		/* ############################################
		 * Grid
		 * ############################################
		 */
		this.bodyDataProvider = setupBodyDataProvider();
		DefaultColumnHeaderDataProvider colHeaderDataProvider = new DefaultColumnHeaderDataProvider(this.propertyNames,
				this.propertyToLabels);
		DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(this.bodyDataProvider);

		this.bodyLayer = new BodyLayerStack(this.bodyDataProvider);
		final ColumnOverrideLabelAccumulator columnLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyLayer.getBodyDataLayer());
        bodyLayer.getBodyDataLayer().setConfigLabelAccumulator(columnLabelAccumulator);
		ColumnHeaderLayerStack columnHeaderLayer = new ColumnHeaderLayerStack(colHeaderDataProvider);
		RowHeaderLayerStack rowHeaderLayer = new RowHeaderLayerStack(rowHeaderDataProvider);
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(colHeaderDataProvider,
				rowHeaderDataProvider);
		CornerLayer cornerLayer = new CornerLayer(new DataLayer(cornerDataProvider), rowHeaderLayer, columnHeaderLayer);

		GridLayer gridLayer = new GridLayer(this.bodyLayer, columnHeaderLayer, rowHeaderLayer, cornerLayer);
		
		natTable = new NatTable(parent, SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE | SWT.DOUBLE_BUFFERED
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER, gridLayer, false);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(natTable);

		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new TagsetEditorConfiguration(this, columnLabelAccumulator));
		natTable.configure();

		/* 
		 * Necessary to select the first cell, activated for 
		 * keyboard navigation!
		 * Cf. https://www.eclipse.org/forums/index.php?t=msg&th=1083775&goto=1752061&#msg_1752061
		 */
		natTable.addPaintListener(new PaintListener() {
		    @Override
		    public void paintControl(PaintEvent e) {
		        natTable.setFocus();
		        natTable.doCommand(new SelectCellCommand(bodyLayer.getSelectionLayer(), 0, 0, false, false));
		        natTable.removePaintListener(this);
		    }
		});
		
		btnRemoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.err.println(bodyLayer.getSelectionLayer().getSelectedRowPositions());
			}
		});

		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Set<Range> selectedRows = bodyLayer.getSelectionLayer().getSelectedRowPositions();
				if (!selectedRows.isEmpty()) {
					List<Range> rangesList = Arrays.asList(selectedRows.toArray(new Range[(selectedRows.size())]));
					sortRangesByEnd(rangesList);
					Range lastRange = rangesList.get(rangesList.size() - 1);
					int lastRowIndex = lastRange.end - 1;
					TagsetValue lastEntry = tagset.getValues().get(lastRowIndex);
					tagset.addValue(lastRowIndex + 1,
							TagsetFactory.createTagsetValue(lastEntry.getLayer(), lastEntry.getElementType(),
									lastEntry.getNamespace(), lastEntry.getName(), null, false, null));
					natTable.refresh();
					natTable.setFocus();
			        natTable.doCommand(new SelectCellCommand(bodyLayer.getSelectionLayer(), 4, lastRowIndex + 1, false, false));
				}
				else {
					if (tagset.getValues().isEmpty()) {
						tagset.addValue(TagsetFactory.createTagsetValue(null, null, null, null, null, false, null));
						natTable.refresh();
						natTable.setFocus();
						natTable.doCommand(new SelectCellCommand(bodyLayer.getSelectionLayer(), 0, 0, false, false));
					}
					else {
						TagsetValue lastValue = tagset.getValues().get(tagset.getValues().size() - 1);
						tagset.addValue(TagsetFactory.createTagsetValue(lastValue.getLayer(), lastValue.getElementType(), lastValue.getNamespace(), lastValue.getName(), null, false, null));
						natTable.refresh();
						natTable.setFocus();
						natTable.doCommand(new SelectCellCommand(bodyLayer.getSelectionLayer(), 4, tagset.getValues().size() - 1, false, false));
					}
				}
			}

			private void sortRangesByEnd(List<Range> ranges) {
				Collections.sort(ranges, new Comparator<Range>() {
		            @Override
		            public int compare(Range range1, Range range2) {
		                return Integer.valueOf(range1.end).compareTo(
		                        Integer.valueOf(range2.end));
		            }
		        });
			}
		});
		
	}

	private IDataProvider setupBodyDataProvider() {
		this.propertyToLabels = new HashMap<>();
        this.propertyToLabels.put("layer", "Layer");
        this.propertyToLabels.put("elementType", "Element type");
        this.propertyToLabels.put("namespace", "Annotation namespace");
        this.propertyToLabels.put("name", "Annotation name");
        this.propertyToLabels.put("value", "Annotation value");
        this.propertyToLabels.put("description", "Description");
        this.propertyNames = new String[] {"layer","elementType","namespace","name","value","description"};
        return new ListDataProvider<>(tagset.getValues(), new ReflectiveColumnPropertyAccessor<TagsetValue>(this.propertyNames));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		if (natTable != null) {
			natTable.setFocus();
		}

	}
	
	
	class BodyLayerStack extends AbstractLayerTransform {

		private SelectionLayer selectionLayer;
		private DataLayer bodyDataLayer;

		public BodyLayerStack(IDataProvider dataProvider) {
			this.bodyDataLayer = new DataLayer(dataProvider);
			bodyDataLayer.setColumnPercentageSizing(true);
			bodyDataLayer.setColumnWidthPercentageByPosition(0, 16);
			bodyDataLayer.setColumnWidthPercentageByPosition(1, 16);
			bodyDataLayer.setColumnWidthPercentageByPosition(2, 16);
			bodyDataLayer.setColumnWidthPercentageByPosition(3, 16);
			bodyDataLayer.setColumnWidthPercentageByPosition(4, 16);
			bodyDataLayer.setColumnWidthPercentageByPosition(5, 16);
			
			// CustomUpdateHandler
			this.bodyDataLayer.unregisterCommandHandler(UpdateDataCommand.class);
            this.bodyDataLayer.registerCommandHandler(new TagsetUpdateDataCommandHandler(bodyDataLayer));
			
			ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(bodyDataLayer);
			ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(columnReorderLayer);
			this.selectionLayer = new SelectionLayer(columnHideShowLayer);
			ViewportLayer viewportLayer = new ViewportLayer(this.selectionLayer);
			setUnderlyingLayer(viewportLayer);
		}

		public SelectionLayer getSelectionLayer() {
			return this.selectionLayer;
		}

		public final DataLayer getBodyDataLayer() {
			return bodyDataLayer;
		}

	}

	private class ColumnHeaderLayerStack extends AbstractLayerTransform {

		public ColumnHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider);
			ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer, TagsetEditor.this.bodyLayer,
					TagsetEditor.this.bodyLayer.getSelectionLayer());
			setUnderlyingLayer(colHeaderLayer);
		}
	}

	private class RowHeaderLayerStack extends AbstractLayerTransform {

		public RowHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
			RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer, TagsetEditor.this.bodyLayer,
					TagsetEditor.this.bodyLayer.getSelectionLayer());
			setUnderlyingLayer(rowHeaderLayer);
		}
	}

	/**
	 * @return the tagset
	 */
	public final Tagset getTagset() {
		return tagset;
	}

	/**
	 * @return the bodyLayer
	 */
	public final BodyLayerStack getBodyLayer() {
		return bodyLayer;
	}

	/**
	 * // TODO Add description
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 * 
	 */
	public class TagsetUpdateDataCommandHandler extends UpdateDataCommandHandler {

		private final Logger log = LogManager.getLogger(TagsetUpdateDataCommandHandler.class);

		private DataLayer dataLayer;

		public TagsetUpdateDataCommandHandler(DataLayer dataLayer) {
			super(dataLayer);
			this.dataLayer = dataLayer;
		}

		@Override
		protected boolean doCommand(UpdateDataCommand command) {
			try {
				int columnPosition = command.getColumnPosition();
				int rowPosition = command.getRowPosition();

				Object currentValue = this.dataLayer.getDataValueByPosition(columnPosition, rowPosition);
				if (currentValue == null || command.getNewValue() == null
						|| !currentValue.equals(command.getNewValue())) {
					this.dataLayer.setDataValueByPosition(columnPosition, rowPosition, command.getNewValue());
					this.dataLayer
							.fireLayerEvent(new CellVisualChangeEvent(this.dataLayer, columnPosition, rowPosition));

					if (command.getColumnPosition() == 4) {
						Object newValue = command.getNewValue();
						if (newValue instanceof String) {
							if (((String) newValue).startsWith("/") && ((String) newValue).endsWith("/")) {
								boolean isRegex = true;
								try {
									String pattern = ((String) newValue).substring(1, ((String) newValue).length() - 1);
									Pattern.compile(pattern);
								}
								catch (PatternSyntaxException exception) {
									isRegex = false;
									MessageDialog.openWarning(Display.getCurrent().getActiveShell(), "Invalid regular expression",
											"The value you have entered is not a valid regular expression!\n\nError: "
													+ exception.getMessage());
								}
								tagset.getValues().get(command.getRowPosition()).setRegularExpression(isRegex);
							}
						}
					}
				}
				return true;
			}
			catch (Exception e) {
				log.error("Failed to update value to: " + command.getNewValue(), e); //$NON-NLS-1$
				return false;
			}
		}
	}

}
