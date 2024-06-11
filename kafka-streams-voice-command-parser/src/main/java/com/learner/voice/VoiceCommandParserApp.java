package com.learner.voice;

import com.learner.voice.config.KafkaStreamsConfig;
import com.learner.voice.service.LocalSpeechToTextServiceImpl;
import com.learner.voice.service.LocalTranslateServiceImpl;
import org.apache.kafka.streams.KafkaStreams;

public class VoiceCommandParserApp {

  public static void main(String[] args) {

    LocalSpeechToTextServiceImpl speechToTextService = new LocalSpeechToTextServiceImpl();
    LocalTranslateServiceImpl translateService = new LocalTranslateServiceImpl();
    VoiceCommandParserTopology voiceCommandParserTopology = new VoiceCommandParserTopology(speechToTextService, translateService, 0.90);

    KafkaStreamsConfig kafkaStreamsConfig = new KafkaStreamsConfig();
    KafkaStreams kafkaStreams = new KafkaStreams(voiceCommandParserTopology.createTopology(), kafkaStreamsConfig.createConfig());

    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }
}
