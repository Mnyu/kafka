package com.learner.voice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ParsedVoiceCommand {

  private String id;
  private String textCommand;
  private String audioCodec;
  private String language;
  private Double probability;
}
