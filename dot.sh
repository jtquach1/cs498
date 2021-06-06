#!/bin/bash

input_dir=/home/jtquach/cs498
retrieval_dir=/home/jtquach/jtqua/Documents

function split {
   name="${1%.*}"
   ext="${1##*.}"
   # echo filename=$name extension=$ext
   echo $name
}

mv $input_dir/*.dot .
echo "Converting DOT to PDF"
for FILE in `ls *.dot`
do
	echo $FILE
	prefix=`split $FILE dot`
	dot -Tpdf $FILE -o $prefix.pdf
	echo $prefix.pdf
done

mv *.dot $retrieval_dir
mv *.pdf $retrieval_dir
