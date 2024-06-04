# kafka

```shell
docker run -p 9092:9092 apache/kafka:3.7.0

docker exec -it <CONTAINER_NAME> bash

bin/kafka-topics.sh --create --topic user-topic --bootstrap-server localhost:9092

bin/kafka-topics.sh --describe --topic user-topic --bootstrap-server localhost:9092

bin/kafka-console-producer.sh --topic user-topic --bootstrap-server localhost:9092

bin/kafka-console-consumer.sh --topic user-topic --from-beginning --bootstrap-server localhost:9092
```
