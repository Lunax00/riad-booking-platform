@echo off
REM ============================================
REM Script de démarrage complet (avec User Service)
REM ============================================

echo ============================================
echo    User Service - Demarrage Complet
echo ============================================
echo.

REM Vérifier que Docker est disponible
docker --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Docker n'est pas installe ou n'est pas dans le PATH
    pause
    exit /b 1
)

docker info >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Docker daemon n'est pas demarre. Lancez Docker Desktop.
    pause
    exit /b 1
)

REM Copier .env si nécessaire
if not exist .env (
    copy .env.example .env
)

REM Build du JAR si nécessaire
if not exist target\user-service-0.0.1-SNAPSHOT.jar (
    echo [INFO] Compilation du projet...
    cd ..
    call mvn clean package -DskipTests -pl user-service -am
    cd user-service
)

REM Démarrer tous les services
echo [INFO] Demarrage de tous les services...
docker-compose up -d

echo.
echo [INFO] Attente du demarrage (60 secondes)...
timeout /t 60 /nobreak >nul

echo.
echo [INFO] Verification de l'etat des services...
docker-compose ps

echo.
echo ============================================
echo    Tous les services sont demarres!
echo ============================================
echo.
echo User Service API: http://localhost:8081
echo Health Check:     http://localhost:8081/actuator/health
echo Keycloak Admin:   http://localhost:8180 (admin/admin)
echo PostgreSQL:       localhost:5432
echo.
echo Pour voir les logs: docker-compose logs -f user-service
echo Pour arreter:       docker-compose down
echo.
pause

