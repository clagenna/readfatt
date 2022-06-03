set loc=%~dp0
echo %loc%
cd /d "%loc%"
forfiles -p "F:\Google Drive\SMichele\AASS" -m EE_*.pdf -c "java -jar %loc%target\readfatt-jar-with-dependencies.jar -f @path -p FattEE.properties"
