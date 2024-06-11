package com.learner.voice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.learner.voice.model.ParsedVoiceCommand;
import com.learner.voice.model.VoiceCommand;
import com.learner.voice.serdes.JsonSerde;
import com.learner.voice.service.SpeechToTextService;
import com.learner.voice.service.TranslateService;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TestOutputTopic;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VoiceCommandParserTopologyTest {

  @Mock
  private SpeechToTextService speechToTextService;
  @Mock
  private TranslateService translateService;

  private TopologyTestDriver topologyTestDriver; // TopologyTestDriver simulates being kafka for our topology
  private TestInputTopic<String, VoiceCommand> voiceCommandsInputTopic;
  private TestOutputTopic<String, ParsedVoiceCommand> recognizedCommandsOutputTopic;
  private TestOutputTopic<String, ParsedVoiceCommand> unrecognizedCommandsOutputTopic;

  @BeforeEach
  void setUp() {
    VoiceCommandParserTopology voiceCommandParserTopology = new VoiceCommandParserTopology(speechToTextService, translateService, 0.90);
    Topology topology = voiceCommandParserTopology.createTopology();
    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "test");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
    topologyTestDriver = new TopologyTestDriver(topology, properties);

    // TopologyTestDriver simulates being kafka for our topology
    Serializer<String> keySerializer = Serdes.String().serializer();
    JsonSerde<VoiceCommand> voiceCommandJsonSerde = new JsonSerde<>(VoiceCommand.class);
    Serializer<VoiceCommand> valueSerializer = voiceCommandJsonSerde.serializer();
    voiceCommandsInputTopic = topologyTestDriver.createInputTopic(VoiceCommandParserTopology.VOICE_COMMANDS_TOPIC, keySerializer, valueSerializer);

    Deserializer<String> keyDeserializer = Serdes.String().deserializer();
    JsonSerde<ParsedVoiceCommand> parsedVoiceCommandJsonSerde = new JsonSerde<>(ParsedVoiceCommand.class);
    Deserializer<ParsedVoiceCommand> valueDeserializer = parsedVoiceCommandJsonSerde.deserializer();
    recognizedCommandsOutputTopic = topologyTestDriver.createOutputTopic(VoiceCommandParserTopology.RECOGNIZED_COMMANDS_TOPIC, keyDeserializer, valueDeserializer);
    unrecognizedCommandsOutputTopic = topologyTestDriver.createOutputTopic(VoiceCommandParserTopology.UNRECOGNIZED_COMMANDS_TOPIC, keyDeserializer, valueDeserializer);
  }

  @Test
  @DisplayName("Given an English voice command, when processed correctly then we receive a ParsedCommand in the recognized-commands topic")
  void testScenario1() {
    // Preconditions (Given)
    byte[] randomBytes = new byte[20];
    new Random().nextBytes(randomBytes);
    VoiceCommand voiceCommand = VoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audio(randomBytes)
        .audioCodec("FLAC")
        .language("en-US")
        .build();
    ParsedVoiceCommand newParsedVoiceCommand = ParsedVoiceCommand.builder()
        .id(voiceCommand.getId()).textCommand("call john")
        .language("en-US")
        .probability(0.98)
        .build();
    BDDMockito.given(speechToTextService.speechToText(voiceCommand)).willReturn(newParsedVoiceCommand);

    // Actions (When)
    voiceCommandsInputTopic.pipeInput(voiceCommand);

    // Verifications (Then)
    ParsedVoiceCommand parsedVoiceCommand = recognizedCommandsOutputTopic.readRecord().value();
    assertEquals(voiceCommand.getId(), parsedVoiceCommand.getId());
    assertEquals("call john", parsedVoiceCommand.getTextCommand());
  }

  @Test
  @DisplayName("Given a non-English voice command, when processed correctly then we receive a ParsedCommand in the recognized-commands topic")
  void testScenario2() {
    // Preconditions (Given)
    byte[] randomBytes = new byte[20];
    new Random().nextBytes(randomBytes);
    VoiceCommand voiceCommand = VoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audio(randomBytes)
        .audioCodec("FLAC")
        .language("es-AR")
        .build();
    ParsedVoiceCommand newParsedVoiceCommand = ParsedVoiceCommand.builder()
        .id(voiceCommand.getId()).textCommand("llamar a juan")
        .language("es-AR")
        .probability(0.98)
        .build();
    ParsedVoiceCommand translatedParsedVoiceCommand = ParsedVoiceCommand.builder()
        .id(voiceCommand.getId()).textCommand("call juan")
        .language("en-US")
        .probability(0.98)
        .build();
    BDDMockito.given(speechToTextService.speechToText(voiceCommand)).willReturn(newParsedVoiceCommand);
    BDDMockito.given(translateService.translate(newParsedVoiceCommand)).willReturn(translatedParsedVoiceCommand);

    // Actions (When)
    voiceCommandsInputTopic.pipeInput(voiceCommand);

    // Verifications (Then)
    ParsedVoiceCommand parsedVoiceCommand = recognizedCommandsOutputTopic.readRecord().value();
    assertEquals(voiceCommand.getId(), parsedVoiceCommand.getId());
    assertEquals("call juan", parsedVoiceCommand.getTextCommand());
  }

  @Test
  @DisplayName("Given a non-recognizable voice command, when processed correctly then we receive a ParsedCommand in the unrecognized-commands topic")
  void testScenario3() {
    // Preconditions (Given)
    byte[] randomBytes = new byte[20];
    new Random().nextBytes(randomBytes);
    VoiceCommand voiceCommand = VoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audio(randomBytes)
        .audioCodec("FLAC")
        .language("en-US")
        .build();
    ParsedVoiceCommand newParsedVoiceCommand = ParsedVoiceCommand.builder()
        .id(voiceCommand.getId()).textCommand("call john")
        .language("en-US")
        .probability(0.75)
        .build();
    BDDMockito.given(speechToTextService.speechToText(voiceCommand)).willReturn(newParsedVoiceCommand);

    // Actions (When)
    voiceCommandsInputTopic.pipeInput(voiceCommand);

    // Verifications (Then)
    ParsedVoiceCommand parsedVoiceCommand = unrecognizedCommandsOutputTopic.readRecord().value();
    assertEquals(voiceCommand.getId(), parsedVoiceCommand.getId());
    assertTrue(recognizedCommandsOutputTopic.isEmpty());
    verify(translateService, never()).translate(any(ParsedVoiceCommand.class));
  }

  @Test
  @DisplayName("Given voice command is too short(less than 10 bytes), when processed correctly then we don't receive any command in any of the output topics")
  void testScenario4() {
    // Preconditions (Given)
    byte[] randomBytes = new byte[9];
    new Random().nextBytes(randomBytes);
    VoiceCommand voiceCommand = VoiceCommand.builder()
        .id(UUID.randomUUID().toString())
        .audio(randomBytes)
        .audioCodec("FLAC")
        .language("en-US")
        .build();

    // Actions (When)
    voiceCommandsInputTopic.pipeInput(voiceCommand);

    // Verifications (Then)
    assertTrue(recognizedCommandsOutputTopic.isEmpty());
    assertTrue(unrecognizedCommandsOutputTopic.isEmpty());
    verify(speechToTextService, never()).speechToText(any(VoiceCommand.class));
    verify(translateService, never()).translate(any(ParsedVoiceCommand.class));
  }

}