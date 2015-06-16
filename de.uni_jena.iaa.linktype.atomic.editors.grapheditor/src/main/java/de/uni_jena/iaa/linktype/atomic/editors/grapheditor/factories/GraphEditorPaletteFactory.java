/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories;

import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.Activator;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.factories.SaltElementsFactory.ElementType;
import de.uni_jena.iaa.linktype.atomic.editors.grapheditor.util.CreationAndDirectEditTool;

/**
 * @author Stephan Druskat
 *
 */
public class GraphEditorPaletteFactory {
	
	private static ImageDescriptor SPANNINGRELATION_DESCRIPTOR, DOMINANCERELATION_DESCRIPTOR, POINTINGRELATION_DESCRIPTOR, ORDERRELATION_DESCRIPTOR, STRUCTURE_DESCRIPTOR, SPAN_DESCRIPTOR;

	/**
	 * 
	 */
	private static void getImages() {
		AbstractUIPlugin plugin = Activator.getDefault();
		ImageRegistry imageRegistry = plugin.getImageRegistry();
		SPANNINGRELATION_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.SPANNINGRELATION_ICON));
		DOMINANCERELATION_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.DOMINANCERELATION_ICON));
		POINTINGRELATION_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.POINTINGRELATION_ICON));
		ORDERRELATION_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.ORDERRELATION_ICON));
		STRUCTURE_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.STRUCTURE_ICON));
		SPAN_DESCRIPTOR = ImageDescriptor.createFromImage(imageRegistry.get(Activator.SPAN_ICON));
	}

	public static PaletteRoot createPalette() {
		getImages();
		PaletteRoot palette = new PaletteRoot();
		palette.add(createToolsGroup(palette));
		palette.add(createNodesDrawer());
		palette.add(createEdgesDrawer());
		return palette;
	}

	private static PaletteEntry createEdgesDrawer() {
		PaletteDrawer edgesDrawer = new PaletteDrawer("Edges");
		edgesDrawer.add(createDominanceRelationTool());
		edgesDrawer.add(createSpanningRelationTool());
		edgesDrawer.add(createPointingRelationTool());
		edgesDrawer.add(createOrderRelationTool());
		return edgesDrawer;
	}

	private static PaletteEntry createOrderRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Order relation", "Create a new order relation", new SaltElementsFactory(ElementType.ORDER_RELATION), ORDERRELATION_DESCRIPTOR, ORDERRELATION_DESCRIPTOR);
		entry.setToolClass(ConnectionCreationTool.class);
		return entry;
	}

	private static PaletteEntry createPointingRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Pointing relation", "Create a new pointing relation", new SaltElementsFactory(ElementType.POINTING_RELATION), POINTINGRELATION_DESCRIPTOR, POINTINGRELATION_DESCRIPTOR);
		entry.setToolClass(ConnectionCreationTool.class);
		return entry;
	}

	private static PaletteEntry createSpanningRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Spanning relation", "Create a new spanning relation", new SaltElementsFactory(ElementType.SPANNING_RELATION), SPANNINGRELATION_DESCRIPTOR, SPANNINGRELATION_DESCRIPTOR);
		entry.setToolClass(ConnectionCreationTool.class);
		return entry;
	}

	private static PaletteEntry createDominanceRelationTool() {
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry("Dominance relation", "Create a new dominance relation", new SaltElementsFactory(ElementType.DOMINANCE_RELATION), DOMINANCERELATION_DESCRIPTOR, DOMINANCERELATION_DESCRIPTOR);
		entry.setToolClass(ConnectionCreationTool.class);
		return entry;
	}

	private static PaletteEntry createNodesDrawer() {
		PaletteDrawer nodesDrawer = new PaletteDrawer("Nodes");
		nodesDrawer.add(createStructureTool());
		nodesDrawer.add(createSpanTool());
		return nodesDrawer;
	}

	private static PaletteEntry createSpanTool() {
		CreationToolEntry entry = new CombinedTemplateCreationEntry("Span", "Create a new Span", new SaltElementsFactory(ElementType.SPAN), SPAN_DESCRIPTOR, SPAN_DESCRIPTOR);
		entry.setToolClass(CreationAndDirectEditTool.class);
		return entry;	}

	private static PaletteEntry createStructureTool() {
		CreationToolEntry entry = new CombinedTemplateCreationEntry("Structure", "Create a new Structure", new SaltElementsFactory(ElementType.STRUCTURE), STRUCTURE_DESCRIPTOR, STRUCTURE_DESCRIPTOR);
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
