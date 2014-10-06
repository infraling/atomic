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
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.uni_jena.iaa.linktype.atomic.model.salt.editor.Activator;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SDominanceRelationFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SPointingRelationFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SSpanFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SSpanningRelationFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.factories.SStructureFactory;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.tools.CreationAndDirectEditTool;
import de.uni_jena.iaa.linktype.atomic.model.salt.editor.palette.tools.UnannotatedConnectionCreationTool;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicEditorPaletteFactory {
	
	private static ImageDescriptor DIRECTED_ICON, UNDIRECTED_ICON, NODE_ICON, SPAN_ICON, SPANNINGREL_ICON;
	
	/**
	 * 
	 */
	private static void getImages() {
		AbstractUIPlugin plugin = Activator.getDefault();
		ImageRegistry imageRegistry = plugin.getImageRegistry();
		DIRECTED_ICON = ImageDescriptor.createFromImage(imageRegistry.get(Activator.DIRECTED_EDGE_ICON));
		UNDIRECTED_ICON = ImageDescriptor.createFromImage(imageRegistry.get(Activator.UNDIRECTED_EDGE_ICON));
		NODE_ICON = ImageDescriptor.createFromImage(imageRegistry.get(Activator.NODE_ICON));
		SPAN_ICON = ImageDescriptor.createFromImage(imageRegistry.get(Activator.SPAN_ICON));
		SPANNINGREL_ICON = ImageDescriptor.createFromImage(imageRegistry.get(Activator.SPANNINGREL_ICON));
	}
	
	private static PaletteEntry createSStructureTool() {
		CreationToolEntry entry = new CreationToolEntry("Structure node", "Create a new SStructure", new SStructureFactory(), NODE_ICON, NODE_ICON);
		entry.setToolClass(CreationAndDirectEditTool.class);
		return entry;
	}

	private static PaletteEntry createSSpanTool() {
		CreationToolEntry entry = new CreationToolEntry("Span node", "Create a new SSpan", new SSpanFactory(), SPAN_ICON, SPAN_ICON);
		entry.setToolClass(CreationAndDirectEditTool.class);
		return entry;
	}
	
	private static PaletteEntry createSPointingRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Pointing relation", "Create a new pointing relation", new SPointingRelationFactory(), DIRECTED_ICON, DIRECTED_ICON);
		entry.setToolClass(UnannotatedConnectionCreationTool.class);
		return entry;
	}
	
	private static PaletteEntry createSSpanningRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Spanning relation", "Create a new spanning relation", new SSpanningRelationFactory(), SPANNINGREL_ICON, SPANNINGREL_ICON);
		entry.setToolClass(UnannotatedConnectionCreationTool.class);
		return entry;
	}
	
	private static PaletteEntry createSDominanceRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Dominance relation", "Create a new dominance relation", new SDominanceRelationFactory(), UNDIRECTED_ICON, UNDIRECTED_ICON);
		entry.setToolClass(UnannotatedConnectionCreationTool.class);
		return entry;
	}

	public static PaletteRoot createPalette() {
		getImages();
		PaletteRoot palette = new PaletteRoot();
		palette.add(createSelectionToolsGroup(palette));
		palette.add(createNodesDrawer());
		palette.add(createRelationsDrawer());
		return palette;
	}

	private static PaletteEntry createRelationsDrawer() {
		PaletteDrawer relationsDrawer = new PaletteDrawer("Relations");
		relationsDrawer.add(createSSpanningRelationTool());
		relationsDrawer.add(createSPointingRelationTool());
		relationsDrawer.add(createSDominanceRelationTool());
	
		return relationsDrawer;
	}

	private static PaletteEntry createNodesDrawer() {
		PaletteDrawer nodesDrawer = new PaletteDrawer("Nodes");
		nodesDrawer.add(createSStructureTool());
		nodesDrawer.add(createSSpanTool());
		return nodesDrawer;
	}

	private static PaletteEntry createSelectionToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		toolbar.add(new MarqueeToolEntry());
		return toolbar;
	}

}
