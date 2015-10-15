Namespaces
==========

In order to achieve maximum conformity in plugin project names, folder names, package names, Maven group IDs, etc. with the corpus-tools.org domain, the exact notation corpus-tools.org (or, org.corpus-tools) has been adapted where possible. However, for some instances restrictions apply, e.g., the dash cannot be used for Java package names. In these cases, the underscore is used (org.corpus_tools). Additionally, Eclipse does not seem to be able to deal with a mixture of both notations over plugin project names, plugin IDs, etc. Therefore, below table gives an overview of when to use which notation.


| Site of occurrence | dash - | underscore _ | example |
|---|:---:|:---:|---|
| Folder name | **X** | | /plugins/org.corpus-tools.atomic |
| Project name |  | **X** | org.corpus_tools.atomic.plugin |
| Maven group ID | **X** | | org.corpus-tools.atomic |
| Plugin ID (MANIFEST.MF, plugin.xml, etc.) | | **X** | org.corpus_tools.atomic.plugin |
| Extension ID | | **X** | <extension id="org.corpus_tools.atomic.application" point="org.eclipse.core.runtime.applications"> |
| Product file name | | **X** | org.corpus_tools.atomic.product |
| Package name | | **X** | org.corpus_tools.atomic.plugin.core |