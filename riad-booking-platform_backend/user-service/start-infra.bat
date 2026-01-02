@echo off
REM ============================================
REM Script de démarrage du User Service
REM ============================================

echo ============================================
echo    User Service - Demarrage
echo ============================================
echo.

REM Vérifier que Docker est disponible
docker --version >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Docker n'est pas installe ou n'est pas dans le PATH
    echo Veuillez installer Docker Desktop: https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)

REM Vérifier que Docker daemon tourne
docker info >nul 2>&1
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Docker daemon n'est pas demarre
    echo Veuillez lancer Docker Desktop
    pause
    exit /b 1
)

echo [INFO] Docker est disponible
echo.

REM Copier .env si nécessaire
if not exist .env (
    echo [INFO] Creation du fichier .env depuis .env.example
    copy .env.example .env
)

REM Démarrer les services
echo [INFO] Demarrage de PostgreSQL et Keycloak...
docker-compose up -d postgres keycloak

echo.
echo [INFO] Attente du demarrage de PostgreSQL (30 secondes)...
timeout /t 30 /nobreak >nul

echo.
echo [INFO] Verification de l'etat des services...
docker-compose ps

echo.
echo ============================================
echo    Services de base demarres!
echo ============================================
echo.
echo PostgreSQL: localhost:5432
echo Keycloak Admin: http://localhost:8180 (admin/admin)
echo.
echo Pour demarrer le User Service:
echo   1. Via Docker: docker-compose up -d user-service
echo   2. Via Maven:  mvn spring-boot:run
echo.
pause

