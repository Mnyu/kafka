# Stateless Processing

### Theory
1. Significance of used configs - StreamsConfig, ProducerConfig and ConsumerConfig?


### Steps to run :
1. Run kafka container and create required topics - ```voice-commands```,```recognized-commands"``` and ```unrecognized-commands```.
2. Run VoiceCommandParserApp
3. Run ParsedCommandConsumer
4. Run VoiceCommandProducer