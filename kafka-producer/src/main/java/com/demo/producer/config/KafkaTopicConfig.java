package com.demo.producer.config;

import static com.demo.producer.config.AppConstants.LOCATION_TOPIC;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

  @Bean
  public NewTopic topic() {
    return TopicBuilder.name(LOCATION_TOPIC).partitions(1).replicas(1).build();
  }
}
