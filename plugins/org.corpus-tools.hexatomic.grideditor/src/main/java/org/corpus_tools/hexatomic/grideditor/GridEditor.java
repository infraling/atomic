package org.corpus_tools.hexatomic.grideditor;

import java.util.ArrayList; 
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.corpus_tools.atomic.api.editors.DocumentGraphEditor;
import org.corpus_tools.hexatomic.grideditor.GridEditor.NodeAnnotation;
import org.corpus_tools.hexatomic.grideditor.data.AnnotationGridPropertyAccessor;
//import org.corpus_tools.hexatomic.grideditor.data.AnnotationGridPropertyAccessor;
import org.corpus_tools.salt.common.SDocumentGraph;
import org.corpus_tools.salt.common.SSpan;
import org.corpus_tools.salt.common.SToken;
import org.corpus_tools.salt.core.SAnnotation;
import org.corpus_tools.salt.core.SNode;
import org.corpus_tools.salt.core.SRelation;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import org.eclipse.nebula.widgets.nattable.data.ISpanningDataProvider;
import org.eclipse.swt.SWT;

public class GridEditor extends DocumentGraphEditor {

	private ColorManager colorManager;
	private ISpanningDataProvider dataProvider = null;
	private Table<Object, String, Object> annotationTable;
	
	public GridEditor() {
		super();
		colorManager = new ColorManager();
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		super.init(site, input);
		annotationTable = compileAnnotationTable(graph);
	}
	
	private Table<Object, String, Object> compileAnnotationTable(SDocumentGraph graph) {
		Table<Object, String, Object> table = HashBasedTable.create();
		final List<SToken> orderedTokens = graph.getSortedTokenByText();
		for (SToken t : orderedTokens) {
			int idx = orderedTokens.indexOf(t);
			table.put(orderedTokens.indexOf(t), "Text", graph.getText(t));
			for (SAnnotation a : t.getAnnotations()) {
				table.put(idx, a.getQName().toString(), a);
			}
			List<SRelation<SNode, SNode>> rels = graph.getInRelations(t.getId());
			for (SRelation<SNode, SNode> r : rels) {
				SNode src = null;
				if ((src = r.getSource()) instanceof SSpan) {
					for (SAnnotation a : src.getAnnotations()) {
						table.put(idx, a.getQName().toString(), a);
					}
				}
			}
		}
		return table;
	}

	@Override
	public void dispose() {
		colorManager.dispose();
		super.dispose();
	}
	@Override
	public void createEditorPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		
		/* ############################################
		 * Grid
		 * ############################################
		 */
		dataProvider = createDataProvider();
		
		
		
	}
	
	private ISpanningDataProvider createDataProvider() {
		return new AnnotationGridDataProvider(graph, new AnnotationGridPropertyAccessor(annotationTable));
		return null;
	}
	
	/**
	 * @author Stephan Druskat
	 * 
	 *         // TODO Add description
	 *
	 */
	public class NodeAnnotation {
		
		private final SAnnotation annotation;
		private final SNode node;
		
		public NodeAnnotation(SAnnotation annotation, SNode node) {
			this.annotation = annotation;
			this.node = node;
		}
		
		/**
		 * @return the annotation
		 */
		public final SAnnotation getAnnotation() {
			return annotation;
		}
		/**
		 * @return the node
		 */
		public final SNode getNode() {
			return node;
		}
	
	}

}
