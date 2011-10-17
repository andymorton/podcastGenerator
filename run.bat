echo off
setlocal enabledelayedexpansion
set cp=lib;config;.;./podcastGenerator.jar

for %%f in (lib/*.jar) do (set cp=!cp!;lib/%%f%)

java -cp %cp% com.morty.podcast.writer.PodCastCreationRunner %1 > %2