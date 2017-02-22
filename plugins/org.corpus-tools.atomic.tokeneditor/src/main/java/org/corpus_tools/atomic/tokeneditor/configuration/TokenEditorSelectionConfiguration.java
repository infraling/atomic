/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.configuration;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.eclipse.nebula.widgets.nattable.selection.config.DefaultSelectionLayerConfiguration;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenEditorSelectionConfiguration extends DefaultSelectionLayerConfiguration {

	private final SDocumentGraph graph;

	/**
	 * @param graph
	 */
	public TokenEditorSelectionConfiguration(SDocumentGraph graph) {
		this.graph = graph;
	}

	@Override
	protected void addSelectionUIBindings() {
		addConfiguration(new TokenEditorSelectionBindings(graph));
	}
}