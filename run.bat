echo off
setlocal enabledelayedexpansion
set cp=lib
for %%f in (lib/*.jar) do (
	set cp=!cp!;lib/%%f%
)

java -cp %cp% -jar podcastGenerator.jar standard-podcast-context.xml
