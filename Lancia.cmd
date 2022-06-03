set loc=%~dp0
echo %loc%
cd /d "%loc%"
cd
java -jar %loc%target\readfatt-jar-with-dependencies.jar -p FattEE.properties -f %1