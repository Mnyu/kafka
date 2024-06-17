package com.learner.bank.config;

import com.learner.bank.topology.BankBalanceTopology;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class KafkaStreamsConfig {

  @Value("${host.info}")
  private String hostInfo;

  @Value("${kafka.streams.state.dir:/tmp/kafka-streams/bank}") //default values are added using :
  private String kafkaStreamStateDir;

  @Value("${kafka.streams.host.info:localhost:8080}") //default values are added using :
  private String kafkaStreamsHostInfo;

  @Bean
  public Properties kafkaStreamsProps() {
    Properties properties = new Properties();
    properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "bank-balance-app");
    properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    properties.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    properties.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, "0");
    properties.put(StreamsConfig.APPLICATION_SERVER_CONFIG, kafkaStreamsHostInfo);
    properties.put(StreamsConfig.STATE_DIR_CONFIG, kafkaStreamStateDir);
    return properties;
  }

  @Bean
  public KafkaStreams kafkaStreams(@Qualifier("kafkaStreamsProps") Properties properties) {
    Topology topology = BankBalanceTopology.buildTopology();
    
    KafkaStreams kafkaStreams = new KafkaStreams(topology, properties);
    kafkaStreams.start();

    Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));

    return kafkaStreams;
  }

  @Bean
  public HostInfo hostInfo() {
    log.info("Creating host info: {}", hostInfo);
    String[] split = hostInfo.split(":");
    return new HostInfo(split[0], Integer.parseInt(split[1]));
  }
}
