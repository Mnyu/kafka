package com.learner.bank.repository;

import com.learner.bank.exception.ObjectNotFoundException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyQueryMetadata;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.HostInfo;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

@Slf4j
@AllArgsConstructor
public abstract class GenericKafkaStreamsRepository<K,V> {

  private final Serde<K> keySerde;
  private final Serde<V> valueSerde;
  protected final HostInfo hostInfo;
  private final KafkaStreams kafkaStreams;
  private final String storeName;
  protected String findRemotelyUri;

  protected abstract V findRemotely(K key, HostInfo hostInfo);

  public V find(K key) {
    KeyQueryMetadata keyQueryMetadata = kafkaStreams.queryMetadataForKey(storeName, key, keySerde.serializer());
    HostInfo activeHost = keyQueryMetadata.activeHost();
    if (hostInfo.equals(activeHost)) {
      return findLocally(key);
    }
    return findRemotely(key, activeHost);

  }

  private V findLocally(K key) {
    log.info("Looking for object with key: {}, locally", key);
    return Optional.ofNullable(getStore().get(key))
        .orElseThrow(() -> new ObjectNotFoundException(key, storeName));
  }

  private ReadOnlyKeyValueStore<K, V> getStore() {
    return kafkaStreams.store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));
  }
}
