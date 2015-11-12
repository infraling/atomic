/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Stephan Druskat - initial API and implementation
 *     Martin Klotz - nested class {@link ModuleTableReader} initial API
 *     					and implementation
 *******************************************************************************/
package org.corpus_tools.atomic.pepper.update;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.framework.Bundle;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.cli.PepperStarterConfiguration;
import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.impl.PepperOSGiConnector;

/**
 * TODO Description
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateJob extends Job {

	/**
	 * Defines a static logger variable so that it references the XML{@link org.apache.logging.log4j.Logger} instance named "PepperUpdateJob".
	 */
	private static final Logger log = LogManager.getLogger(PepperUpdateJob.class);

	/**
	 * Contains the path to the modules.xml file, which provides information about modules to be updated or installed.
	 */
	private static final String MODULES_XML_PATH = getModulesXMLPath();

	/**
	 * Contains all registered modules with groupId, artifactId and Maven repository. Compiled during the first call of {@link PepperUpdateJob#}
	 */
	Map<String, Pair<String, String>> moduleTable;

	private AtomicPepperOSGiConnector pepper;

	/**
	 * @param name
	 */
	public PepperUpdateJob(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Returns the String representation of the absolute path for
	 * the modules.xml file. The location is found via {@link org.eclipse.core.runtime.FileLocator#find(URL)}
	 * and the relative path from the o.c-t.a.pepper bundle root. 
	 *
	 * @return modulesXMLPath The modules.xml path.
	 */
	private static String getModulesXMLPath() {
		Bundle bundle = Platform.getBundle("org.corpus_tools.atomic.pepper");
		URL url = FileLocator.find(bundle, new Path("conf/modules.xml"), null);
		URL modulesXMLURL = null;
	    try {
			modulesXMLURL = FileLocator.resolve(url);
		}
		catch (IOException e) {
			log.error("Could not resolve location of modules.xml!", e);
		}
	    String modulesXMLPath = new File(modulesXMLURL.getFile()).getAbsolutePath();
		return modulesXMLPath;
	}

	/*
	 * @copydoc @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		// Update via pepper-lib
		// Remember: PepperUpdateJob is the equivalent of PepperStarter here,
		// run only with args "update all"
		log.info("Loading Pepper properties.");
		AtomicPepperConfiguration pepperProps = new AtomicPepperConfiguration();
		pepperProps.load();
		
		log.trace("Creating new Atomic Pepper OSGi connector and set its configuration to the newly loaded pepper properties: {}.", pepperProps);
		pepper = new AtomicPepperOSGiConnector();// AtomicPepperOSGiConnector();
		pepper.setConfiguration(pepperProps);
		if (!pepper.isInitialized()) {
			pepper.init();
		}
		
		updateAllPepperModules();
		
		return Status.OK_STATUS;
	}

	/**
	 * The actual update (or initial installation) of all Pepper modules
	 * listed in modules.xml. 
	 */
	private void updateAllPepperModules() {
		AtomicPepperOSGiConnector pepperConnector = getPepper();
		log.trace("Get the module table from modules.xml");
		try {
			moduleTable = getModuleTable();
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Getting the module table failed!", e);
		}
		
		log.trace("Read the module table, update each module, and write the update result into a list of Strings, one item per module.");
		List<String> lines = new ArrayList<String>();
		for (String s : moduleTable.keySet()) {
			if (pepperConnector.update(moduleTable.get(s).getLeft(), s, moduleTable.get(s).getRight(), false, false)) {
				lines.add(s.concat(" successfully updated."));
			} else {
				lines.add(s.concat(" NOT updated."));
			}
		}
		Collections.<String> sort(lines);
	}

	/**
	 * If the module table has not been parsed from the modules.xml
	 * file already (and set to the {@link #moduleTable} field),
	 * do it now. Either way, return {@link #moduleTable}.
	 *
	 * @return moduleTable The module table
	 */
	private Map<String, Pair<String, String>> getModuleTable() throws ParserConfigurationException, SAXException, IOException {
		if (this.moduleTable != null) {
			return moduleTable;
		}
		HashMap<String, Pair<String, String>> table = new HashMap<String, Pair<String, String>>();
		SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
		try {
			saxParser.parse(MODULES_XML_PATH, new ModuleTableReader(table));
		}
		catch (Exception e) {
			log.debug("Unable to parse modules.xml.", e);
		}
		return table;
	}

	/**
	 * @return pepper The AtomicPepperOSGiConnector
	 */
	public AtomicPepperOSGiConnector getPepper() {
		return pepper;
	}

	/**
	 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
	 *
	 * Licensed under the Apache License, Version 2.0 (the "License");
	 * you may not use this file except in compliance with the License.
	 * You may obtain a copy of the License at
	 *
	 *       http://www.apache.org/licenses/LICENSE-2.0
	 *
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS,
	 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	 * See the License for the specific language governing permissions and
	 * limitations under the License.
	 *
	 *
	 */
	/**
	 * This class is the call back handler for reading the modules.xml file, which provides Information about the pepperModules to be updated / installed.
	 * 
	 * @author klotzmaz
	 */
	private class ModuleTableReader extends DefaultHandler2 {
		/**
		 * all read module names are stored here Map: artifactId --> (groupId, repository)
		 */
		private Map<String, Pair<String, String>> listedModules;
		/** this string contains the last occurred artifactId */
		private String artifactId;
		/** this string contains the group id */
		private String groupId;
		/** this string contains the repository */
		private String repo;
		/** the name of the tag between the modules are listed */
		private static final String TAG_LIST = "pepperModulesList";
		/**
		 * the name of the tag in the modules.xml file, between which the modules' properties are listed
		 */
		private static final String TAG_ITEM = "pepperModules";
		/**
		 * the name of the tag in the modules.xml file, between which the modules' groupId is written
		 */
		private static final String TAG_GROUPID = "groupId";
		/**
		 * the name of the tag in the modules.xml file, between which the modules' name is written
		 */
		private static final String TAG_ARTIFACTID = "artifactId";
		/**
		 * the name of the tag in the modules.xml file, between which the modules' source is written
		 */
		private static final String TAG_REPO = "repository";
		/** the name of the attribute for the default repository */
		private static final String ATT_DEFAULTREPO = "defaultRepository";
		/** the name of the attribute for the default groupId */
		private static final String ATT_DEFAULTGROUPID = "defaultGroupId";
		/** contains the default groupId for modules where no groupId is defined */
		private String defaultGroupId;
		/**
		 * contains the default repository for modules where no repository is defined
		 */
		private String defaultRepository;
		/** is used to read the module name character by character */
		private StringBuilder chars;

		/** this boolean says, whether characters should be read or ignored */
		// private boolean openEyes;

		public ModuleTableReader(Map<String, Pair<String, String>> artifactIdUrlMap) {
			listedModules = artifactIdUrlMap;
			chars = new StringBuilder();
			groupId = null;
			artifactId = null;
			repo = null;
			// openEyes = false;
		}

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			localName = qName.substring(qName.lastIndexOf(":") + 1);
			// openEyes = TAG_GROUPID.equals(localName) ||
			// TAG_ARTIFACTID.equals(localName) || TAG_REPO.equals(localName);
			if (TAG_LIST.equals(localName)) {
				defaultRepository = attributes.getValue(ATT_DEFAULTREPO);
				defaultGroupId = attributes.getValue(ATT_DEFAULTGROUPID);
			}
			chars.delete(0, chars.length());
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			for (int i = start; i < start + length /* && openEyes */; i++) {
				chars.append(ch[i]);
			}
			// openEyes = false;
		}

		@Override
		public void endElement(java.lang.String uri, String localName, String qName) throws SAXException {
			localName = qName.substring(qName.lastIndexOf(":") + 1);
			if (TAG_ARTIFACTID.equals(localName)) {
				artifactId = chars.toString();
				chars.delete(0, chars.length());
			}
			else if (TAG_GROUPID.equals(localName)) {
				groupId = chars.toString();
				chars.delete(0, chars.length());
			}
			else if (TAG_REPO.equals(localName)) {
				repo = chars.toString();
				chars.delete(0, chars.length());
			}
			else if (TAG_ITEM.equals(localName)) {
				groupId = groupId == null ? defaultGroupId : groupId;
				listedModules.put(artifactId, Pair.of(groupId, (repo == null || repo.isEmpty() ? defaultRepository : repo)));
				chars.delete(0, chars.length());
				groupId = null;
				artifactId = null;
				repo = null;
			}
		}
	}
}
