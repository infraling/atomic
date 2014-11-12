#!/bin/bash
(
	mkdir de.uni_jena.iaa.linktype.atomic.repository/target/products/plugins
	cd atomic-custom-files
	cp -r ./* ../de.uni_jena.iaa.linktype.atomic.repository/target/products/plugins/
	cd ..
	cd de.uni_jena.iaa.linktype.atomic.repository/target/products/
for file in ./*.zip; do
  echo "Working on" ${file##*/} "now."
  zip -r ${file##*/} ./plugins/
  #zip -r ${file##*/} test/
  #mv ${file##*/} unzip/
done
)
exit 0






#You can do the following, when your current directory is parent_directory:

#for d in [0-9][0-9][0-9]
#do
#    ( cd $d && your-command-here )
#done

#The ( and ) create a subshell, so the current directory isn't changed in the main script.
