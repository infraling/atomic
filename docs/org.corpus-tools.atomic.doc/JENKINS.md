This script is set up to be used in a Jenkins build of Atomic, for building the snapshot documentation

    #!/bin/bash
    mvn clean package -P docs
    cd ../..
    cp -r ./docs/org.corpus-tools.atomic.doc/target/doxygen/doc-snapshot/* ./doctemp
    cp -r ./docs/org.corpus-tools.atomic.doc/target/docbkx/html/* ./userdoctemp
    cp -r ./docs/org.corpus-tools.atomic.doc/src/main/resources/ ./userdoctemp/assets
    rm ./userdoctemp/assets/.gitignore
    rm ./userdoctemp/assets/README.md
    git checkout gh-pages
    git pull origin gh-pages
    if [ -d doc-snapshot ]; then
      rm ./doc-snapshot/dev/placeholder
      rm ./doc-snapshot/user/placeholder
    fi
    mv ./doctemp/* ./doc-snapshot/dev/
    mv ./userdoctemp/* ./doc-snapshot/user/
    git add $(git ls-files --others --exclude-standard)
    git commit -a -m 'Update documentation (user and developer) (SNAPSHOT)'
    git push origin gh-pages
    # Uncomment in Jenkins and set to correct branch
    # git checkout issue/67