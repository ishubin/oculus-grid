set -e

cd ..
mkdir -p bin/storage
mkdir -p target
rm -rf bin/*
rm -rf target/*
mvn assembly:assembly -DskipTests=true
cp target/oculus-grid-jar-with-dependencies.jar bin/oculus-grid.jar
cp grid.agent.tags.xml bin/.
cp scripts/*.properties bin/.
cp scripts/run-server.sh bin/.
cp scripts/run-agent.sh bin/.

cd bin
zip -r -9 oculus-grid.zip *

ls -1 | grep -v *.zip | xargs rm -rf 

