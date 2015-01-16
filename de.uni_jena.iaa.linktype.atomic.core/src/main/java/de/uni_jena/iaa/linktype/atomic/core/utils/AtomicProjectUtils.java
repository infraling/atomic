/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sDocumentStructure.tokenizer.Tokenizer;

/**
 * @author Stephan Druskat
 *
 */
public class AtomicProjectUtils {
	
	private static final Logger log = LoggerFactory.getLogger(AtomicProjectUtils.class);
	
	private static ArrayList<Object[]> tokenizers = new ArrayList<Object[]>() {
		private static final long serialVersionUID = 1L;	
		{
	    add(new Object[]{new Tokenizer(), "TreeTagger Tokenizer"});
		}
	};
	
	private AtomicProjectUtils() {}

	public static String extractStringFromFile(String filePath) throws IOException {
		File corpusTextFile = new File(filePath);
		StringBuffer buffer = new StringBuffer();
		
		FileInputStream fis = new FileInputStream(corpusTextFile);
	    InputStreamReader isr = new InputStreamReader(fis, AtomicProjectUtils.getFileEncoding());
	    Reader in = new BufferedReader(isr);
	    int ch;
	    while ((ch = in.read()) > -1) {
	        buffer.append((char)ch);
	    }
	    in.close();
	    return buffer.toString();
	}

	public static String getFileEncoding() {
		// FIXME Add combobox to FileDialog rather than using extra dialog
		// FIXME Add guessEncoding
		// FIXME Clean up code and make safe
		String[] encodings = new String[] {"UTF8", "ASCII", "ISO8859_1", "ISO8859_2", "ISO8859_4", "ISO8859_5", "ISO8859_7", "ISO8859_9", "ISO8859_13", "ISO8859_15", "KOI8_R", "UTF-16", "UnicodeBigUnmarked", "UnicodeLittleUnmarked", "Cp1250", "Cp1251", "Cp1252", "Cp1253", "Cp1254", "Cp1257", "UnicodeBig", "UnicodeLittle"};
		String[] list = new String[] {"UTF-8 (Eight-bit UCS Transformation Format)", "US-ASCII (American Standard Code for Information Interchange)", "ISO-8859-1 (ISO 8859-1, Latin Alphabet No. 1)", "ISO-8859-2 (Latin Alphabet No. 2)", "ISO-8859-4 (Latin Alphabet No. 4)", "ISO-8859-5 (Latin/Cyrillic Alphabet)", "ISO-8859-7 (Latin/Greek Alphabet)", "ISO-8859-9 (Latin Alphabet No. 5)", "ISO-8859-13 (Latin Alphabet No. 7)", "ISO-8859-15 (Latin Alphabet No. 9)", "KOI8-R (KOI8-R, Russian)", "UTF-16 (Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark)", "UTF-16BE (Sixteen-bit Unicode Transformation Format, big-endian byte order)", "UTF-16LE (Sixteen-bit Unicode Transformation Format, little-endian byte order)", "windows-1250 (Windows Eastern European)", "windows-1251 (Windows Cyrillic)", "windows-1252 (Windows Latin-1)", "windows-1253 (Windows Greek)", "windows-1254 (Windows Turkish)", "windows-1257 (Windows Baltic)"};
		ArrayList<String> listList = new ArrayList<String>();
		for (int i = 0; i < list.length; i++) {
			listList.add(list[i]);
		}
		ListDialog ld = new ListDialog(Display.getCurrent().getActiveShell());
		ld.setAddCancelButton(true);
		ld.setContentProvider(new ArrayContentProvider());
		ld.setLabelProvider(new LabelProvider());
		ld.setInput(list);
		ld.setInitialSelections(new Object[]{list[0]});
		ld.setTitle("Select file encoding");
		ld.setAddCancelButton(false);
		if (ld.open() == Dialog.OK);
			String result = (String) ld.getResult()[0];
		int ind = listList.indexOf(result);
		return encodings[ind];
	}

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
	};
	
	public static void addAtomicProjectNatureToIProject(IProject iProject) {
		try {
		      IProjectDescription description = iProject.getDescription();
		      String[] natures = description.getNatureIds();
		      String[] newNatures = new String[natures.length + 1];
		      System.arraycopy(natures, 0, newNatures, 0, natures.length);
		      newNatures[natures.length] = "de.uni_jena.iaa.linktype.atomic.core.atomicProjectNature";
		      description.setNatureIds(newNatures);
		      iProject.setDescription(description, null);
		   } catch (CoreException e) {
		      // Something went wrong
		   }
		log.info("Added Atomic Project Nature to IProject.");
	}
}
