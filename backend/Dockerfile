# =============================================================
# Dockerfile — EazyPlanIA Backend (Spring Boot + Java 21)
# Multi-stage: Maven build → JRE runtime
# =============================================================

# ---- Stage 1: Build ----
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

WORKDIR /build
COPY pom.xml .
# Pre-descarga dependencias (capa cacheable si pom.xml no cambia)
RUN mvn dependency:go-offline -B
COPY src/ src/
RUN mvn package -DskipTests -B

# ---- Stage 2: Runtime ----
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
