# Stateless Processing

### Theory
1. Significance of used configs - StreamsConfig, ProducerConfig and ConsumerConfig?

### Repartition in Kafka Streams
![Image](https://github.com/Mnyu/kafka/blob/main/kafka-streams-voice-command-parser/docs/repartition-in-kafka-streams.png)

### Voice Command Parser App

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-streams-voice-command-parser/docs/appDiagram.png)

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-streams-voice-command-parser/docs/topology.png)

### Steps to run :
1. Run kafka container and create required topics - ```voice-commands```,```recognized-commands"``` and ```unrecognized-commands```.
2. Run VoiceCommandParserApp
3. Run ParsedCommandConsumer
4. Run VoiceCommandProducer

