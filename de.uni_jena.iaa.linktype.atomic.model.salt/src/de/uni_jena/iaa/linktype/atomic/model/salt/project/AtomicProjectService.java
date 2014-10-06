/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena stephan.druskat@uni-jena.de
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package de.uni_jena.iaa.linktype.atomic.model.salt.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author Michael Grübsch
 * @version $Revision$, $Date$
 */
public class AtomicProjectService
{
  protected static final AtomicProjectService INSTANCE = new AtomicProjectService();
  
  // TODO gf. als OSGI-Service
  public static AtomicProjectService getInstance()
  {
    return INSTANCE;
  }

  protected AtomicProjectService()
  {
    super();
  }

  public IProject getProject(String projectName)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getProject(projectName);
  }

  public boolean isProjectExisting(String projectName)
  {
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    return root.getProject(projectName).exists();
  }

  public IProject createIProject(String desiredProjectName) throws CoreException
  {
    IProject project = getProject(desiredProjectName);

    project.create(null);
    project.open(null);
    // TODO CreateSaltProjectHandler.addAtomicProjectNatureToIProject(iProject); // FIXME Throws CoreException
    return project;
  }

}
