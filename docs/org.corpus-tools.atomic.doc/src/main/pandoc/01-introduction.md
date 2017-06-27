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
The features in this version are preliminary implementations and may change in the future.

# License

Atomic is licensed under the Apache License, Version 2.0.

-----------------------------------------------------------------------------------------------
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     [`http://www.apache.org/licenses/LICENSE-2.0`](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
-----------------------------------------------------------------------------------------------

# Citation

If you have used Atomic in your work, please cite as:

- Stephan Druskat, Lennart Bierkandt, Volker Gast, Christoph Rzymski, and Florian Zipser (2014): "Atomic: an open-source software platform for multi-level corpus annotation". In *Proceedings of the 12th Konferenz zur Verarbeitung natürlicher Sprache (KONVENS 2014)*, Hildesheim, vol. 1, pp. 228–234. Available at: <http://nbn-resolving.de/urn:nbn:de:gbv:hil2-opus-2866>. 

# Introduction

Atomic uses [Salt](http://corpus-tools.org/salt) as its data model. Knowing a bit about how Salt works helps to make the most of Atomic.

## Salt

### Empty tokens

Empty tokens, i.e., tokens covering a section of the source text with length 0, are unordered by default. In Salt, tokens are connected to the source text via a relation (a graph edge) which have a `start` and an `end` label. These labels point to character indices in the source text. In the case of empty tokens, both labels have the same value. As more than one empty token covering the same zero-length section of the source text are implicitly the same token (although they can be modeled as different objects), they cannot have a specific order, and should be imagined as a vertically stacked rather than a horizontally linked, although they may be displayed as such (e.g., in the [*Grid Editor*](#grid-editor)). This means that the display order is arbitrary and can not be guaranteed.
