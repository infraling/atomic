#!/bin/bash
(
	mkdir de.uni_jena.iaa.linktype.atomic.repository/target/products/plugins
	cd atomic-custom-files
	cp -r ./* ../de.uni_jena.iaa.linktype.atomic.repository/target/products/plugins/
	cd ..
	cd de.uni_jena.iaa.linktype.atomic.repository/target/products/
for file in ./*.zip; do
  echo "Replacing relANNIS and SaltXML plugins in" ${file##*/} "."
  zip -r ${file##*/} ./plugins/
done
rm -rf plugins/
)
echo "Finished!"
exit 0