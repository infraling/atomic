/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.eclipse.emf.ecore.EObject;

import com.google.common.collect.HashBiMap;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.SDocumentGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SIdentifiableElement;

/**
 * @author Stephan Druskat
 *
 */
public class GraphElementRegistry {
	
//	private static HashBiMap<String, EObject> idMap = HashBiMap.create();
	private static final HashMap<String, String> prefixMap = new HashMap<String, String>();
	static {
		prefixMap.put("SStructureImpl", "N");
		prefixMap.put("SSpanImpl", "S");
		prefixMap.put("STokenImpl", "T");
		prefixMap.put("SDominanceRelationImpl", "D");
		prefixMap.put("SSpanningRelationImpl", "R");
		prefixMap.put("SOrderRelationImpl", "O");
		prefixMap.put("SPointingRelationImpl", "P");
	}
	private static HashMap<SDocumentGraph, HashBiMap<String, EObject>> graphIDMap = new HashMap<SDocumentGraph, HashBiMap<String,EObject>>();
	private static HashMap<SDocumentGraph, ArrayList<String>> typeOccurrenceMap = new HashMap<SDocumentGraph, ArrayList<String>>();

	public static String returnIDForElement(SIdentifiableElement element, SDocumentGraph graph) {
		ArrayList<String> typeOccurrences = null;
		HashBiMap<String, EObject> idMap = null;
		if (graphIDMap.get(graph) == null) {
			idMap = HashBiMap.create();
			typeOccurrences = new ArrayList<String>();
			graphIDMap.put(graph, idMap);
			typeOccurrenceMap.put(graph, typeOccurrences);
		}
		else {
			idMap = graphIDMap.get(graph);
			typeOccurrences = typeOccurrenceMap.get(graph);
		}
		if (idMap.containsValue(element)) {
			return idMap.inverse().get(element);
		}
		else {
			int typeOccurrence = Collections.frequency(typeOccurrences, element.getClass().getSimpleName());
			idMap.put(prefixMap.get(element.getClass().getSimpleName()) + (typeOccurrence + 1), (EObject) element);
			typeOccurrences.add(element.getClass().getSimpleName());
			return idMap.inverse().get(element);
		}
	}

}
