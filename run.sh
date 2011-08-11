#!/bin/sh
newclasspath=lib
for i in lib/*.jar
do
newclasspath=$newclasspath:lib/$i
done


java -cp $newclasspath -jar podcastGenerator.jar standard-podcast-context.xml
