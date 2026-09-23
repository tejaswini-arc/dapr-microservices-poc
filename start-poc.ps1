docker compose -f docker-compose-kafka.yml up -d

# wait for Kafka

.\scripts\create-kafka-topics.ps1

Push-Location .\dapr-order-service
mvn clean package -DskipTests
Pop-Location

Push-Location .\dapr-payment-service
mvn clean package -DskipTests
Pop-Location

Push-Location .\dapr-notification-service
mvn clean package -DskipTests
Pop-Location

dapr run -f .