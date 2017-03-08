#!/bin/bash
sed -i -e 's/href="/href="doc\/help\//g' $1
sed -i -e 's/topic="/topic="doc\/help\//g' $1