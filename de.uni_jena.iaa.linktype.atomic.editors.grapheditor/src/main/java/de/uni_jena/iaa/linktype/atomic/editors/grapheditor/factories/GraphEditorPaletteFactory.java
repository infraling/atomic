/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.SaltElementsFactory.ElementType;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.CreationAndDirectEditTool;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditorPaletteFactory {

	public static PaletteRoot createPalette() {
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createNodesDrawer());
		return palette;
	}

	private static PaletteEntry createNodesDrawer() {
		PaletteDrawer nodesDrawer = new PaletteDrawer("Nodes");
		nodesDrawer.add(createStructureTool());
		nodesDrawer.add(createSpanTool());
		return nodesDrawer;
	}

	private static PaletteEntry createSpanTool() {
		CreationToolEntry entry = new CombinedTemplateCreationEntry("Span", "Create a new Span", new SaltElementsFactory(ElementType.SPAN), null, null);//NODE_ICON, NODE_ICON);
		entry.setToolClass(CreationAndDirectEditTool.class);
		return entry;	}

	private static PaletteEntry createStructureTool() {
		CreationToolEntry entry = new CombinedTemplateCreationEntry("Structure", "Create a new Structure", new SaltElementsFactory(ElementType.STRUCTURE), null, null);//NODE_ICON, NODE_ICON);
		entry.setToolClass(CreationAndDirectEditTool.class);
		return entry;	
	}

	private static PaletteEntry createToolsGroup(PaletteRoot palette) {
		PaletteToolbar toolbar = new PaletteToolbar("Tools");
		ToolEntry tool = new PanningSelectionToolEntry();
		toolbar.add(tool);
		palette.setDefaultEntry(tool);
		toolbar.add(new MarqueeToolEntry());
		return toolbar;
	}

}
