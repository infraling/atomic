[![Build Status](https://travis-ci.org/infraling/atomic.svg?branch=development)](https://travis-ci.org/infraling/atomic)

| :exclamation: |	**Repository will be archived soon!** |
|-|-|
|:warning:|Please note that Atomic is not developed anymore. It serves as architectural prototype for Hexatomic, which is currently under development at https://github.com/hexatomic/hexatomic.<br/>This repository will be archived soon!|

# Atomic
Software for multi-level annotation of linguistic corpora

## Build

`mvn install` builds the core plugins for Atomic.

Then there are also three Maven profiles, each of which builds a specific version of Atomic:

1. `mvn install -P stable` builds only stable features into *repository/target/products/*.
2. `mvn install -P preview` builds stable features and those that can be used productively with caution, and that may include bugs, into *repository-preview/target/products/*.
3. `mvn install -P experimental` builds stable and preview features, and those that are experimental, and hence should not be used productively, into *repository-experimental/target/products/*).

### Build documentation

Documentation is built separately from the product build. If you want to include up-to-date docs in the product build, build docs before product. Documentation has a [separate README](docs/org.corpus-tools.atomic.doc/README.md) with details on how to build.
