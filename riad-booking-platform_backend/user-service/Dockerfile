# ============================================
# User Service Dockerfile
# ============================================
# Pour construire : mvn clean package -DskipTests depuis le répertoire parent
# Puis : docker-compose up -d

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copier le JAR compilé
COPY target/*.jar app.jar

EXPOSE 8081

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8081/actuator/health || exit 1

# Variables d'environnement par défaut
ENV SPRING_PROFILES_ACTIVE=docker
ENV SERVER_PORT=8081

# Running app
ENTRYPOINT ["java", "-jar", "app.jar"]