package com.learner.voice.service;

import com.learner.voice.model.ParsedVoiceCommand;

public class LocalTranslateServiceImpl implements TranslateService {

  @Override
  public ParsedVoiceCommand translate(ParsedVoiceCommand original) {
    return ParsedVoiceCommand.builder()
        .id(original.getId())
        .textCommand("call juan")
        .probability(original.getProbability())
        .language(original.getLanguage())
        .build();
  }
}
