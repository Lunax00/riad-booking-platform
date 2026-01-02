# Stage 1 : Build
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app

# Copier le POM parent d'abord
COPY ../pom.xml /app/parent-pom.xml

# Copier le POM du service
COPY pom.xml .

# Installer le parent POM localement (en ignorant les erreurs de résolution initiales)
RUN mvn install:install-file -Dfile=/app/parent-pom.xml -DpomFile=/app/parent-pom.xml -DgroupId=com.lunaire -DartifactId=riad-booking-platform_backend -Dversion=0.0.1-SNAPSHOT -Dpackaging=pom || true

# Télécharger les dépendances
RUN mvn dependency:go-offline -B || true

COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 : Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Running app
ENTRYPOINT ["java", "-jar", "app.jar"]