---
title:  Atomic User Guide
date:
- Stephan Druskat
abstract: |
	Atomic is an extensible annotation platform for multi-layer linguistic corpora. It runs on Windows, Linux and Mac OS X.
	The software works on the graph-based, generic **Salt** data model, and therefore can process diverse annotation types.
	It also includes **Pepper**, a conversion framework for linguistic data, which makes it compatible with a large number of linguistic formats.
	Atomic can be extended via plugins, i.e., new editors, views, analysis or processing components can be added to the platform.

	Atomic is open source under the Apache License, Version 2.0.
geometry: margin=1in
...

# Note

Atomic is currently undergoing a complete overhaul.

This does not make it feasible to publish any user documentation at this point. All released versions of Atomic will, however, include comprehensive user documentation in different formats.

# Introduction

Atomic uses [Salt](http://corpus-tools.org/salt) as its data model. Knowing a bit about how Salt works helps to make the most of Atomic.

## Salt

### Empty tokens

Empty tokens, i.e., tokens covering a section of the source text with length 0, are unordered by default. In Salt, tokens are connected to the source text via a relation (a graph edge) which have a `start` and an `end` label. These labels point to character indices in the source text. In the case of empty tokens, both labels have the same value. As more than one empty token covering the same zero-length section of the source text are implicitly the same token (although they can be modeled as different objects), they cannot have a specific order, and should be imagined as a vertically stacked rather than a horizontally linked, although they may be displayed as such (e.g., in the [*Grid Editor*](#grid-editor)). This means that the display order is arbitrary and can not be guaranteed.
