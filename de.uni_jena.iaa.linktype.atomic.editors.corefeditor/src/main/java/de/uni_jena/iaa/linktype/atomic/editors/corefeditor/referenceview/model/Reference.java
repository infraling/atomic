/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.editors.corefeditor.referenceview.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SSpan;

/**
 * @author Stephan Druskat
 *
 */
public class Reference {
	
	private String name;
	private List<SSpan> spans = new ArrayList<SSpan>();
	private TreeMap<Integer, SSpan> spanMap = new TreeMap<Integer, SSpan>();
	
	public SSpan addSpan(SSpan span) {
		getSpans().add(span);
		return span;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the spans
	 */
	public List<SSpan> getSpans() {
		return spans;
	}

	/**
	 * @param spans the spans to set
	 */
	public void setSpans(List<SSpan> spans) {
		this.spans = spans;
	}

	/**
	 * @return the spanMap
	 */
	public TreeMap<Integer, SSpan> getSpanMap() {
		return spanMap;
	}

	/**
	 * @param spanMap the spanMap to set
	 */
	public void setSpanMap(TreeMap<Integer, SSpan> spanMap) {
		this.spanMap = spanMap;
	}
	
}
