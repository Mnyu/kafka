package com.learner.voice.service;

import com.learner.voice.model.ParsedVoiceCommand;
import com.learner.voice.model.VoiceCommand;

public interface SpeechToTextService {

  ParsedVoiceCommand speechToText(VoiceCommand voiceCommand);

}
