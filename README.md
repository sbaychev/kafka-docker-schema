# Introduction

This is a simplified Kafka-Producer-Consumer plug and play application
It provides for Java based Configurations for Producer-Consumer of a well
defined already Kafka instance(s) (cluster) managed by Zookeeper (cluster)
* with lots of code comments and explanations for currently used configs

docker-compose file takes care for the cluster setup

The topic setup should be predefined as need be, given their partition strategy and replication.
The Kafka Listener is provided a given topic or topic partitions.
Currently we are expecting a topic named: event.e 

# Sample Rest API Usage
curl -d "message=Doctor" http://localhost:8077/publish/

# Requirements

- Java 8 or above (see installation instructions below)
- Docker
- Docker Compose
- JUnit 4

**TODO:**
1. Move to Gradle
2. Move to JUnit 5
3. Move to java 11
4. Move to CustomErrorHandling

**FIXME:** 
1. The confluent schema registry expects the 8081 always to be used for it; need to modify it's server properties for other value
