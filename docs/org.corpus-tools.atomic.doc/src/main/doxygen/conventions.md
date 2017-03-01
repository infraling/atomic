# Conventions

# Namespaces {#namespaces}

Maven's `artifactId` and OSGi's `Bundle-SymbolicName` must be the same for OSGi-fied components (i.e., plugins), otherwise Atomic will not build. At the same time, OSGi bundle names cannot contain a dash `-`.

Hence, all entities reflecting the namespace for `corpus-tools.org` must replace the dash `-` with an underscore `_`:

    org.corpus_tools

E.g., 

- Directory names
- Eclipse project names
- Maven group IDs
- OSGi bundle names
- Extension IDs 
- Product file names
- Java packages
