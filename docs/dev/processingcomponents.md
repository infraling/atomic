Processing components
=====================

Extending Atomic with new processing components
-----------------------------------------------

Property names of processing component configuration classes - which are Java Beans - must be externalized.
In order to do so, run the String Externalization Wizard on the class implementing ProcessingComponentConfiguration.
Give the accessor and properties file a meaningful name, we suggest "ConfigurationProperties.java" and "configuration.properties".
In the class extending ProcessingComponentConfigurationControls, for all controls
which are configuring configuration properties, call `setProperty.(control, ConfigurationProperties.<propertyName>)`.
This will make sure that the automated databinding works for your configuration. 
In the background, the GUI class targeting processing components and their configuration is responsible for setting up JFace databinding for your configuration.

If configuration properties are not of type `java.lang.String`, it is necessary to implement a converter which converts the value of the GUI widget (a `java.lang.String`) to the type of the configureation property. For example, if the configuration has a property `String[] skills` and the GUI widget's value is a list of comma-separated Strings ("programming, singing, dancing"), the converter will map the widget's value (a String) to a `String[]`, and vice versa. Some converters are already contained in package ???, so see if you can use one of the available converters before implementing your own. 