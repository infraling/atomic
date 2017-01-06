/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.core.SNode;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionBindings;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.IKeyEventMatcher;
import org.eclipse.swt.events.KeyEvent;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenEditorSelectionBindings extends DefaultSelectionBindings {

	private final SDocumentGraph graph;

	/**
	 * @param graph
	 */
	public TokenEditorSelectionBindings(SDocumentGraph graph) {
		this.graph = graph;
	}

	@Override
	public void configureUiBindings(UiBindingRegistry uiBindingRegistry) {
		super.configureUiBindings(uiBindingRegistry);
		uiBindingRegistry.registerKeyBinding(new IKeyEventMatcher() {

			@Override
			public boolean matches(KeyEvent event) {
				if (event.keyCode == 'n') {
					return true;
				}
				return false;
			}
		}, new IKeyAction() {

			@Override
			public void run(NatTable natTable, KeyEvent event) {
				System.err.println("N gives us >" + graph.getText((SNode) natTable.getData("selectedToken")));

			}
		});
	}
}