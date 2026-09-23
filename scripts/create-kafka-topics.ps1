# ============================================================
# create-kafka-topics.ps1
# ============================================================

$KafkaContainer = "kafka"

$Topics = @(
    "book.ticket",
    "payment.success"
)

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " Kafka Topic Initialization" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan


# ============================================================
# 1. Check Kafka container
# ============================================================

$KafkaRunning = docker inspect -f "{{.State.Running}}" $KafkaContainer 2>$null

if ($KafkaRunning -ne "true") {

    Write-Host "ERROR: Kafka container is not running." -ForegroundColor Red
    exit 1
}

Write-Host "Kafka container is running." -ForegroundColor Green


# ============================================================
# 2. Wait for Kafka broker
# ============================================================

$MaxRetries = 30

for ($i = 1; $i -le $MaxRetries; $i++) {

    docker exec $KafkaContainer `
        kafka-topics `
        --bootstrap-server localhost:9092 `
        --list 2>$null | Out-Null

    if ($LASTEXITCODE -eq 0) {

        Write-Host "Kafka broker is ready." -ForegroundColor Green
        break
    }

    if ($i -eq $MaxRetries) {

        Write-Host "ERROR: Kafka broker is not ready." -ForegroundColor Red
        Write-Host "Run: docker logs kafka --tail 100" -ForegroundColor Yellow
        exit 1
    }

    Write-Host "Waiting for Kafka... $i/$MaxRetries" `
        -ForegroundColor Yellow

    Start-Sleep -Seconds 2
}


# ============================================================
# 3. Create topics
# ============================================================

foreach ($Topic in $Topics) {

    Write-Host ""
    Write-Host "Checking topic: $Topic" -ForegroundColor Cyan

    docker exec $KafkaContainer `
        kafka-topics `
        --bootstrap-server localhost:9092 `
        --describe `
        --topic $Topic 2>$null | Out-Null

    if ($LASTEXITCODE -eq 0) {

        Write-Host "Already exists: $Topic" `
            -ForegroundColor Green

        continue
    }

    Write-Host "Creating: $Topic" -ForegroundColor Yellow

    docker exec $KafkaContainer `
        kafka-topics `
        --bootstrap-server localhost:9092 `
        --create `
        --topic $Topic `
        --partitions 1 `
        --replication-factor 1

    if ($LASTEXITCODE -ne 0) {

        Write-Host "ERROR creating topic: $Topic" `
            -ForegroundColor Red

        exit 1
    }

    Write-Host "Created successfully: $Topic" `
        -ForegroundColor Green
}


# ============================================================
# 4. Display topics
# ============================================================

Write-Host ""
Write-Host "==============================================" -ForegroundColor Cyan
Write-Host " Current Kafka Topics" -ForegroundColor Cyan
Write-Host "==============================================" -ForegroundColor Cyan

docker exec $KafkaContainer `
    kafka-topics `
    --bootstrap-server localhost:9092 `
    --list

Write-Host ""
Write-Host "Kafka topic initialization completed." `
    -ForegroundColor Green
Write-Host ""