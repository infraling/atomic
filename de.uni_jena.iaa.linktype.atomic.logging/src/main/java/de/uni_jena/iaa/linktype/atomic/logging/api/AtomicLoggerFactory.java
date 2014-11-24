/**
 * 
 */
package de.uni_jena.iaa.linktype.atomic.logging.api;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.PropertyConfigurator;

import de.uni_jena.iaa.linktype.atomic.logging.Activator;
import de.uni_jena.iaa.linktype.atomic.logging.impl.Slf4jLogger;

/**
 * @author Stephan Druskat
 *
 */
public abstract class AtomicLoggerFactory {
	
	/**
	 * Configured flag.
	 */
	private static boolean isConfigured;

	/**
	 * loggers repo/cache.
	 */
	@SuppressWarnings("rawtypes")
	private static Map<Class, IAtomicLogger> loggersRepository;

	/**
	 * Log4j filename.
	 */
	private static final String LOG4J_FILENAME = "log4j2.xml";

	/**
	 * @param classname class name (typically caller class)
	 * @return logger
	 */
	public static IAtomicLogger getLogger(Class classname) {
		configure();
		if (loggersRepository.containsKey(classname)) {
			return loggersRepository.get(classname);
		} else {
			IAtomicLogger slf4jLogger = new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(classname));
			loggersRepository.put(classname, slf4jLogger);
			return slf4jLogger;
		}
	}

	/**
	 * Configure logger.
	 * @throws IOException 
	 */
	private synchronized static void configure() {
		if (isConfigured) {
			return;
		}
		isConfigured = true;
		// initialize MAP (cache)
		loggersRepository = new HashMap<Class, IAtomicLogger>();
		// configure logger
		File file = new File(".", LOG4J_FILENAME);
		if (file.exists()) {
			PropertyConfigurator.configure(file.getAbsolutePath());
		} else {
			// configure from default: local resources
			URL entry = Activator.getDefault().getBundle().getEntry(LOG4J_FILENAME);
			PropertyConfigurator.configure(entry);
		}
	}

}
