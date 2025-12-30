
# Stage 1 : Build
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /build

COPY pom.xml .

COPY catalog-service/pom.xml ./catalog-service/
COPY catalog-service/src ./catalog-service/src

RUN mvn clean package -DskipTests -f {SERVICE_NAME}/pom.xml

# Stage 2 : Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /build/catalog-service/target/*.jar app.jar

EXPOSE 8082

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:{PORT}/actuator/health || exit 1

# Running
ENTRYPOINT ["java", "-jar", "app.jar"]