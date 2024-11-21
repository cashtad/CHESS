#!/bin/bash
mkdir ./bin
javac -cp .\src;.\lib\jfreechart-1.5.3.jar -encoding UTF-8 -d .\bin src\*.java
