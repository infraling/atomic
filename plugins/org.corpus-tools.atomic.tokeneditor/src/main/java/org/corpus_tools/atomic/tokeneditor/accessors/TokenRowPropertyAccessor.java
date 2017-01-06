/**
 * 
 */
package org.corpus_tools.atomic.tokeneditor.accessors;

import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.STextualRelation;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.nebula.widgets.nattable.data.IColumnPropertyAccessor;

/**
 * TODO Description
 *
 * @author Stephan Druskat <mail@sdruskat.net>
 *
 */
public class TokenRowPropertyAccessor implements IColumnPropertyAccessor<SToken> {

	private final SDocumentGraph graph;

	// private final List<String> propertyNames = Arrays.asList("text",
	// "offsets");

	/**
	 * @param graph
	 */
	public TokenRowPropertyAccessor(SDocumentGraph graph) {
		this.graph = graph;
	}

	@Override
	public Object getDataValue(SToken token, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return graph.getText(token);

		case 1:
			int start = 0, end = 0;
			for (SRelation<?, ?> outRel : ((SNode) token).getOutRelations()) {
				if (outRel instanceof STextualRelation) {
					start = ((STextualRelation) outRel).getStart();
					end = ((STextualRelation) outRel).getEnd();
					return start + " - " + end;
				}
			}
			break;

		default:
			break;
		}

		return null;
	}

	@Override
	public void setDataValue(SToken token, int columnIndex, Object newValue) {
	}

	@Override
	public int getColumnCount() {
		return 2;
	}

	@Override
	public String getColumnProperty(int columnIndex) {
		return null;
	}

	@Override
	public int getColumnIndex(String propertyName) {
		return -1;
	}
}