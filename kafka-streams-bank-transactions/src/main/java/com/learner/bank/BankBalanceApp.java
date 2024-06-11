package com.learner.bank;

import com.learner.bank.config.KafkaStreamsConfig;
import com.learner.bank.topology.BankBalanceTopology;
import java.util.Properties;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.Topology;

public class BankBalanceApp {

  public static void main(String[] args) {
    Properties properties = KafkaStreamsConfig.getConfig();
    Topology topology = BankBalanceTopology.buildTopology();
    KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);

    kafkaStreams.start();
    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
  }

}
