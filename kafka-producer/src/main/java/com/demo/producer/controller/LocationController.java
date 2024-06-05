package com.demo.producer.controller;

import com.demo.producer.service.KafkaService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/location")
public class LocationController {

  private KafkaService kafkaService;

  @Autowired
  public LocationController(KafkaService kafkaService) {
    this.kafkaService = kafkaService;
  }

  @PostMapping("/update")
  public ResponseEntity<?> updateLocation() {
    kafkaService.produce("( " + Math.round(Math.random() * 100) + ", "+ Math.round(Math.random() * 100) +" )");
    return new ResponseEntity<>(Map.of("message", "Location updated!"), HttpStatus.OK);
  }
}
