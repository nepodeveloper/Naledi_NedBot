# Stage 1: build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven wrapper, pom.xml and src
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src ./src

# Give execute permission to mvnw
RUN chmod +x mvnw

# Build the project (skip tests)
RUN ./mvnw -q -DskipTests package

# Stage 2: runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port your Spring Boot app runs on
EXPOSE 8080

# Run the jar
ENTRYPOINT ["java","-jar","app.jar"]
