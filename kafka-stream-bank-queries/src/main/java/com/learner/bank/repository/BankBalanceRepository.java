package com.learner.bank.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learner.bank.model.BankBalance;
import com.learner.bank.model.JsonSerde;
import com.learner.bank.topology.BankBalanceTopology;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.Response;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.state.HostInfo;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BankBalanceRepository extends GenericKafkaStreamsRepository<Long, BankBalance>{

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  public BankBalanceRepository(HostInfo hostInfo, KafkaStreams kafkaStreams) {
    super(Serdes.Long(), new JsonSerde<>(BankBalance.class), hostInfo, kafkaStreams,
        BankBalanceTopology.BANK_BALANCES_STORE, "/bank-balance/%s");
  }

  @Override
  protected BankBalance findRemotely(Long key, HostInfo hostInfo) {
    log.info("Finding Bank Balance with key {} remotely in host {}", key, hostInfo);
    String url = "http://%s:%d" + findRemotelyUri;
    String urlWithParams = url.formatted(hostInfo.host(), hostInfo.port(), key.toString());
    OkHttpClient okHttpClient = new OkHttpClient();
    Request request = new Builder().url(urlWithParams).build();
    try (Response response = okHttpClient.newCall(request).execute()) {
      return OBJECT_MAPPER.readValue(Objects.requireNonNull(response.body()).string(), BankBalance.class);
    } catch (Exception e) {
      throw new RuntimeException("Exception reading bank balance from remote server");
    }
  }
}
