package com.learner.voice.service;

import com.learner.voice.model.ParsedVoiceCommand;
import com.learner.voice.model.VoiceCommand;

public class LocalSpeechToTextServiceImpl implements SpeechToTextService {

  @Override
  public ParsedVoiceCommand speechToText(VoiceCommand voiceCommand) {
    return switch (voiceCommand.getId()) {
      case "26679943-f55e-4731-986e-c5c5395715de" -> ParsedVoiceCommand.builder()
          .id(voiceCommand.getId())
          .textCommand("call john")
          .probability(0.957)
          .language(voiceCommand.getLanguage())
          .build();
      case "9821f112-ec35-4679-91e7-c558de479bc5" -> ParsedVoiceCommand.builder()
          .id(voiceCommand.getId())
          .textCommand("llamar a juan")
          .probability(0.937)
          .language(voiceCommand.getLanguage())
          .build();
      default -> ParsedVoiceCommand.builder()
          .id(voiceCommand.getId())
          .textCommand("call john")
          .probability(0.37)
          .language(voiceCommand.getLanguage())
          .build();
    };
  }
}
