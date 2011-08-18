#!/bin/sh
CLASSPATH=lib:.:./podcastGenerator.jar
for i in lib/*.jar
do
CLASSPATH=$CLASSPATH:$i
done

echo $CLASSPATH

java -classpath $CLASSPATH com.morty.podcast.writer.PodCastCreationRunner standard-podcast-context.xml
