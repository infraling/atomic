Programming model
=================

As of version 0.3.0, Atomic is partly based on the [Eclipse 4 programming model](https://wiki.eclipse.org/Eclipse4/RCP). However, as some plugins providing core functionalities (Project Explorer, Console View) are not yet available as "pure e4" plugins (i.e., following the Eclipse 4 programming model without dependencies to Eclipse 3.x APIs), Atomic employs the so-called [compatibility layer](http://www.eclipse.org/community/eclipse_newsletter/2013/february/article3.php#compatibiliylayer_overview), which allows running 3.x plugins on top of an Eclipse 4 application.

Once the respective plugins are available as pure e4 plugins, these should replace the old 3.x ones, finally making Atomic a pure e4 application.

To make that transition maximally easy once it is possible, Atomic's plugins are - among other things - separated along whether they depend on 3.x APIs. 3.x-dependent plugin project names follow the pattern <i>de.uni_jena.iaa.linktype.atomic.<b>compat</b>.domain.\<further nested patterns\></i>. One obvious exception is *d.u.i.l.atomic*, the core plugin, which is also running on the compatibility layer. *Library* plugins don't follow either model.

The following table gives an overview over which plugin project belongs to which category. The prefix *de.uni_jena.iaa.linktype* is omitted for readability (not for the *.atomic* core plugin, however, which is placed at the top of the table).

| Plugin | 3.x | e4 | lib |
|---|:---:|:---:|:---:|
| atomic | **X** | | |
| logging | | | **X** |
| product | | **X** | |