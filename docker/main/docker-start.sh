
docker run --net=host --rm confluentinc/cp-kafka:latest kafka-topics --create --topic event.t --partitions 4 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

docker run --net=host --rm confluentinc/cp-kafka:latest kafka-topics --describe --topic event.t --zookeeper localhost:32181

docker run --net=host --rm  confluentinc/cp-enterprise-kafka:5.3.1 kafka-topics --create --topic event.t --partitions 4 --replication-factor 1 --if-not-exists --zookeeper localhost:32181