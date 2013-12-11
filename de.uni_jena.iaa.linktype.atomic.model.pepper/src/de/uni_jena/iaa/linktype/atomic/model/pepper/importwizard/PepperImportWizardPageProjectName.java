/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * Michael Grübsch
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

package de.uni_jena.iaa.linktype.atomic.model.pepper.importwizard;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import de.uni_jena.iaa.linktype.atomic.model.salt.project.AtomicProjectService;

/**
 * 
 * @author Michael Grübsch
 * @version $Revision: 1.2 $, $Date: 2012/03/29 22:59:03 $
 */
public class PepperImportWizardPageProjectName extends WizardPage
{
  protected final PepperImportWizard pepperImportWizard;
  protected Text text;

  /**
   * Create the wizard.
   */
  public PepperImportWizardPageProjectName(PepperImportWizard pepperImportWizard, String pageName, String title, ImageDescriptor titleImage)
  {
    super(pageName, title, titleImage);
    setPageComplete(false);
    setDescription("Enter the name of the project whose data should be imported.");

    this.pepperImportWizard = pepperImportWizard;
  }

  /**
   * Create contents of the wizard.
   * 
   * @param parent
   */
  @Override
  public void createControl(Composite parent)
  {
    Composite container = new Composite(parent, SWT.NULL);

    setControl(container);
    GridLayout gl_container = new GridLayout(2, false);
    gl_container.marginBottom = 20;
    container.setLayout(gl_container);

    Label lblNewLabel = new Label(container, SWT.NONE);
    lblNewLabel.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 2, 1));
    lblNewLabel.setText("Project name; must be unique");

    text = new Text(container, SWT.BORDER);
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    text.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        updatePageComplete();
      }
    });
  }

  protected String getProjectName()
  {
    String path = text.getText().trim();
    return 0 < path.length() ? path : null;
  }

  protected void updatePageComplete()
  {
    String projectName = getProjectName();
    if (projectName != null)
    {
      if (AtomicProjectService.getInstance().isProjectExisting(projectName))
      {
        setMessage(null);
        setErrorMessage("Project does already exist - choose another name.");
        setPageComplete(false);
      }
      else
      {
        setMessage(null);
        setErrorMessage(null);
        setPageComplete(true);
      }
    }
    else
    {
      setMessage(null);
      setErrorMessage(null);
      setPageComplete(false);
    }

    pepperImportWizard.setProjectName(projectName);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVisible(boolean visible)
  {
    if (visible)
    {
      String projectName = pepperImportWizard.getProjectName();
      if (projectName != null)
      {
        text.setText(projectName);
      }
    }

    super.setVisible(visible);

    if (visible)
    {
      text.setFocus();
    }
  }
}
