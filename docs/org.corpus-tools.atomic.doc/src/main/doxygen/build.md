# Build Atomic

# Build the documentation {#build-documentation}

## User documentation {#user-documentation}

`TODO`

## Developer documentation {#developer-documentation}

In order to build the developer documentation locally, you have to have **Doxygen** installed on your build system. [Doxygen](http://doxygen.org/) is a tool that generates documentation from source code.

To build the documentation, go to `{HOME}/docs/org.corpus_tools.atomic.doc/` and run 

    mvn clean package -P doxygen

The documentation will be in `{HOME}/docs/org.corpus_tools.atomic.doc/target/doxygen/`.
