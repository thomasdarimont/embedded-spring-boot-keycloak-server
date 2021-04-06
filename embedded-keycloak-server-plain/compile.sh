#!/usr/bin/env bash

echo "This is WIP"
exit 0

echo "[-->] Detect artifactId from pom.xml"
ARTIFACT=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${project.artifactId}' \
--non-recursive \
exec:exec);
echo "artifactId is '$ARTIFACT'"

echo "[-->] Detect artifact version from pom.xml"
VERSION=$(mvn -q \
  -Dexec.executable=echo \
  -Dexec.args='${project.version}' \
  --non-recursive \
  exec:exec);
echo "artifact version is '$VERSION'"

echo "[-->] Detect Spring Boot Main class ('start-class') from pom.xml"
MAINCLASS=$(mvn -q \
-Dexec.executable=echo \
-Dexec.args='${start-class}' \
--non-recursive \
exec:exec);
echo "Spring Boot Main class ('start-class') is '$MAINCLASS'"

echo "[-->] Cleaning target directory & creating new one"
rm -rf target
mkdir -p target/native-image

echo "[-->] Build Spring Boot App with mvn package"
mvn -DskipTests package

echo "[-->] Expanding the Spring Boot fat jar"
JAR="$ARTIFACT-$VERSION.jar"
cd target/native-image
jar -xvf ../$JAR >/dev/null 2>&1
cp -R META-INF BOOT-INF/classes

echo "[-->] Set the classpath to the contents of the fat jar & add the Spring Graal AutomaticFeature to the classpath"
LIBPATH=`find BOOT-INF/lib | tr '\n' ':'`
MAVEN_REPO_HOME=~/.m2/repository
FEATURE=$MAVEN_REPO_HOME/org/springframework/experimental/spring-graal-native/0.6.1.RELEASE/spring-graal-native-0.6.1.RELEASE.jar
CP=BOOT-INF/classes:$LIBPATH:$FEATURE

GRAALVM_VERSION=`$GRAALVM_HOME/bin/native-image --version`
echo "[-->] Compiling Spring Boot App '$ARTIFACT' with $GRAALVM_VERSION"
time $GRAALVM_HOME/bin/native-image \
  --no-server -J-Xmx26G \
  --no-fallback \
  --report-unsupported-elements-at-runtime \
  --initialize-at-build-time \
  --initialize-at-run-time=io.undertow.server.protocol.ajp.AjpServerRequestConduit \
  --initialize-at-run-time=io.undertow.server.protocol.ajp.AjpServerResponseConduit \
  --initialize-at-run-time=org.xnio.channels.Channels \
  -H:+TraceClassInitialization \
  -H:Name=$ARTIFACT \
  -H:+ReportExceptionStackTraces \
  -Dspring.graal.remove-unused-autoconfig=true \
  -Dspring.graal.remove-yaml-support=false \
  -Dspring.graal.missing-selector-hints=warning \
  -cp $CP $MAINCLASS;
