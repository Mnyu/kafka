echo "Waiting for Kafka to come online..."

# cub kafka-ready -b kafka:9092 1 20

sleep 30s

/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --topic bank-transactions --replication-factor 1 --partitions 4 --create

/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --topic bank-balances --replication-factor 1 --partitions 4 --create

/opt/kafka/bin/kafka-topics.sh --bootstrap-server kafka:9092 --topic rejected-transactions --replication-factor 1 --partitions 4 --create

sleep infinity

