package org.corpus_tools.search.parts;

import java.net.URI;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.api.editors.SaltNodeSelectable;
import org.corpus_tools.search.service.SearchService;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.di.UISynchronize;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import com.google.common.base.Splitter;

import annis.exceptions.AnnisQLSemanticsException;
import annis.exceptions.AnnisQLSyntaxException;
import annis.service.objects.Match;
import annis.service.objects.MatchGroup;

public class ANNISSearch {

	private static final Logger log = LogManager.getLogger(ANNISSearch.class);
	
	@Inject
	private SearchService search;

	@Inject
	private IWorkbenchPage page;
	
	
	private Button btReindex;
	
	
	private final static Splitter pathSplitter = Splitter.on('/').omitEmptyStrings().trimResults();
	
	@Inject
	private UISynchronize uiSync;
	private Table table;

	private Label lblStatus;

	@PostConstruct
	public void createPartControl(Composite parent, IEclipseContext context) {
		GridLayout gl_parent = new GridLayout(3, false);
		parent.setLayout(gl_parent);
		
		lblStatus = new Label(parent, SWT.NONE);
		lblStatus.setAlignment(SWT.RIGHT);
		lblStatus.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		new Label(parent, SWT.NONE);

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true, 1, 1));
		RowLayout rl_composite = new RowLayout(SWT.VERTICAL);
		composite.setLayout(rl_composite);

		btReindex = new Button(composite, SWT.PUSH);
		btReindex.setText("Re-index corpus");

		final Text txtQuery = new Text(composite, SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		txtQuery.setFont(JFaceResources.getTextFont());
		txtQuery.setLayoutData(new RowData(204, 143));
		txtQuery.addKeyListener(new KeyListener() {

			@Override
			public void keyReleased(KeyEvent e) {
			
			}

			@Override
			public void keyPressed(KeyEvent e) {
				
				if (e.keyCode == SWT.CR && (e.stateMask & SWT.MOD1) != 0) {
					executeSearch(parent, txtQuery.getText());
					e.doit = false;
				}

			}
		});

		final Button btExecute = new Button(composite, SWT.PUSH);
		btExecute.setText("Execute Query");

		Composite composite_1 = new Composite(parent, SWT.NONE);
		composite_1.setLayout(new FillLayout(SWT.HORIZONTAL));
		GridData gd_composite_1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_composite_1.heightHint = 107;
		gd_composite_1.widthHint = 109;
		composite_1.setLayoutData(gd_composite_1);
		
		table = new Table(composite_1, SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		Menu menu = new Menu(table);
		table.setMenu(menu);
		
		MenuItem mntmOpenWithDefault = new MenuItem(menu, SWT.NONE);
		mntmOpenWithDefault.setText("Open with default editor");
		new Label(parent, SWT.NONE);
		mntmOpenWithDefault.addSelectionListener(new OpenMenuListener(null));
		
		// find each available editors and add an entry for the editor
		IEditorDescriptor[] editors = PlatformUI.getWorkbench().getEditorRegistry().getEditors("anything.salt");
		for(int i=0; i < editors.length; i++) {
			MenuItem mntOpenWithEditor = new MenuItem(menu, SWT.NONE);
			mntOpenWithEditor.setText("Open with " + editors[i].getLabel());
			mntOpenWithEditor.addSelectionListener(new OpenMenuListener(editors[i]));
		}
		btExecute.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				executeSearch(parent, txtQuery.getText());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			
			}
		});
		btReindex.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				search.reindexAllDocuments(true);

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
	}
	
	
	
	private IFile searchDocumentInResource(IResource res, String documentName) throws CoreException {
		
		if(res instanceof IFile) {
			IFile file = (IFile) res;
			if((documentName + ".salt").equals(file.getName())) {
				return file;
			} 
		} else if (res instanceof IContainer ){
			for (IResource child : ((IContainer) res).members()) {
				IFile foundChild = searchDocumentInResource(child, documentName);
				if(foundChild != null) {
					return foundChild;
				}
			}
		}
		// if nothing was found return null
		return null;
	}
	
	private void executeSearch(Composite parent, String aql) {
		
		lblStatus.setText("Searching...");
		table.removeAll();
		
		for(int c=table.getColumnCount()-1; c >= 0; c--) {
			table.getColumn(c).dispose();
		}
		
		Job j = new Job("Searching in corpus") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				try {
					MatchGroup result = search.find(aql);
					
					uiSync.asyncExec(() -> {
						// find the maximal number of nodes per match and add a column for each
						int maxNumNodes = 0;
						for (Match m : result.getMatches()) {
							maxNumNodes = Math.max(maxNumNodes, m.getSaltIDs().size());
						}
						
						// add columns for corpus and document name
						TableColumn corpusColumn = new TableColumn(table, SWT.NULL);
						corpusColumn.setText("corpus");
						
						TableColumn documentColumn = new TableColumn(table, SWT.NULL);
						documentColumn.setText("document");
						
						for (int i = 1; i <= maxNumNodes; i++) {
							TableColumn c = new TableColumn(table, SWT.NULL);
							c.setText("node #" + i);
						}
						
						int displayMatchCount = 0;
						for (Match m : result.getMatches()) {

							TableItem item = new TableItem(table, SWT.NULL);
							
							item.setData(m);
							
							if(!m.getSaltIDs().isEmpty()) {
								List<String> path = pathSplitter.splitToList(
										m.getSaltIDs().iterator().next().getPath());
								item.setText(0, path.get(0)); // corpus
								item.setText(1, path.get(path.size()-1)); // document
							} else {
								item.setText(0, "<unknown>");
								item.setText(1, "<unknown>");
							}
								
							int nodeIdx = 0;
							for (URI u : m.getSaltIDs()) {
								item.setText(2+nodeIdx, u.getFragment());
								nodeIdx++;
							}
							
							
							displayMatchCount++;
							if(displayMatchCount > 10000) {
								break;
							}
						}

						for (int i = 0; i < table.getColumnCount(); i++) {
							table.getColumn(i).pack();
						}
						long matchCount = result.getMatches().size();
						if(matchCount > 10000) {
							lblStatus.setText("Found " + matchCount + " matches. (Only displaying first 10.000 matches)");
						} else {
							lblStatus.setText("Found " + matchCount + " matches.");
						}
					});
									
				} catch(AnnisQLSyntaxException | AnnisQLSemanticsException ex ) {
					uiSync.asyncExec(() -> {
						lblStatus.setText("ERROR: " + ex.getMessage());
						MessageDialog.openError(parent.getShell(), "Can't parse AQL", ex.getMessage());
					});
				}
				return Status.OK_STATUS;
			}
				
		};
		j.setUser(true);
		j.setPriority(Job.LONG);
		j.schedule();	
	}
	
	private class OpenMenuListener implements SelectionListener {
		
		private final IEditorDescriptor editorDesc;

		public OpenMenuListener(IEditorDescriptor editorDesc) {
			this.editorDesc = editorDesc;
		}
		
		private void openMatchInEditor(Widget selected) {
			
			if(selected instanceof TableItem) {
				TableItem selectedItem = (TableItem) selected;
				if(selectedItem.getData() instanceof Match) {
					Match match = (Match) selectedItem.getData();
					// get first match (which already contains the corpus name and document name)
					List<String> path = pathSplitter.splitToList(match.getSaltIDs().get(0).getPath());
					// find the document in the Workspace
					IWorkspace workspace = ResourcesPlugin.getWorkspace();
					IWorkspaceRoot root = workspace.getRoot();
					
					for(IProject p : root.getProjects()) {
						if(path.get(0).equals(p.getName())) {
							try {
								IFile matchingDoc = searchDocumentInResource(p, path.get(path.size()-1));
								if(matchingDoc != null) {
									
									IEditorPart newEditor = null;
									if(editorDesc == null) {
										// use the default editor
										IEditorDescriptor desc = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(matchingDoc.getName());
										newEditor = page.openEditor(new FileEditorInput(matchingDoc), desc.getId());
									} else {
										 newEditor = page.openEditor(new FileEditorInput(matchingDoc), editorDesc.getId());
										
									}
									
									if(newEditor instanceof SaltNodeSelectable) {
										List<String> selectedNodeNames = new LinkedList<>();
										for(URI uri : match.getSaltIDs()) {
											selectedNodeNames.add(uri.toASCIIString());
										}
										((SaltNodeSelectable) newEditor).setSelection(selectedNodeNames);
									}
								}
								
							} catch (CoreException ex) {
								log.error("Could not open match in editor", ex);
							}
							break;
						}
					}
				}
			}
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(table.getSelectionCount() > 0) {
				openMatchInEditor(table.getSelection()[0]);
			}
			
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
