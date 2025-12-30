# Stage 1 : Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .
COPY user-service/pom.xml ./user-service/

COPY user-service/src ./user-service/src

# Compiling:
RUN mvn clean package -DskipTests -f user-service/pom.xml

# Stage 2 : Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/user-service/target/*.jar app.jar

EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Running app
ENTRYPOINT ["java", "-jar", "app.jar"]