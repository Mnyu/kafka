# Scaling Kafka Stream Spring Boot App

### Tasks/Requirements

1. Second instance to see how kafka streams scales stateful applications.
2. In this case, we want to see how the data is distributed across the different applications/instances.
3. Then we want to see what happens when we take 1 instance down and how the data is rebalanced once again.

### Why Scale?

1. When the amount of data that our Kafka Streams application handles grows, we might need more computing power to keep up with the desired processing time.
2. That's why Kafka Streams is designed to be able to Scale Horizontally.
3. This means we can add more instances of our app to increase the performance.

### What happens with the data currently stored in local state store?
1. Kafka Streams uses a special Kafka topic to re-balance the application state, so that the keys are distributed across the new horizontal structure.
2. All state stores are updated to reflect the new horizontal structure.

### Current State of the App

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-stream-bank-queries/docs/scale-1.png)

1. There is topic,```bank-transactions```, where we get all the transactions into the application.
2. This topic has 3 partitions, all 3 partitions are assigned to the 1 consumer which is our application.
3. Everything from the 3 partitions goes into the local state store of the same consumer instance as there is only 1 consumer.
4. Another thing that Kafka does is, it will back up everything that we have in our local store into a special topic.
5. This special topic will hold all the data that the local store has, so it can re-balance when we add the 2nd instance.

### Adding a 2nd Instance

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-stream-bank-queries/docs/scale-2.png)

1. When the 2nd instance is added, both the instances will be part of a ```Consumer Group```.
2. This means that the 3 partitions will be shared across the instances within the consumer group. 2 partitions assigned to 1 instance and 1st partition assigned to 2nd instance.
3. This also means that transactions for 2 partitions will end up in the 1st instance state store and transactions from 1 partition will end up in 2nd instance state store.
4. Kafka guarantees that all the messages that have the same key will end up in the same partition in order.
5. Because our bank balance id is the key for our topic, every transaction for the same bank balance is going to go ot the same partition. So, we won't end up with transactions in different instances for the same bank balance.

### Query the State Store that is distributed

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-stream-bank-queries/docs/scale-3.png)

### Why is the data distributed? KTable vs GlobalKTable

1. Kafka Streams distributes the data, os that queries/updates are faster.
2. If we have less data, we can get the results way faster than if we had to query a big data store.
3. But Kafka Streams also provides a different table called ```GlobalKTable```, which is replicated across all instances.
4. This is mostly used for data that is not updated as often, and that we do not expect to grow too much in size.
5. Because when you have a lot of instances, keeping all the instances in sync is a very costly process as we have replicate and stop processing while doing replication.
6. In our Bank Application, we could implement a ```GlobalKTable``` for storing the Type of Bank Account e.g. Savings, Investment, Current, Credit etc.

