# KAFKA

[Official Documentation](https://kafka.apache.org/documentation/)

### Components

1. [Producer](https://github.com/Mnyu/kafka/blob/main/kafka-producer/README.md)
2. [Consumer](https://github.com/Mnyu/kafka/blob/main/kafka-consumer/README.md)
3. Kafka Streams :
   1. [Stateless Processing](https://github.com/Mnyu/kafka/blob/main/kafka-streams-voice-command-parser/README.md)
   2. [Stateful Processing](https://github.com/Mnyu/kafka/blob/main/kafka-streams-bank-transactions/README.md)


### Local Docker CLI operations :
```shell
docker run -p 9092:9092 apache/kafka:3.7.0

docker exec -it <CONTAINER_NAME> bash

cd /opt/kafka

bin/kafka-topics.sh --create --topic user-topic --bootstrap-server localhost:9092

bin/kafka-topics.sh --describe --topic user-topic --bootstrap-server localhost:9092

bin/kafka-console-producer.sh --topic user-topic --bootstrap-server localhost:9092

bin/kafka-console-consumer.sh --topic user-topic --from-beginning --bootstrap-server localhost:9092
```