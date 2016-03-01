/*******************************************************************************
 * Copyright 2016 Friedrich-Schiller-Universität Jena
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
 *******************************************************************************/
package org.corpus_tools.atomic.projects;

/**
 *  Compiles a "project" object in the target data model from a {@link ProjectData} object.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public interface ProjectCompiler {

	/**
	 * Runs the compilation task, i.e., maps the data from the {@link ProjectData} object
	 * including the complete corpus structure n-ary trees to a "project" object
	 * in the target data model.
	 * <p>
	 * Returns the complete "project" object in the target data model.
	 *
	 * @return the complete project in the target data model
	 */
	Object run();

	/**
	 * Sets the {@link #projectData} for instances of implementations
	 * of this interface.
	 *
	 * @param projectData
	 */
	void setProjectData(Corpus projectData);

}