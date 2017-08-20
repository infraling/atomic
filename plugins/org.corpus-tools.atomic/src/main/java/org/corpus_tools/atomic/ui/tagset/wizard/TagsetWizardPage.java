/**
 * 
 */
package org.corpus_tools.atomic.ui.tagset.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;

/**
 * // TODO Add description
 *
 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
 * 
 */
public class TagsetWizardPage extends WizardPage {
	
	private static final Logger log = LogManager.getLogger(TagsetWizardPage.class);
	
	private IProject project = null;
	
	/**
	 * // TODO Add description
	 * 
	 */
	public TagsetWizardPage() {
		super("Corpus tagset");
		setTitle("Corpus tagset");
		setDescription("Choose the corpus you want to create a tagset for.");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		
		Label lblChooseTheCorpus = new Label(container, SWT.NONE);
		lblChooseTheCorpus.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblChooseTheCorpus.setText("Choose the corpus project you are creating the tagset for:");
		
		// Corpus combo
		final ComboViewer comboViewer = new ComboViewer(container);
		comboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		comboViewer.setLabelProvider(new ProjectLabelProvider());
		comboViewer.setContentProvider(ArrayContentProvider.getInstance());
		List<IProject> projects = getProjectsWithoutTagsets();
		comboViewer.setInput(projects);
		comboViewer.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				log.info("Select tagset for corpus '{}'.", comboViewer.getStructuredSelection().getFirstElement());
				setProject((IProject) comboViewer.getStructuredSelection().getFirstElement());
				setPageComplete(isPageComplete());
			}
		});
		
		setControl(container);
		setPageComplete(false);
	}

	private List<IProject> getProjectsWithoutTagsets() {
		IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		List<IProject> projectsWithoutTagsets = new ArrayList<>();
		for (IProject iProject : allProjects) {
			IFile tagsetFile = iProject.getFile(iProject.getName().replaceAll(" ", "-") + ".ats");
			if (!tagsetFile.exists()) {
				projectsWithoutTagsets.add(iProject);
			}
		}
		return projectsWithoutTagsets;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return getProject() != null;
	}

	/**
	 * @return the project
	 */
	public final IProject getProject() {
		return project;
	}

	/**
	 * @param project the project to set
	 */
	private final void setProject(IProject project) {
		this.project = project;
	}

	/**
	 * A simple {@link LabelProvider} returning names of {@link IProject}s.
	 *
	 * @author Stephan Druskat <[mail@sdruskat.net](mailto:mail@sdruskat.net)>
	 */
	public class ProjectLabelProvider extends LabelProvider {

		@Override
		public String getText(Object element) {
			IProject project = null;
			if (element instanceof IProject) {
				project = (IProject) element;
			}
			return project == null ? "" : project.getName();
		}

	}
}
