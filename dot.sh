#!/bin/bash

function split {
   name="${1%.*}"
   echo $name
}

echo "Converting DOT to PDF"
for FILE in `ls *.dot`
do
	echo $FILE
	prefix=`split $FILE dot`
	dot -Tpdf $FILE -o $prefix.pdf
	echo $prefix.pdf
done
