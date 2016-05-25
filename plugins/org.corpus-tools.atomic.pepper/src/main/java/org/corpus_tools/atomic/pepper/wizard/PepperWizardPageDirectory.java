/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * Michael Gr�bsch
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

package org.corpus_tools.atomic.pepper.wizard;

import java.io.File;

import org.corpus_tools.atomic.pepper.wizard.AbstractPepperWizard.ExchangeTargetType;
import org.corpus_tools.pepper.common.MODULE_TYPE;
import org.corpus_tools.pepper.modules.PepperModule;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Michael Gr�bsch
 * @version $Revision: 1.2 $, $Date: 2012/03/29 22:59:03 $
 */
public class PepperWizardPageDirectory<P extends PepperModule> extends WizardPage implements IWizardPage
{
  protected final AbstractPepperWizard<P> pepperWizard;

  protected Text text;
  protected Button btnFile;
  protected Button btnDirectory;

  /**
   * Create the wizard.
   */
  public PepperWizardPageDirectory
    ( AbstractPepperWizard<P> pepperWizard
    , String pageName
    , String title
    , ImageDescriptor titleImage
    , String description
    )
  {
    super(pageName, title, titleImage);
    setPageComplete(false);
    setDescription(description);

    this.pepperWizard = pepperWizard;
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
    GridLayout gl = new GridLayout(2, false);
    gl.marginBottom = 20;
    container.setLayout(gl);

    Label label;
    
    Composite composite = new Composite(container, SWT.NONE);
    composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
    RowLayout rl = new RowLayout(SWT.HORIZONTAL);
    rl.marginLeft = rl.marginRight = 0;
    composite.setLayout(rl);
    
    label = new Label(composite, SWT.NONE);
    label.setText("Target path is a ");

    btnFile = new Button(composite, SWT.RADIO);
    btnFile.setText("File");
    
    btnDirectory = new Button(composite, SWT.RADIO);
    btnDirectory.setText("Directory");

    switch (pepperWizard.getExchangeTargetType())
    {
      case FILE:
        btnFile.setSelection(true);
        break;
      case DIRECTORY:
        btnDirectory.setSelection(true);
        break;
    }

    SelectionAdapter btnSelectionListener = new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        updatePageComplete();
      }
    };

    btnFile.addSelectionListener(btnSelectionListener);
    btnDirectory.addSelectionListener(btnSelectionListener);

    label = new Label(container, SWT.NONE);
    label.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, true, false, 2, 1));
    switch (pepperWizard.getWizardMode())
    {
      case IMPORTER:
        label.setText("Source path the data should be imported from");
        break;
      case EXPORTER:
        label.setText("Target path the data should be exported to");
        break;
	default:
		break;
    }

    text = new Text(container, SWT.BORDER);
    String directory = pepperWizard.getExchangeTargetPath();
    if (directory != null)
    {
      text.setText(directory);
    }
    text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    text.addModifyListener(new ModifyListener()
    {
      @Override
      public void modifyText(ModifyEvent e)
      {
        updatePageComplete();
      }
    });

    Button button = new Button(container, SWT.NONE);
    button.addSelectionListener(new SelectionAdapter()
    {
      @Override
      public void widgetSelected(SelectionEvent e)
      {
        if (btnFile.getSelection())
        {
          FileDialog dialog;
          switch (pepperWizard.getWizardMode())
          {
            case IMPORTER:
              dialog = new FileDialog(getShell(), SWT.OPEN);
              break;
            case EXPORTER:
              dialog = new FileDialog(getShell(), SWT.SAVE);
              dialog.setOverwrite(true);
              break;
            default:
              throw new IllegalArgumentException("Unknown wizard mode: " + pepperWizard.getWizardMode());
          }

          dialog.setFileName(text.getText());
          String fileName = dialog.open();
          if (fileName != null)
          {
            text.setText(fileName);
          }
        }
        else
        {
          DirectoryDialog dialog = new DirectoryDialog(getShell());
          dialog.setFilterPath(text.getText());
          String directoryName = dialog.open();
          if (directoryName != null)
          {
            text.setText(directoryName);
          }
        }
      }
    });
    button.setText("...");
  }

  protected String getTargetPath()
  {
    String path = text.getText().trim();
    return 0 < path.length() ? path : null;
  }

  /**
   *
   * @param targetPath
   * @return
   */
  protected String validateTargetPath(File targetPath, boolean isFile)
  {
    if (pepperWizard.getWizardMode() == MODULE_TYPE.EXPORTER)
    {
      if (targetPath.exists())
      {
        return
            isFile && targetPath.isFile()
          ? null
          : isFile
          ? "Target path exists but is not a file!"
          : targetPath.isDirectory()
          ? null
          : "Target path exists but is not a directory!";
      }
      else
      {
        return null;
      }
    }
    else
    {
      if (isFile)
      {
        return
            ! targetPath.exists()
          ? "Target file does not exists!"
          : ! targetPath.isFile()
          ? "Target path is not a file!"
          : ! targetPath.canRead()
          ? "Target file can not be read!"
          : null;
      }
      else
      {
        if (targetPath.exists())
        {
          if (targetPath.isDirectory())
          {
            File[] files = targetPath.listFiles();
            
            if (files != null && 0 < files.length)
            {
              return null;
            }
            else
            {
              return "Target directory contains no files!";
            }
          }
          else
          {
            return "Target path is not a directory!";
          }
        }
        else
        {
          return "Target directory does not exists!";
        }
      }
    }
  }

  protected void updatePageComplete()
  {
    String targetPath = getTargetPath();
    if (targetPath != null)
    {
      String validationMessage = validateTargetPath(new File(targetPath), btnFile.getSelection());

      setMessage(null);
      setErrorMessage(validationMessage);
      setPageComplete(validationMessage == null);
    }
    else
    {
      setMessage(null);
      setErrorMessage(null);
      setPageComplete(false);
    }

    pepperWizard.setExchangeTargetPath(targetPath);
    pepperWizard.setExchangeTargetType(btnFile.getSelection() ? ExchangeTargetType.FILE : ExchangeTargetType.DIRECTORY);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void setVisible(boolean visible)
  {
    if (visible)
    {
      updatePageComplete();
    }

    super.setVisible(visible);
  }
}
