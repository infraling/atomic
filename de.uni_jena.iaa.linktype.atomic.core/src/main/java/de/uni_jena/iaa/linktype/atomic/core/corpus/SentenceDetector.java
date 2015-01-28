/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Stephan Druskat
 *
 */
public abstract class SentenceDetector {
	
	public List<int[]> getSentenceRanges(String corpusText) {
		List<int[]> sentenceRanges = new ArrayList<int[]>();
		int sentenceStartIndex = -1;
		String[] sentences = detectSentences(corpusText);
		for (int i = 0; i < sentences.length; i++) {
			System.err.println(sentences[i].trim());
		}
		for (String sentence : sentences) {
			int sentenceStart = corpusText.indexOf(sentence, sentenceStartIndex);
			int sentenceEnd = sentenceStart + (sentence.length());
			sentenceRanges.add(new int[]{sentenceStart, sentenceEnd});
			sentenceStartIndex = sentenceEnd;
		}
		return sentenceRanges;
	}
	
	protected abstract String[] detectSentences(String corpusText);

}
