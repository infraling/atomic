/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.atomical.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicalAnnotationGraphParser {
	
	/**
	 * @param rawInput
	 * @return
	 */
	public static String parseRawParameters(String rawInput) {
		try {
			return rawInput.trim().split(" ", 2)[1];
		} catch (ArrayIndexOutOfBoundsException e) { // In case no parameters have been put in (i.e., single "n" command)
			return "";
		}
		
	}

	/**
	 * @param rawInput
	 * @return
	 */
	public static String parseCommand(String rawInput) {
		return rawInput.trim().split(" ", 2)[0];
	}

	@SuppressWarnings("unchecked")
	public static HashMap<Object, Object> parseParameters(String rawParameters) { // FIXME Change Object to String and TEST
		HashMap<Object, Object> hash = new HashMap<Object, Object>();
		hash.put("attributes".intern(), new LinkedHashMap<Object, Object>()); // Attributes
		hash.put("switch", new ArrayList<String>(1));
		hash.put("keys".intern(), new ArrayList<Object>()); // Valueless keys, used for deleting parameters
		hash.put("elements".intern(), new ArrayList<String>()); // All nodes and edges from the String
		hash.put("words".intern(), new ArrayList<String>()); // All Strings that are not attributes
		hash.put("all_nodes", new ArrayList<String>()); // Nodes and Tokens
		hash.put("meta".intern(), new ArrayList<String>()); // Any meta information provided
		hash.put("nodes".intern(), new ArrayList<String>()); // All nodes (SStructures)
		hash.put("edges".intern(), new ArrayList<String>()); // All edges
		hash.put("tokens".intern(), new ArrayList<String>()); // All Tokens
		hash.put("spans".intern(), new ArrayList<String>()); // All Spans

		// Regex 
		HashMap<Object, Object> r = new HashMap<Object, Object>();
		Pattern ctrl = Pattern.compile("\\s|:"); //: and empty String
		r.put("ctrl".intern(), ctrl);
		Pattern bstring = Pattern.compile("[^\\s:]+"); // Everything but : and empty String
		r.put("bstring".intern(), bstring);
		Pattern qstring = Pattern.compile("\"(([^\"]*(\\\\\"[^\"]*)*[^\\\\])|)\""); // Quoted Strings that can include escape character \"
		r.put("qstring".intern(), qstring);
		Pattern string = Pattern.compile("(" + r.get("qstring") + "|" + r.get("bstring") + ")");
		r.put("string".intern(), string);
		Pattern attribute = Pattern.compile(r.get("string") + ":" + r.get("string") + "?");
		r.put("attribute".intern(), attribute);
		for (Object key : r.keySet()) {
			Pattern oldVal = null;
			if (r.get(key) instanceof Pattern)
				oldVal = (Pattern) r.get(key);
			r.put(key, Pattern.compile("^" + oldVal));
		}
		
		// Write matches to hash
		while (!rawParameters.isEmpty()) {
			Matcher m = null;
			if ((m = ((Pattern) r.get("attribute")).matcher(rawParameters)).find()) {
				String key = m.group(2) != null ? m.group(2).replace("\\\"", "\"") : m.group(1);
				String val = m.group(6) != null ? m.group(6).replace("\\\"", "\"") : m.group(5);
				if (val == null) 
					((ArrayList<Object>) hash.get("keys")).add(key);
				else 
					((LinkedHashMap<Object, Object>) hash.get("attributes")).put(key, val);
			}
			else if ((m = ((Pattern) r.get("string")).matcher(rawParameters)).find()) {
				String word = m.group(2) != null ? m.group(2).replace('\"', '"') : m.group(1);
				((ArrayList<String>) hash.get("words")).add(word);
				if (word.matches("^([DNPTRSOdnptrso]\\d+)|M$")) { // ENT (not ent as in ltraw) because in Atomic caps are used for IDs
					((ArrayList<String>) hash.get("elements")).add(word);
					switch (word.charAt(0)) {
					case 'N':
					case 'n':
						((ArrayList<String>) hash.get("nodes")).add(word);
						((ArrayList<String>) hash.get("all_nodes")).add(word);
						break;
					case 'S':
					case 's':
						((ArrayList<String>) hash.get("spans")).add(word);
						((ArrayList<String>) hash.get("all_nodes")).add(word);
						break;
//					case 'M':
//						((ArrayList<String>) hash.get("meta")).add(word);
//						((ArrayList<String>) hash.get("all_nodes")).add(word);
//						break;
					case 'D':
					case 'd':
						((ArrayList<String>) hash.get("edges")).add(word);
						break;
					case 'P':
					case 'p':
						((ArrayList<String>) hash.get("edges")).add(word);
						break;
					case 'O':
					case 'o':
						((ArrayList<String>) hash.get("edges")).add(word);
						break;
					case 'R':
					case 'r':
						((ArrayList<String>) hash.get("edges")).add(word);
						break;
					case 'T':
					case 't':
						((ArrayList<String>) hash.get("tokens")).add(word);
						((ArrayList<String>) hash.get("all_nodes")).add(word);
						break;
					default:
						break;
					}
				}
				else if (word.matches("-dsop")) // edge switch
					((ArrayList<String>) hash.get("switch")).add(word.substring(1));
			}
			else 
				if ((m = ((Pattern) r.get("ctrl")).matcher(rawParameters)).find());
			else
				break;
			int stringLength = rawParameters.length();
			rawParameters = rawParameters.substring(m.group(0).length(), stringLength);
		}
		return hash;
	}

}
