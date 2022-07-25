@echo off
pushd %~dp0
set /p nr1=Enter Log File path:
java -jar loganalyzer-0.0.1-SNAPSHOT-jar-with-dependencies.jar %nr1%
pause