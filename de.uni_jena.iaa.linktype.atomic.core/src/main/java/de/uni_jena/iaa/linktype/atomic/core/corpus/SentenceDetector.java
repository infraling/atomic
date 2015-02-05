/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.core.corpus;

import java.util.Set;
import java.util.TreeMap;

import com.google.common.collect.Range;
import com.google.common.collect.TreeRangeSet;

/**
 * Subclasses must override the method {@link SentenceDetector#detectSentenceRanges(String)}
 * with their own implementation. The return type is a {@link TreeMap}-backed {@link Set} of
 * {@link Range}s, i.e. of two integers (start, end) that define the index boundaries of a sentence
 * within the corpus text. Subclasses must use {@link Range#closed(Comparable, Comparable)}
 * for creating ranges to be added to the {@link TreeRangeSet}.
 * 
 * @author Stephan Druskat
 *
 */
public abstract class SentenceDetector {
	
	public abstract TreeRangeSet<Integer> detectSentenceRanges(String corpusText);

}
