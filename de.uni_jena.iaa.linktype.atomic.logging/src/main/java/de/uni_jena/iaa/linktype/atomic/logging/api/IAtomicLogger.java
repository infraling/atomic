/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.logging.api;

/**
 * @author Stephan Druskat
 *
 */
public interface IAtomicLogger {

	/**
	 * log debug messages.
	 * @param message
	 */
	void debug(String message);

	/**
	 * log trace messages.
	 * @param message
	 */
	void trace(String message);

	/**
	 * log info messages.
	 * @param message
	 */
	void info(String message);

	/**
	 * log warn messages.
	 */
	void warn(String message);

	/**
	 * log error messages.
	 * @param message
	 */
	void error(String message);

	/**
	 * log Throwable objects (typically exceptions). 
	 * @param message
	 * @param e
	 */
	void error(String message, Throwable e);
	
}
