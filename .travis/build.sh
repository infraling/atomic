#!/bin/sh

maven_build() {
	mvn clean package
}

setup_git() {
  git config --global user.email "travis@travis-ci.org"
  git config --global user.name "Travis CI"
}

commit_files() {
  git checkout -b travis-build
  git add .
  git commit --message "Travis build: $TRAVIS_BUILD_NUMBER"
}

upload_files() {
  git remote add origin-travis https://${GITHUB_TOKEN}@github.com/infraling/atomic.git
  git push --quiet --set-upstream origin-travis travis-build
}

maven_build
setup_git
commit_files
upload_files