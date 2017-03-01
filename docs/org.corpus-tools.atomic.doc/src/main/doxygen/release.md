# Release Atomic

# How to build a release-ready product {#release}

Change directory to `docs/org.corpus-tools.atomic.doc/`.

    cd docs/org.corpus-tools.atomic.doc/

Run

    mvn clean package -P doxygen

Run

    mvn verify -P eclipse

Change directory to project root.

    cd ../..

Run

    mvn clean install

---
When using Jenkins, don't run `mvn verify -P eclipse`, instead move the profile functionality to an *Execute shell* step in the Build Job Config.

---