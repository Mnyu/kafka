package com.learner.voice.consumer;

import com.learner.voice.VoiceCommandParserTopology;
import com.learner.voice.model.ParsedVoiceCommand;
import com.learner.voice.serdes.JsonSerde;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

public class ParsedCommandConsumer {

  public static void main(String[] args) {
    Map<String, Object> props = Map.of(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
        ConsumerConfig.GROUP_ID_CONFIG, "parsed-command-consumer-1",
        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest"
    );

    try (var commandConsumer = new KafkaConsumer<>(props, Serdes.String().deserializer(), new JsonSerde<>(ParsedVoiceCommand.class).deserializer())) {
      commandConsumer.subscribe(List.of(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC, VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC));
      Runtime.getRuntime().addShutdownHook(new Thread(() -> close(commandConsumer)));

      while (true)  {
        commandConsumer.poll(Duration.ofSeconds(1))
            .forEach(record -> System.out.println("""
                            Topic: %s
                            Result: %s
                            """.formatted(record.topic(), record.value().toString())));
        commandConsumer.commitAsync();
      }
    }
  }

  private static void close(KafkaConsumer<String, ParsedVoiceCommand> commandConsumer) {
    commandConsumer.wakeup();
  }
}
