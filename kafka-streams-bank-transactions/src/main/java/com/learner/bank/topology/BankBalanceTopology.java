package com.learner.bank.topology;

import com.learner.bank.model.BankBalance;
import com.learner.bank.model.BankTransaction;
import com.learner.bank.model.BankTransaction.BankTransactionState;
import com.learner.bank.model.JsonSerde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

public class BankBalanceTopology {

  public static final String BANK_TRANSACTIONS = "bank-transactions";
  public static final String BANK_BALANCES = "bank-balances";
  public static final String REJECTED_TRANSACTIONS = "rejected-transactions";


  public static Topology buildTopology(){
    StreamsBuilder streamsBuilder = new StreamsBuilder();
    JsonSerde<BankTransaction> bankTransactionJsonSerde = new JsonSerde<>(BankTransaction.class);
    JsonSerde<BankBalance> bankBalanceJsonSerde = new JsonSerde<>(BankBalance.class);

    KStream<Long, BankBalance> bankBalanceKStream =
        streamsBuilder.stream(BANK_TRANSACTIONS, Consumed.with(Serdes.Long(), bankTransactionJsonSerde))
        .groupByKey()
        .aggregate(BankBalance::new,
            (key, bankTransaction, bankBalance) -> bankBalance.process(bankTransaction),
            Materialized.with(Serdes.Long(), bankBalanceJsonSerde))
        .toStream();

    bankBalanceKStream.to(BANK_BALANCES, Produced.with(Serdes.Long(), bankBalanceJsonSerde));

    bankBalanceKStream.mapValues((readOnlyKey, bankBalance) -> bankBalance.getLatestTransaction())
        .filter((key, bankTransaction) -> bankTransaction.getState().equals(BankTransactionState.REJECTED))
        .to(REJECTED_TRANSACTIONS, Produced.with(Serdes.Long(), bankTransactionJsonSerde));

    return streamsBuilder.build();
  }

}
