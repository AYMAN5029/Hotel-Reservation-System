#!/bin/bash

# Hotel Reservation System - Microservices Startup Script
echo "Starting Hotel Reservation System Microservices..."

# Function to check if a service is running on a specific port
check_service() {
    local port=$1
    local service_name=$2
    echo "Checking if $service_name is running on port $port..."
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "✓ $service_name is already running on port $port"
        return 0
    else
        echo "✗ $service_name is not running on port $port"
        return 1
    fi
}

# Function to start a service
start_service() {
    local service_dir=$1
    local service_name=$2
    local port=$3
    
    echo "Starting $service_name..."
    cd "$service_dir"
    
    # Start the service in background
    nohup mvn spring-boot:run > "../logs/${service_name}.log" 2>&1 &
    
    # Wait for service to start
    echo "Waiting for $service_name to start on port $port..."
    local attempts=0
    local max_attempts=30
    
    while [ $attempts -lt $max_attempts ]; do
        if check_service $port "$service_name" >/dev/null 2>&1; then
            echo "✓ $service_name started successfully on port $port"
            break
        fi
        sleep 2
        attempts=$((attempts + 1))
    done
    
    if [ $attempts -eq $max_attempts ]; then
        echo "✗ Failed to start $service_name after $max_attempts attempts"
        return 1
    fi
    
    cd ..
}

# Create logs directory
mkdir -p logs

echo "========================================="
echo "Hotel Reservation System Microservices"
echo "========================================="

# Start services in order
echo "Step 1: Starting Eureka Server (Service Discovery)..."
start_service "eureka-server" "Eureka Server" 8761

echo ""
echo "Step 2: Starting API Gateway..."
start_service "api-gateway" "API Gateway" 8080

echo ""
echo "Step 3: Starting User Service..."
start_service "user-service" "User Service" 8081

echo ""
echo "Step 4: Starting Hotel Service..."
start_service "hotel-service" "Hotel Service" 8082

echo ""
echo "Step 5: Starting Reservation Service..."
start_service "reservation-service" "Reservation Service" 8083

echo ""
echo "Step 6: Starting Payment Service..."
start_service "payment-service" "Payment Service" 8084

echo ""
echo "========================================="
echo "All services started successfully!"
echo "========================================="
echo ""
echo "Service URLs:"
echo "- Eureka Dashboard: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- User Service: http://localhost:8081"
echo "- Hotel Service: http://localhost:8082"
echo "- Reservation Service: http://localhost:8083"
echo "- Payment Service: http://localhost:8084"
echo ""
echo "All API requests should go through the API Gateway (port 8080)"
echo "Example: http://localhost:8080/api/hotels"
echo ""
echo "Logs are available in the 'logs' directory"
echo "To stop all services, run: ./stop-microservices.sh"
