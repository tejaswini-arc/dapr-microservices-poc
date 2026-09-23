# ============================================================
# 1. Start Kafka
# ============================================================

Write-Host "Starting Kafka..."
docker compose -f docker-compose-kafka.yml up -d


# ============================================================
# 2. Wait for Kafka
# ============================================================

Write-Host "Waiting for Kafka..."
Start-Sleep -Seconds 5


# ============================================================
# 3. Create Kafka topics
# ============================================================

Write-Host "Creating Kafka topics..."
.\scripts\create-kafka-topics.ps1


# ============================================================
# 4. Start Observability Stack
#    Prometheus + Grafana
# ============================================================

Write-Host "Starting Prometheus and Grafana..."

docker compose -f docker-compose-observability.yml up -d


# ============================================================
# 5. Build Order Service
# ============================================================

Write-Host "Building Order Service..."

Push-Location .\dapr-order-service
mvn clean package -DskipTests
Pop-Location


# ============================================================
# 6. Build Payment Service
# ============================================================

Write-Host "Building Payment Service..."

Push-Location .\dapr-payment-service
mvn clean package -DskipTests
Pop-Location


# ============================================================
# 7. Build Notification Service
# ============================================================

Write-Host "Building Notification Service..."

Push-Location .\dapr-notification-service
mvn clean package -DskipTests
Pop-Location


# ============================================================
# 8. Start Dapr Applications
# ============================================================

Write-Host "Starting Dapr services..."

dapr run -f .


# ============================================================
# 9. URLs
# ============================================================

Write-Host ""
Write-Host "============================================================"
Write-Host "Dapr Microservices POC Started"
Write-Host "============================================================"
Write-Host ""
Write-Host "Kafka:"
Write-Host "  Kafka        : localhost:9092"
Write-Host ""
Write-Host "Services:"
Write-Host "  Order        : http://localhost:8080"
Write-Host "  Payment      : http://localhost:8081"
Write-Host "  Notification : http://localhost:8082"
Write-Host ""
Write-Host "Observability:"
Write-Host "  Prometheus   : http://localhost:9095"
Write-Host "  Grafana      : http://localhost:3000"
Write-Host ""
Write-Host "Dapr Metrics:"
Write-Host "  Order        : http://localhost:9192/metrics"
Write-Host "  Payment      : http://localhost:9191/metrics"
Write-Host "  Notification : http://localhost:9190/metrics"
Write-Host ""
Write-Host "============================================================"