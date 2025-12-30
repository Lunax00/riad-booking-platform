# 🏨 Riad Micro Service Booking Backend

Architecture microservices pour un système de réservation de riads au Maroc.

## 📋 Table des matières

- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Démarrage](#démarrage)
- [Accès aux services](#accès-aux-services)
- [Troubleshooting](#troubleshooting)

---

## 🏗️ Architecture

Ce projet implémente une architecture microservices avec :

- **API Gateway** : Point d'entrée unique (port 8080)
- **User Service** : Gestion des utilisateurs (port 8081)
- **Catalog Service** : Catalogue des riads (port 8082)
- **Search Service** : Recherche avancée (port 8083)
- **Reservation Service** : Gestion des réservations (port 8084)
- **Payment Service** : Gestion des paiements (port 8085)

**Infrastructure** :
- **PostgreSQL** : Base de données
- **RabbitMQ** : Message broker
- **Keycloak** : Authentification OAuth2/OIDC
- **Elasticsearch** : Moteur de recherche
- **Prometheus + Grafana** : Monitoring

---

## ✅ Prérequis

Vous devez installer :

1. **Java 17+**
   ```bash
   java -version
   # Doit afficher java version "17" ou plus
   ```

2. **Maven 3.8+**
   ```bash
   mvn -version
   ```

3. **Docker + Docker Compose**
   ```bash
   docker --version
   docker-compose --version
   ```

4. **Git**
   ```bash
   git --version
   ```

---

## 🚀 Installation

### Étape 1 : Cloner ou créer le projet

```bash
cd ~/projects
git clone <your-repo-url> riad-micro-service-booking-backend
cd riad-micro-service-booking-backend
```

### Étape 2 : Vérifier la structure

```bash
# Vérifier que tous les répertoires existent
ls -la
# Doit afficher : api-gateway/, user-service/, catalog-service/, etc.
```

### Étape 3 : Compiler le projet parent

```bash
mvn clean install
# Cette commande compile le POM parent
```

---

## 🏃 Démarrage

### Démarrer l'infrastructure avec Docker Compose

```bash
# Vérifier que Docker est en cours d'exécution
docker ps

# Démarrer tous les services
docker-compose up -d

# Vérifier que tous les containers sont en cours d'exécution
docker-compose ps
```

**Attendre 30-60 secondes pour que tous les services se lancent.**

### Démarrer les microservices Spring Boot

Chaque service doit être démarré dans un terminal séparé :

**Terminal 1 - API Gateway** :
```bash
cd api-gateway
mvn spring-boot:run
# Doit afficher : "Started GatewayApplication in X seconds"
```

**Terminal 2 - User Service** :
```bash
cd user-service
mvn spring-boot:run
```

**Terminal 3 - Catalog Service** :
```bash
cd catalog-service
mvn spring-boot:run
```

**Terminal 4 - Search Service** :
```bash
cd search-service
mvn spring-boot:run
```

**Terminal 5 - Reservation Service** :
```bash
cd reservation-service
mvn spring-boot:run
```

**Terminal 6 - Payment Service** :
```bash
cd payment-service
mvn spring-boot:run
```

**Vérifier** : Tous les services doivent être "Started" dans leurs logs.

---

## 🌐 Accès aux Services

### Services Web

| Service | URL | Credentials |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | - |
| **User Service** | http://localhost:8081 | - |
| **Catalog Service** | http://localhost:8082 | - |
| **Search Service** | http://localhost:8083 | - |
| **Reservation Service** | http://localhost:8084 | - |
| **Payment Service** | http://localhost:8085 | - |

### Infrastructure

| Service | URL | Credentials |
|---------|-----|-------------|
| **Keycloak** | http://localhost:8080 | admin / admin |
| **RabbitMQ** | http://localhost:15672 | guest / guest |
| **PostgreSQL** | localhost:5432 | riad_user / riad_password |
| **pgAdmin** | http://localhost:5050 | admin@example.com / admin |
| **Elasticsearch** | http://localhost:9200 | - |
| **Prometheus** | http://localhost:9090 | - |
| **Grafana** | http://localhost:3000 | admin / admin |

---

## 🔧 Commandes Utiles

### Docker Compose

```bash
# Démarrer tous les services
docker-compose up -d

# Arrêter tous les services
docker-compose down

# Voir les logs d'un service
docker-compose logs -f postgres

# Reconstruire les images
docker-compose build

# Supprimer les volumes (attention : perte de données)
docker-compose down -v
```

### Maven

```bash
# Compiler le projet parent
mvn clean install

# Compiler et tester un service
cd user-service
mvn clean package

# Nettoyer les fichiers compilés
mvn clean

# Afficher les dépendances
mvn dependency:tree
```

---

## 🐛 Troubleshooting

### ❌ Erreur : "Docker daemon is not running"

**Solution** :
```bash
# Lancez Docker Desktop (Windows/Mac) ou :
sudo systemctl start docker  # Linux
```

### ❌ Erreur : "Port 5432 already in use"

PostgreSQL est déjà en cours d'exécution. Arrêtez-le ou utilisez un autre port dans `docker-compose.yml`.

```bash
# Voir quel processus utilise le port
lsof -i :5432  # Mac/Linux
netstat -ano | findstr :5432  # Windows

# Tuer le processus
kill -9 <PID>
```

### ❌ Erreur : "Connection refused" dans les services

Les services essaient de se connecter avant que PostgreSQL soit prêt. Attendez 30-60 secondes.

```bash
# Vérifier que PostgreSQL est prêt
docker-compose logs postgres
```

### ❌ Erreur : Maven ne trouve pas les dépendances

```bash
# Nettoyer le cache Maven
rm -rf ~/.m2/repository
mvn clean install
```

### ❌ Erreur : "Cannot find symbol" à la compilation

Vérifiez que le fichier `pom.xml` est correct et que vous êtes à la racine du projet.

```bash
# Vérifier la structure
pwd
ls pom.xml
```

---

## 📝 Notes de Développement

- Les fichiers `application.yml` de chaque service ne sont pas commités (`.gitignore`). Vous les créerez lors des prochaines tâches.
- Les images Docker pour les services Spring Boot seront créées lors de la tâche "Containerization".
- Les données PostgreSQL sont stockées dans le volume `postgres_data`.

---

## 📚 Documentation Supplémentaire

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Docker Documentation](https://docs.docker.com/)
- [Keycloak Documentation](https://www.keycloak.org/documentation)

---

## 👨‍💻 Auteur

Créé pour un apprentissage progressif en microservices, Docker, Kubernetes et DevOps.