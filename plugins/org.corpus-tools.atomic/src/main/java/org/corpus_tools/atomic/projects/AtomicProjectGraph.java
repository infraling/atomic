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
 *******************************************************************************/
package org.corpus_tools.atomic.projects;

import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;

/**
 * This class implements a String-based graph representing
 * the Atomic project structure, i.e.:
 * <p>
 * A project, which contains exactly one {@link SaltProject} (not
 * represented as {@link SaltProject}s are always XMI-persisted in
 * a file called SaltProject.salt), which contains a corpus graph
 * (not represented as Atomic assumes that there is always only
 * exactly one corpus graph), which contains n corpora, which
 * contain m documents each, which contain exactly one source text
 * each.
 * <p> 
 * See also the graphical in {@link IProjectCreator}.
 *
 * <p>@author Stephan Druskat <stephan.druskat@uni-jena.de>
 *
 */
public class AtomicProjectGraph {

}
