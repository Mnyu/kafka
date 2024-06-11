# Stateful Processing

### Why Stateful Processing ?
1. When we are handling data, we sometimes need more than an event can give us.
2. We need to relate the events in some way, that's why we need stateful processing.
3. We need to have some sort of memory of what happened before so that we can know now what is happening and why is it happening.

### Why Stateful Processing ?
1. **Perform Aggregations** : We can update the state of an aggregate by applying transformations with memory of previous state.
2. **Understand relationships between events** : We can find patterns in the events by understanding how tey relate to one another.
3. **Enrich data with Joins** : We can join streams with stored data to present more insights in our data.

### State Stores :
In our applications, the state is stored in State Stores. They have the following characteristics:

1. **Embedded** : The state store is by default embedded to the container in which the application is running.
   <br></br>
2. **In memory or disk** :
    1. State store can be in-memory(RAM) or on-disk.
    2. If it is in-memory, it will run faster but will take more time to recover in case of failures where we have reload the previous state from Kafka.
    3. If it is on-disk, the reload time in case if failures will be lesser.
    4. Usually, using the disk is the best approach in these cases.
       <br></br>
3. **Interactive queries** :
    1. We can query these state stores for processed and stored data.
    2. We can implement REST APIs to access this data as well.
       <br></br>
4. **Fault Tolerant** :
    1. The data is not only stored in the state store, but it is also stored in special Kafka topic called ChangeLogs to avoid data loss.
    2. If the data is lost for some reason, the Kafka streams application will load the data from the special topic.
       <br></br>
5. **Key Value Store** : Store is key-value based, so it fits nicely with Kafka's overall design.

### KTable
1. KTables are used to store the state of our application.
2. They are basically key-value based, so it fits nicely with Kafka's overall design(messages and partitioning concepts).
3. Any update that we do with the same key is going to override the previous state, so be very careful and very sure of what we are storing.

### Table-Stream Duality
1. Tables and Streams can be seen as 2 sides of the same coin. Tables can be represented as Streams and Streams can be represented as Tables.
   <br></br>
2. Tables can be represented as Streams of changes. Everytime a change happens in a table, this can be streamed. In a sense, a Stream can be seen as a ChangeLog for a table.
   <br></br>
3. A Stream can be used to reconstruct a Table, by applying the Insert or Update operations on the same key.

<br></br>
### Bank Transactions and Balance Management App

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-streams-bank-transactions/docs/dataModel.jpeg)

![Image](https://github.com/Mnyu/kafka/blob/main/kafka-streams-bank-transactions/docs/Topology.jpeg)

### Steps to run :
1. Run kafka container and create required topics - ```bank-transactions```,```bank-balances"``` and ```rejected-transactions```.