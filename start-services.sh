#!/bin/bash

# Order Management System - Service Management Script

echo "🚀 Order Management System - Service Manager"
echo "=============================================="

# Function to check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo "❌ Docker is not running. Please start Docker first."
        exit 1
    fi
    echo "✅ Docker is running"
}

# Function to start all services
start_all() {
    echo "🔄 Starting all services..."
    docker-compose up -d --build
    echo "✅ All services started successfully!"
    echo ""
    echo "📊 Service Status:"
    docker-compose ps
    echo ""
    echo "🌐 Service URLs:"
    echo "   Order Service: http://localhost:8087/swagger-ui.html"
    echo "   Payment Service: http://localhost:8082/swagger-ui.html"
    echo "   Notification Service: http://localhost:8083/swagger-ui.html"
    echo "   Kafka UI: http://localhost:8080"
}

# Function to stop all services
stop_all() {
    echo "🛑 Stopping all services..."
    docker-compose down
    echo "✅ All services stopped successfully!"
}

# Function to restart all services
restart_all() {
    echo "🔄 Restarting all services..."
    docker-compose down
    docker-compose up -d --build
    echo "✅ All services restarted successfully!"
}

# Function to view logs
view_logs() {
    echo "📋 Viewing logs for all services..."
    docker-compose logs -f
}

# Function to view specific service logs
view_service_logs() {
    echo "📋 Available services:"
    echo "1. order-service"
    echo "2. payment-service"
    echo "3. notification-service"
    echo "4. kafka"
    echo "5. mysql"
    echo "6. zookeeper"
    echo ""
    read -p "Enter service name to view logs: " service_name
    if [ -n "$service_name" ]; then
        echo "📋 Viewing logs for $service_name..."
        docker-compose logs -f "$service_name"
    else
        echo "❌ No service name provided"
    fi
}

# Function to check service status
check_status() {
    echo "📊 Service Status:"
    docker-compose ps
    echo ""
    echo "🔍 Health Check URLs:"
    echo "   Order Service: http://localhost:8087/actuator/health"
    echo "   Payment Service: http://localhost:8082/actuator/health"
    echo "   Notification Service: http://localhost:8083/actuator/health"
}

# Function to clean up (remove containers, volumes, networks)
cleanup() {
    echo "🧹 Cleaning up Docker resources..."
    read -p "Are you sure you want to remove all containers, volumes, and networks? (y/N): " confirm
    if [[ $confirm == [yY] || $confirm == [yY][eE][sS] ]]; then
        docker-compose down -v --remove-orphans
        docker system prune -f
        echo "✅ Cleanup completed!"
    else
        echo "❌ Cleanup cancelled"
    fi
}

# Function to show help
show_help() {
    echo "📖 Usage: $0 [OPTION]"
    echo ""
    echo "Options:"
    echo "  start       Start all services"
    echo "  stop        Stop all services"
    echo "  restart     Restart all services"
    echo "  status      Check service status"
    echo "  logs        View all service logs"
    echo "  service-logs View specific service logs"
    echo "  cleanup     Clean up Docker resources"
    echo "  help        Show this help message"
    echo ""
    echo "Examples:"
    echo "  $0 start        # Start all services"
    echo "  $0 stop         # Stop all services"
    echo "  $0 status       # Check status"
    echo "  $0 logs         # View logs"
}

# Main script logic
main() {
    check_docker
    
    case "${1:-}" in
        "start")
            start_all
            ;;
        "stop")
            stop_all
            ;;
        "restart")
            restart_all
            ;;
        "status")
            check_status
            ;;
        "logs")
            view_logs
            ;;
        "service-logs")
            view_service_logs
            ;;
        "cleanup")
            cleanup
            ;;
        "help"|"-h"|"--help")
            show_help
            ;;
        "")
            echo "🤔 No option specified. Use '$0 help' for usage information."
            echo ""
            show_help
            ;;
        *)
            echo "❌ Unknown option: $1"
            echo "Use '$0 help' for usage information."
            exit 1
            ;;
    esac
}

# Run main function with all arguments
main "$@"
