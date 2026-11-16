# ==============================================================================
# Multi-stage Dockerfile for StratOps 360 MIS Enterprise Application
# ==============================================================================

# Stage 1: Build application JAR using Maven & Eclipse Temurin JDK 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /workspace

# Cache dependency layer
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build production artifact
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Minimal, secure JRE 21 runtime container
FROM eclipse-temurin:21-jre-alpine
LABEL maintainer="Divyansh Thakur <SRMIST KTR CSE>"
LABEL description="StratOps 360 Management Information Systems Enterprise Platform"

# Create non-privileged system group and user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
WORKDIR /app

# Copy executable jar from builder stage
COPY --from=builder /workspace/target/*.jar app.jar
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser:appgroup

# Expose default HTTP application port
EXPOSE 8080

# Configure container health check against Spring Boot Actuator
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Standard Java runtime options with production profile
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
