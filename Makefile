APP=conretcreator
APP_VER=2.7
JAVA_VERSION="jdk18"
JAR="${APP}-${JAVA_VERSION}-${APP_VER}.jar"

install: git.clone

run: jc.run
compile: jc.build
build: jc.build jc.run

git.clone:
	git clone https://github.com/dockermgeo/k8s-conretcreator

jc.build:
	mvn clean install
	cp ./target/${APP}.jar ./releases/${JAR}

jc.run:
	java -jar ./releases/${JAR} -h

cleanup:
	rm -vf releases/*.jar
