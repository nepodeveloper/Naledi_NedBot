# Local Development Setup

This project includes Docker support for local development with hot reload and debugging capabilities.

## Prerequisites

- Docker and Docker Compose
- Java 17 (for local development without Docker)
- Maven (optional, wrapper included)

## Health Checks

The application includes Spring Boot Actuator for health monitoring:
- Health endpoint: `http://localhost:8080/actuator/health`
- Docker healthcheck configured in both Dockerfile and docker-compose.yml

## Development with Docker

1. Start the application:
```bash
docker-compose up
```

2. Development features:
   - Hot reload enabled through volume mounts
   - Remote debugging on port 5005
   - Maven cache persisted between runs
   - Health checks enabled

3. Debug in VS Code:
   ```jsonc
   // .vscode/launch.json
   {
     "version": "0.2.0",
     "configurations": [
       {
         "type": "java",
         "name": "Debug (Attach)",
         "request": "attach",
         "hostName": "localhost",
         "port": 5005
       }
     ]
   }
   ```

## Local Development without Docker

1. Start the application:
```bash
./mvnw spring-boot:run
```

2. Run with debug options:
```bash
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

## Environment Variables

- `SPRING_PROFILES_ACTIVE`: Set to 'dev' for development
- `SERVER_PORT`: Default 8080
- `JAVA_OPTS`: JVM options, includes debug settings in dev

## Health Check Endpoints

- Application health: `http://localhost:8080/actuator/health`
- Detailed health info: `http://localhost:8080/actuator/health/details` (requires configuration)

## Docker Commands

```bash
# Build the image
docker-compose build

# Start services
docker-compose up

# Start in background
docker-compose up -d

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Rebuild and start
docker-compose up --build
```

## Troubleshooting

1. Permission issues:
```bash
chmod +x mvnw
```

2. Maven cache issues:
```bash
docker-compose down -v  # Remove volumes
```

3. Health check failing:
```bash
# Check logs
docker-compose logs app
# Increase start period in docker-compose.yml if needed
```