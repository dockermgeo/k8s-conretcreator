APP=conretcreator
APP_VER=2.4
JAVA_VERSION="jdk18"
JAR="${APP}-${JAVA_VERSION}-${APP_VER}.jar"

install: jc.run

run: jc.run

compile: jc.build

build: jc.build jc.run

jc.build:
	mvn clean install
	cp ./target/${APP}.jar ./releases/${JAR}

jc.run:
	java -jar ./releases/${JAR} -h

cleanup:
	rm -vf releases/*.jar
