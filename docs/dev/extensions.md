Extending Atomic
================

Introduction
------------

Atomic can be extended in two ways.

1. Existing third-party features or plugins can be added to the software via a Marketplace, or via installing features or plugins directly from an update site. This is the most common way an end user will extend Atomic.
2. You can extend Atomic yourself by creating a plugin or feature. It is this latter way of extending Atomic that this document is concerned with.

How to create new functionality for Atomic
------------------------------------------

Atomic offers a number of **extension points**, i.e., a "socket" where you can plug in your functionality. Extension points are basically contracts – usually a combination of a definition in XML markup, and Java interfaces – that extensions (plugins) must conform to. New Atomic plugins that want to connect to a specific extension point must implement the specified contract. For an overview of Eclipse’s extension point mechanism see <a href="#eclipse-plugins">Clayberg and Rubel (2009, 637–660)</a>.
  

See below for a list of extension points and their description.

References
----------

<a name="eclipse-plugins"></a>Clayberg, E. and Rubel, D. (2009). Eclipse Plug-ins. Addison-Wesley Professional, Boston, Mass., 3rd edition.