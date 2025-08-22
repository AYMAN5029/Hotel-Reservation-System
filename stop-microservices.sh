#!/bin/bash

# Hotel Reservation System - Microservices Stop Script
echo "Stopping Hotel Reservation System Microservices..."

# Function to stop a service running on a specific port
stop_service() {
    local port=$1
    local service_name=$2
    
    echo "Stopping $service_name on port $port..."
    
    # Find process ID running on the port
    local pid=$(lsof -ti:$port)
    
    if [ -n "$pid" ]; then
        echo "Found $service_name running with PID: $pid"
        kill -TERM $pid
        
        # Wait for graceful shutdown
        sleep 3
        
        # Check if process is still running
        if kill -0 $pid 2>/dev/null; then
            echo "Force killing $service_name..."
            kill -KILL $pid
        fi
        
        echo "✓ $service_name stopped successfully"
    else
        echo "✗ $service_name is not running on port $port"
    fi
}

echo "========================================="
echo "Stopping Hotel Reservation Microservices"
echo "========================================="

# Stop services in reverse order
echo "Stopping Payment Service..."
stop_service 8084 "Payment Service"

echo "Stopping Reservation Service..."
stop_service 8083 "Reservation Service"

echo "Stopping Hotel Service..."
stop_service 8082 "Hotel Service"

echo "Stopping User Service..."
stop_service 8081 "User Service"

echo "Stopping API Gateway..."
stop_service 8080 "API Gateway"

echo "Stopping Eureka Server..."
stop_service 8761 "Eureka Server"

echo ""
echo "========================================="
echo "All microservices stopped successfully!"
echo "========================================="

# Clean up log files if they exist
if [ -d "logs" ]; then
    echo "Cleaning up log files..."
    rm -rf logs/*.log
    echo "✓ Log files cleaned up"
fi

echo "All services have been stopped."
