#!/bin/sh

# This script will be packaged inside the Docker image to run the application.

echo "Starting Spring Boot app"
echo "  Profiles          : [${SPRING_PROFILES_ACTIVE}]"
echo "  Running image     : [${BUILD_TAG}]"
echo "  Built from commit : [${BUILD_COMMIT}]"

# Only one Jar file is expected.
APP_JAR=$(find . -mindepth 1 ! -type l -name "*.jar" | head -n 1)

echo "Using jar [${APP_JAR}]"
echo "Using JAVA_OPTIONS [${JAVA_OPTIONS}]"

java ${JAVA_OPTIONS} -jar "${APP_JAR}"
