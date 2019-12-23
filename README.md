# Introduction

This is a simplified Kafka-Producer-Consumer plug and play application
It provides for Java based Configurations for Producer-Consumer of a well
defined already Kafka instance(s) (cluster) managed by Zookeeper (cluster)

docker-compose file takes care for the cluster setup

The topic setup should be predefined as need be, given their partition strategy and replication.
The Kafka Listener is provided a given topic or topic partitions.
Currently we are expecting a topic named: event.e 


curl -d "message=Doctor" http://localhost:8080/publish/

-----------------------------------------
&#x290B;    ****DEFAULT****       &#x290B;
-----------------------------------------
# Requirements

- Java 8 or above (see installation instructions below)
- Docker
- Docker Compose
- JUnit 4

TODO: Move to Gradle, move to JUnit 5, move to java 11
TODO: The confluent schema registry expects the 8081 always to be used for it; need to modify it's server properties for other value

# Project structure

    .
    ├── bin                         : Shell scripts for installing, running and building.
    ├── bitbucket-pipelines.yml     : Bitbucket Pipelines definition.
    ├── build.gradle                : Gradle build script.
    ├── docker                      : Docker-related files for testing, building and deploying the service.
    │   ├── main                    : Production Docker files.
    │   └── test                    : Test Docker files.
    ├── gradle                      : Gradle wrapper.
    ├── gradle.properties           : Local properties for Gradle.
    ├── gradlew                     : Gradle Wrapper script (Unix).
    ├── libraries.gradle            : Contains all the dependencies versions.
    ├── README.md                   : This file you are reading right now ;).
    ├── settings.gradle             : Project's Gradle settings.
    └── src                         : Sources.
        ├── main                    : Main sources.
        └── test                    : Test sources.

# Installation instructions for Java

    NOTE: Always prefer to install Java through your package manager. Use this section only if your system
    does not offer packages for Java 11 yet.

With Java releasing every six months, it's likely that distributions don't keep up. We can however
install several JDKs on se same machine. Just make sure you're using the correct one for each project. 

The script `bin/install-java-11.sh` will handle the installation of Java 11 for you. It will download the
JDK distribution and unpack it at a default location. It will also create a link with a memorable name, so that
the JDK can be updated without having to reconfigure IDEs and other tools each time because of directory names.

## Opening the project in an IDE (for IntelliJ only)

Before opening the project, go to `File -> Project Structure` and add a Java 11 compatible JDK.

When opening the project with IntelliJ, make sure that the Gradle JVM option in the "Open dialog" points to the
correct Java 11 JDK. Finally, set the project SDK and source level correctly for Java 11 in `File -> Project Structure`.

# Building

For running on a terminal, make sure you use the right Java VM (Java 11 or above). You might need to define
`JAVA_HOME` before building with Gradle Wrapper:

    $ export JAVA_HOME=/opt/java/java-11
    $ ./gradlew test clean build -x test

Or

    $ JAVA_HOME=/opt/java/java-11 ./gradlew clean build -x test

Or you may pass the Java home directory directly to the Gradle Wrapper:

    $ ./gradlew -Dorg.gradle.java.home=/opt/java/java-11 clean build -x test

# Testing locally

For testing locally


The run the tests the usual way with the Gradle wrapper.


-----------------


mvn clean compile