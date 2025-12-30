
# Stage 1 : Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

COPY api-gateway/pom.xml ./api-gateway/
COPY api-gateway/src ./api-gateway/src

RUN mvn clean package -DskipTests -f {SERVICE_NAME}/pom.xml

# Stage 2 : Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/api-gateway/target/*.jar app.jar

EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:{PORT}/actuator/health || exit 1

# Running
ENTRYPOINT ["java", "-jar", "app.jar"]