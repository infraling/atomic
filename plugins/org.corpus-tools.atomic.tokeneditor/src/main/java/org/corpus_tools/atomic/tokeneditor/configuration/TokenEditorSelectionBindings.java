/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionBindings;
import org.eclipse.nebula.widgets.nattable.ui.action.IKeyAction;
import org.eclipse.nebula.widgets.nattable.ui.binding.UiBindingRegistry;
import org.eclipse.nebula.widgets.nattable.ui.matcher.KeyEventMatcher;
import org.eclipse.swt.SWT;
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
	protected void configurePageDownButtonBindings(UiBindingRegistry uiBindingRegistry, IKeyAction action) {
		IKeyAction newAction = new IKeyAction() {
			
			@Override
			public void run(NatTable natTable, KeyEvent event) {
				// FIXME: Implement new selection n steps to right
				System.err.println("CUSTOM PAGE DOWN PRESSED!");
				
			}
		};
        uiBindingRegistry.registerKeyBinding(
                new KeyEventMatcher(SWT.NONE, SWT.PAGE_DOWN), newAction);
        uiBindingRegistry.registerKeyBinding(
                new KeyEventMatcher(SWT.MOD2, SWT.PAGE_DOWN), newAction);
        uiBindingRegistry.registerKeyBinding(
                new KeyEventMatcher(SWT.MOD1, SWT.PAGE_DOWN), newAction);
        uiBindingRegistry.registerKeyBinding(
                new KeyEventMatcher(SWT.MOD2 | SWT.MOD1, SWT.PAGE_DOWN), newAction);
    }
}