set -e

versionInPom=`cat pom.xml | grep "<version>.*</version>" | head -n 1 | sed 's/[<version> | </version>]//g'`
version=`cat pom.xml | grep "<version>.*</version>" | head -n 1 | sed 's/[<version> | </version> | (SNAPSHOT) | -]//g'`
echo Version is $version

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
zip -r -9 oculus-grid-$version.zip *

ls -1 | grep -v *.zip | xargs rm -rf 

