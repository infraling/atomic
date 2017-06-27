[![Build Status](https://travis-ci.org/infraling/atomic.svg?branch=development)](https://travis-ci.org/infraling/atomic)

# Atomic
Software for multi-level annotation of linguistic corpora

## Build

`mvn install` creates three different versions of Atomic:

1. Stable (in *repository/target/products/*): Includes only stable features.
2. Review (in *repository-review/target/products/*): Includes features that can be used productively with caution, and that may include bugs.
3. Experimental (in *repository-experimental/target/products/*): Includes features that are experimental, and hence should not be used productively.

### Build documentation

Documentation is built separately from the product build. If you want to include up-to-date docs in the product build, build docs before product. Documentation has a [separate README](docs/org.corpus-tools.atomic.doc/README.md) with details on how to build.