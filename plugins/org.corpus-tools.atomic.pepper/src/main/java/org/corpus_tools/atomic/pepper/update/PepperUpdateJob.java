/*******************************************************************************
 * Copyright 2015 Friedrich-Schiller-Universität Jena,
 * Humboldt-Universität zu Berlin, INRIA
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
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.corpus_tools.atomic.pepper.AtomicPepperConfiguration;
import org.corpus_tools.atomic.pepper.AtomicPepperOSGiConnector;
import org.corpus_tools.atomic.pepper.AtomicPepperStarter;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DefaultHandler2;

import de.hu_berlin.german.korpling.saltnpepper.pepper.connectors.PepperConnector;

/**
 * A {@link Job} handling the update of Pepper modules.
 * <p>
 * 
 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
 */
public class PepperUpdateJob extends Job {

	/**
	 * Defines a static logger variable so that it references the XML{@link org.apache.logging.log4j.Logger} instance named "PepperUpdateJob".
	 */
	private static final Logger log = LogManager.getLogger(PepperUpdateJob.class);
	private static final String FILE_MODULES_XML = "modules.xml";
	private PepperConnector pepper;
	private Map<String, Pair<String, String>> moduleTable;
	private static String MODULES_XML_PATH = null;
	private ArrayList<String> resultLines = null;
	private String resultText;

	/**
	 * Constructor setting the path of the "modules.xml" file.
	 * 
	 * @param name
	 */
	public PepperUpdateJob(String name) {
		super(name);
		MODULES_XML_PATH = setModulesXMLPath();
		resultLines =  new ArrayList<String>();
	}

	/**
	 * Sets the path of the "modules.xml" file which contains a list of Pepper modules to be updated, identified by their Maven GAs.
	 *
	 * @return String The path of the "modules.xml" file
	 */
	private String setModulesXMLPath() {
		URL atomicHomeURL = Platform.getInstallLocation().getURL();
		File atomicHome = new File(atomicHomeURL.getFile());
		String modulesXMLPath = atomicHome.getAbsolutePath() + "/" + AtomicPepperConfiguration.FOLDER_PEPPER_CONF + "/" + FILE_MODULES_XML + "/";
		return modulesXMLPath;
	}

	/**
	 * Handles the actual update process by starting an instance of Pepper (via an instance of {@link AtomicPepperStarter}), and calling {@link #update()}.
	 * 
	 * @copydoc @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		AtomicPepperStarter pepperStarter = new AtomicPepperStarter();
		pepperStarter.startPepper();
		setPepper(pepperStarter.getPepper());

		IStatus updateStatus = update(monitor);
		setResultText("");
		for (String line : resultLines) {
			setResultText(getResultText().concat("\n").concat(line));
		}
		return updateStatus;
	}

	/**
	 * Performs the actual update process by calling {@link AtomicPepperOSGiConnector#update(String, String, String, boolean, boolean)} on all entries of the module table.
	 * 
	 * @param monitor
	 * @return
	 */
	private IStatus update(IProgressMonitor monitor) {
		try {
			if (moduleTable != null) {
				// Do nothing
			}
			else {
			moduleTable = new HashMap<String, Pair<String, String>>();
			SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
			saxParser.parse(MODULES_XML_PATH, new ModuleTableReader(moduleTable));
			}
		}
		catch (ParserConfigurationException | SAXException | IOException e) {
			log.error("Getting the Pepper module table from the file at {} didn't succeed!", MODULES_XML_PATH, e);
			return new PepperUpdateErrorStatus(e);
		}
		int workUnits = moduleTable.entrySet().size();
		SubMonitor subMonitor = SubMonitor.convert(monitor, workUnits);
		for (Map.Entry<String, Pair<String, String>> entry : moduleTable.entrySet()) {
			subMonitor.setTaskName("Updating " + entry.getKey() + " ...");
			boolean cancelled = updateModule(entry, subMonitor.newChild(1));
			if (cancelled) {
				return Status.CANCEL_STATUS;
			}
		}
		return Status.OK_STATUS;
	}

	/**
	 * TODO: Description
	 *
	 * @param entry
	 * @param newChild
	 */
	private boolean updateModule(Entry<String, Pair<String, String>> entry, SubMonitor newChild) {
		if (newChild.isCanceled()) {
			return true;
		}
		AtomicPepperOSGiConnector pepper = (AtomicPepperOSGiConnector) getPepper();
		if (pepper.update(entry.getValue().getLeft(), entry.getKey(), entry.getValue().getRight(), false, false)) {
			 resultLines.add(entry.getKey().concat(" successfully updated."));
		}
		else {
			 resultLines.add(entry.getKey().concat(" NOT updated."));
		}
		newChild.done();
		return false;
	}

	/**
	 * @return the pepper
	 */
	public PepperConnector getPepper() {
		return pepper;
	}

	/**
	 * @param pepper the pepper to set
	 */
	public void setPepper(PepperConnector pepper) {
		this.pepper = pepper;
	}

	/**
	 * @return the resultText
	 */
	public String getResultText() {
		return resultText;
	}

	/**
	 * @param resultText the resultText to set
	 */
	public void setResultText(String resultText) {
		this.resultText = resultText;
	}

	/**
	 * This class is the call back handler for reading the modules.xml file, which provides information about the Pepper modules to be updated or installed.
	 * 
	 * @author Martin Klotz
	 */
	private static class ModuleTableReader extends DefaultHandler2 {
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
			if (TAG_LIST.equals(localName)) {
				defaultRepository = attributes.getValue(ATT_DEFAULTREPO);
				defaultGroupId = attributes.getValue(ATT_DEFAULTGROUPID);
			}
			chars.delete(0, chars.length());
		}

		@Override
		public void characters(char[] ch, int start, int length) throws SAXException {
			for (int i = start; i < start + length; i++) {
				chars.append(ch[i]);
			}
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

	/**
	 * TODO Description
	 * <p>
	 * @author Stephan Druskat <stephan.druskat@uni-jena.de>
	 */
	class PepperUpdateErrorStatus implements IStatus {

		/**
		 * 
		 */
		public PepperUpdateErrorStatus(Throwable e) {
			this.e = e;
		}

		private Throwable e;

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getChildren()
		 */
		@Override
		public IStatus[] getChildren() {
			return null;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getCode()
		 */
		@Override
		public int getCode() {
			return -666;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getException()
		 */
		@Override
		public Throwable getException() {
			return e;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getMessage()
		 */
		@Override
		public String getMessage() {
			if (e instanceof ParserConfigurationException) {
				return "due to a ParserConfigurationExeption";
			}
			else if (e instanceof SAXException) {
				return "due to a SAXException";
			}
			else if (e instanceof IOException) {
				return "due to an IOException";
			}
			else {
				return null;
			}
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getPlugin()
		 */
		@Override
		public String getPlugin() {
			return null;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#getSeverity()
		 */
		@Override
		public int getSeverity() {
			return 0;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#isMultiStatus()
		 */
		@Override
		public boolean isMultiStatus() {
			return false;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#isOK()
		 */
		@Override
		public boolean isOK() {
			return false;
		}

		/*
		 * @copydoc @see org.eclipse.core.runtime.IStatus#matches(int)
		 */
		@Override
		public boolean matches(int severityMask) {
			return false;
		}

	}

}
