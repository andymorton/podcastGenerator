echo off
setlocal enabledelayedexpansion
set cp=lib:.:./podcastGenerator.jar
for %%f in (lib/*.jar) do (
	set cp=!cp!;%%f%
)

java -cp %cp% com.morty.podcast.writer.PodCastCreationRunner standard-podcast-context.xml
