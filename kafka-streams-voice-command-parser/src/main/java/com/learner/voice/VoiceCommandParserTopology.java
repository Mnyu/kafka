package com.learner.voice;

import com.learner.voice.model.ParsedVoiceCommand;
import com.learner.voice.model.VoiceCommand;
import com.learner.voice.serdes.JsonSerde;
import com.learner.voice.service.SpeechToTextService;
import com.learner.voice.service.TranslateService;
import java.util.Map;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Branched;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Named;
import org.apache.kafka.streams.kstream.Produced;

public class VoiceCommandParserTopology {

  public static final String VOICE_COMMANDS_TOPIC = "voice-commands";
  public static final String RECOGNIZED_COMMANDS_TOPIC = "recognized-commands";
  public static final String UNRECOGNIZED_COMMANDS_TOPIC = "unrecognized-commands";

  private final SpeechToTextService speechToTextService;
  private final TranslateService translateService;
  private final Double certaintyThreshold;

  public VoiceCommandParserTopology(SpeechToTextService speechToTextService,
      TranslateService translateService, Double certaintyThreshold) {
    this.speechToTextService = speechToTextService;
    this.translateService = translateService;
    this.certaintyThreshold = certaintyThreshold;
  }

  public Topology createTopology() {
    StreamsBuilder streamsBuilder = new StreamsBuilder();

    JsonSerde<VoiceCommand> voiceCommandJsonSerde = new JsonSerde<>(VoiceCommand.class);
    JsonSerde<ParsedVoiceCommand> parsedVoiceCommandJsonSerde = new JsonSerde<>(ParsedVoiceCommand.class);

    Map<String, KStream<String, ParsedVoiceCommand>> branches = streamsBuilder.stream(VOICE_COMMANDS_TOPIC, Consumed.with(Serdes.String(), voiceCommandJsonSerde))
        .filter((readOnlyKey, voiceCommand) -> voiceCommand.getAudio().length >= 10)
        .mapValues((readOnlyKey, voiceCommand) -> speechToTextService.speechToText(voiceCommand))
        .split(Named.as("branches-"))
        .branch((key, parsedVoiceCommand) -> parsedVoiceCommand.getProbability() > certaintyThreshold,
            Branched.as("recognized"))
        .defaultBranch(Branched.as("unrecognized"));

    branches.get("branches-unrecognized").to(UNRECOGNIZED_COMMANDS_TOPIC, Produced.with(Serdes.String(), parsedVoiceCommandJsonSerde));

    Map<String, KStream<String, ParsedVoiceCommand>> streamsMap = branches.get("branches-recognized")
        .split(Named.as("language-"))
        .branch((key, parsedVoiceCommand) -> parsedVoiceCommand.getLanguage().startsWith("en"), Branched.as("english"))
        .defaultBranch(Branched.as("non-english"));

    streamsMap.get("language-non-english")
        .mapValues((readOnlyKey, value) -> translateService.translate(value))
        .merge(streamsMap.get("language-english"))
        .to(RECOGNIZED_COMMANDS_TOPIC, Produced.with(Serdes.String(), parsedVoiceCommandJsonSerde));

    return streamsBuilder.build();
  }
}
