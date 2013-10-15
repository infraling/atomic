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
/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.console;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;

import de.uni_jena.iaa.linktype.atomic.model.salt.editor.console.AtomicALConsole;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicALConsoleFactory implements IConsoleFactory {
	
	private static IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	private static AtomicALConsole console = new AtomicALConsole("AtomicAL Console", null, null, "UTF-8", true, page);

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
	 */
	@Override
	public void openConsole() {
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
	    ConsolePlugin.getDefault().getConsoleManager().showConsoleView(console);
	}
	
	public static void closeConsole() {  
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();  
	    if (console != null) {  
	    	manager.removeConsoles(new IConsole[] { console });  
	    }  
	}  
	 
	public static AtomicALConsole getConsole(){  
		return console;  
	}

}
