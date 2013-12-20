/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.utils;

import java.util.ArrayList;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicTokenizerUtils {
	
	private static ArrayList<Object[]> tokenizers = new ArrayList<Object[]>() {
		private static final long serialVersionUID = 1L;	
		{
	    add(new Object[]{new Tokenizer(), "TreeTagger Tokenizer"});
		}
	};
	
	private AtomicTokenizerUtils() {
	};
	
	public static Object[] getTokenizers() {
		Object[] tokenizerArray = new Object[tokenizers.size()];
		for (int i = 0; i < tokenizers.size(); i++) {
			tokenizerArray[i] = tokenizers.get(i)[0];
		}
		return tokenizerArray;
	}
	
	public static String[] getTokenizerNames() {
		String[] tokenizerNameArray = new String[tokenizers.size()];
		for (int i = 0; i < tokenizers.size(); i++) {
			tokenizerNameArray[i] = (String) tokenizers.get(i)[1];
		}
		return tokenizerNameArray;
	}

}
