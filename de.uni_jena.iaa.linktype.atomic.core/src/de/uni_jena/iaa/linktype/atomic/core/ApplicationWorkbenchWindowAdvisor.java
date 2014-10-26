/*******************************************************************************
 * Copyright 2013 Friedrich Schiller University Jena
 * stephan.druskat@uni-jena.de
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
package de.uni_jena.iaa.linktype.atomic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.internal.dialogs.WorkbenchWizardElement;
import org.eclipse.ui.internal.ide.EditorAreaDropAdapter;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.wizards.IWizardCategory;
import org.eclipse.ui.wizards.IWizardDescriptor;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    public ApplicationWorkbenchWindowAdvisor(IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    public ActionBarAdvisor createActionBarAdvisor(IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }
    
    @SuppressWarnings("restriction")
	@Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(1000, 800));
        configurer.setShowMenuBar(true);
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);
        configurer.setShowProgressIndicator(true);
        // FIXME: Replace internal class with custom class (EditorAreaDropAdapter)!
        configurer.configureEditorAreaDropListener(new EditorAreaDropAdapter(configurer.getWindow()));
    }
    
    @Override
    public void postWindowOpen() {
    	AbstractExtensionWizardRegistry wizardRegistry = (AbstractExtensionWizardRegistry)PlatformUI.getWorkbench().getNewWizardRegistry();
    	IWizardCategory[] categories = PlatformUI.getWorkbench().getNewWizardRegistry().getRootCategory().getCategories();
    	for(IWizardDescriptor wizard : getAllWizards(categories)){
    	    if(wizard.getCategory().getId().matches("org.eclipse.ui.Basic")){
    	        WorkbenchWizardElement wizardElement = (WorkbenchWizardElement) wizard;
    	        wizardRegistry.removeExtension(wizardElement.getConfigurationElement().getDeclaringExtension(), new Object[]{wizardElement});
    	    }
    	}
    }
    
    private IWizardDescriptor[] getAllWizards(IWizardCategory[] categories) {
    	List<IWizardDescriptor> results = new ArrayList<IWizardDescriptor>();
    	for(IWizardCategory wizardCategory : categories){
    		results.addAll(Arrays.asList(wizardCategory.getWizards()));
    		results.addAll(Arrays.asList(getAllWizards(wizardCategory.getCategories())));
    	}
    	return results.toArray(new IWizardDescriptor[0]);
	}
    
}
