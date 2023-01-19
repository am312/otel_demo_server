#!/bin/bash

export JAVA_HOME=/opt/jdk-17
export JAVA_AGENT="/home/andrew/dev/dd-trace-java/dd-java-agent/build/libs/dd-java-agent-1.4.0-SNAPSHOT.jar"

ls -al $JAVA_AGENT

./gradlew build

$JAVA_HOME/bin/java -javaagent:$JAVA_AGENT \
-Ddd.trace.debug=true \
-Ddatadog.slf4j.simpleLogger.logFile=/tmp/otel_demo_server.log \
-Ddd.profiling.enabled=true \
-XX:FlightRecorderOptions=stackdepth=256 \
-Ddd.logs.injection=true \
-Ddd.trace.sample.rate=1.0 \
-Ddd.appsec.enabled=false \
-Ddd.service=otel_demo_server \
-Ddd.env=dev \
-jar ./build/libs/otel_demo_server-1.0-SNAPSHOT.jar

#$JAVA_11_HOME/bin/java -jar ./build/libs/datadog-demo-1.0-SNAPSHOT.jar
#./gradlew bootrun -Dfile.encoding=UTF-8

