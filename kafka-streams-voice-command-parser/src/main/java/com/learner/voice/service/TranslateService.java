package com.learner.voice.service;

import com.learner.voice.model.ParsedVoiceCommand;

public interface TranslateService {

  ParsedVoiceCommand translate(ParsedVoiceCommand original);

}
