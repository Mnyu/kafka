package com.learner.bank.service;

import com.learner.bank.model.BankBalance;
import com.learner.bank.topology.BankBalanceTopology;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class BankBalanceService {

  private final KafkaStreams kafkaStreams;

  public BankBalance getBankBalanceById(Long bankBalanceId) {
    ReadOnlyKeyValueStore<Long, BankBalance> store = getStore();
    return store.get(bankBalanceId);
  }

  private ReadOnlyKeyValueStore<Long, BankBalance> getStore() {
    return kafkaStreams.store(
        StoreQueryParameters.fromNameAndType(
            BankBalanceTopology.BANK_BALANCES_STORE,
            QueryableStoreTypes.keyValueStore()
        )
    );
  }
}
