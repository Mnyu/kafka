package com.demo.consumer.config;

import static com.demo.consumer.config.AppConstants.GROUP_ID;
import static com.demo.consumer.config.AppConstants.LOCATION_TOPIC;

import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;

@Configuration
public class KafkaConfig {

  @KafkaListener(topics = LOCATION_TOPIC, groupId = GROUP_ID)
  public void consume(String value) {
    System.out.println(value);
  }
}
