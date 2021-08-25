#!/bin/bash

function name {
   name="${1%.*}"
   echo $name
}

echo "Converting DOT to PDF"
for FILE in `ls *.dot`
do
	echo $FILE
	prefix=`name $FILE dot`
	dot -Tpdf $FILE -o $prefix.pdf
	echo $prefix.pdf
done
