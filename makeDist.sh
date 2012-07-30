set -e

versionInPom=`cat pom.xml | grep "<version>.*</version>" | head -n 1 | sed 's/[<version> | </version>]//g'`
version=`cat pom.xml | grep "<version>.*</version>" | head -n 1 | sed 's/[<version> | </version> | (SNAPSHOT) | -]//g'`
echo Version is $version
mkdir -p dist
rm -rf dist/*

mkdir -p dist/bin/storage
mkdir -p dist/sources
mkdir -p target
rm -rf target/*
mvn assembly:assembly -DskipTests=true
cp target/oculus-grid-jar-with-dependencies.jar dist/bin/oculus-grid.jar
cp grid.agent.tags.xml dist/bin/.
cp scripts/*.properties dist/bin/.
cp scripts/run-server.sh dist/bin/.
cp scripts/run-agent.sh dist/bin/.
cp LICENSE dist/bin/.
cp README dist/bin/.

cp -r src dist/sources/src
cp pom.xml dist/sources/.
cp scripts/*.properties dist/sources/.
cp LICENSE dist/sources/.
cp README dist/sources/.


cd dist/bin
zip -r -9 ../oculus-grid-$version-bin.zip *

cd ../sources
zip -r -9 ../oculus-grid-$version-sources.zip *

cd ..
rm -rf bin/ sources/

