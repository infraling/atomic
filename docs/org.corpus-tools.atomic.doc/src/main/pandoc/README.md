# Atomic User Documentation

## This folder contains the Markdown sources for the Atomic User Documentation

### How to build

    pandoc -s -S -f markdown+yaml_metadata_block -t docbook5 atomic-user-guide.md -o ../docbook/atomic-user-guide.xml -M date="`date "+%Y-%m-%d"`"

*Maven version in pandoc*

    pandoc -s -S -f markdown+yaml_metadata_block -t docbook5 atomic-user-guide.md -o ../docbook/atomic-user-guide.xml -M date="`printf 'ATOMICVERSION=${project.version}\n0\n' | mvn -f ../../../../../pom.xml org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate | grep -oP '(?<=ATOMICVERSION=).*'`"
