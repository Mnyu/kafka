# Querying State Store

1. Querying the state store in Kafka streams is a great feature.
2. It allows us to see the state that we have aggregated or stored in the state store that Kafka streams provides.
3. We can also create REST APIs on top of it so that we can expose it to different consumers all over the internet.

### Steps to run :
1. Run kafka container and create required topics - ```bank-transactions```,```bank-balances``` and ```rejected-transactions```.
2. Run ```BankTransactionProducer```
3. Run the app ```KafkaStreamBankQueriesApplication```
4. Hit the GET endpoints
```shell
http://localhost:8080/bank-balance/1
http://localhost:8080/bank-balance/2
http://localhost:8080/bank-balance/3
http://localhost:8080/bank-balance/4
```