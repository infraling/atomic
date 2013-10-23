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
package de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette;

import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;

import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SDominanceRelationFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SPointingRelationFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SStructureFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.tools.CreationAndDirectEditTool;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.tools.UnannotatedConnectionCreationTool;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicEditorPalette extends PaletteRoot {
	
	PaletteGroup group;
	
	public AtomicEditorPalette() {
		addGroup();
		addSelectionTool();
		addSStructureTool();
		addSPointingRelationTool();
		addSDominanceRelationTool();
	}
	
	private void addGroup() {
		group = new PaletteGroup("Salt Controls");
		add(group);
	}
	
	private void addSelectionTool() {
		SelectionToolEntry entry = new SelectionToolEntry();
		group.add(entry);
	}
	
	private void addSStructureTool() {
		CreationToolEntry entry = new CreationToolEntry("Structure node", "Create a new SStructure", new SStructureFactory(), null, null);
		entry.setToolClass(CreationAndDirectEditTool.class);
		group.add(entry);
	}

	private void addSPointingRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Pointing relation", "Create a new pointing relation", new SPointingRelationFactory(), null, null);
		entry.setToolClass(UnannotatedConnectionCreationTool.class);
		group.add(entry);
	}
	
	private void addSDominanceRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Dominance relation", "Create a new dominance relation", new SDominanceRelationFactory(), null, null);
		entry.setToolClass(UnannotatedConnectionCreationTool.class);
		group.add(entry);
	}

}
