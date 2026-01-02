#!/bin/bash
set -e

# Ce script crée la base de données keycloak_db en plus de la base par défaut

echo "🔧 Création de la base de données keycloak_db..."

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
    CREATE DATABASE keycloak_db;
    GRANT ALL PRIVILEGES ON DATABASE keycloak_db TO $POSTGRES_USER;
EOSQL

echo "✅ Base de données keycloak_db créée avec succès"

