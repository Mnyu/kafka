package com.demo.producer.service;

import static com.demo.producer.config.AppConstants.LOCATION_TOPIC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaService {

  private final Logger logger = LoggerFactory.getLogger(KafkaService.class);
  private final KafkaTemplate<String,String> kafkaTemplate;

  @Autowired
  public KafkaService(KafkaTemplate<String,String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public boolean produce(String location) {
    kafkaTemplate.send(LOCATION_TOPIC, location);
    logger.info("message produced");
    return true;
  }

}
