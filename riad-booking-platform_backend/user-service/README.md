# 👤 User Service

Service de gestion des utilisateurs avec authentification Keycloak (OAuth2/OIDC).

## 📋 Fonctionnalités

- CRUD utilisateurs
- Authentification JWT via Keycloak
- Profil utilisateur
- Gestion des rôles (USER, ADMIN)

## 🚀 Démarrage Rapide

### Prérequis

- Java 21+
- Maven 3.8+
- Docker & Docker Compose

### Option 1 : Avec Docker (Recommandé)

```bash
# 1. Copier la configuration
copy .env.example .env

# 2. Compiler le projet (depuis la racine du backend)
cd ..
mvn clean package -DskipTests -pl user-service -am
cd user-service

# 3. Démarrer tous les services
docker-compose up -d

# 4. Vérifier les logs
docker-compose logs -f user-service
```

Ou utilisez les scripts batch :
- `start-infra.bat` - Démarre uniquement PostgreSQL + Keycloak
- `start-all.bat` - Démarre tout (infra + service)

### Option 2 : Développement Local

```bash
# 1. Démarrer l'infrastructure
docker-compose up -d postgres keycloak

# 2. Attendre ~30 secondes que Keycloak démarre

# 3. Lancer le service
mvn spring-boot:run
```

## 🔧 Configuration

### Variables d'environnement (.env)

| Variable | Description | Défaut |
|----------|-------------|--------|
| `POSTGRES_USER` | Utilisateur PostgreSQL | `riad_user` |
| `POSTGRES_PASSWORD` | Mot de passe PostgreSQL | `riad_password` |
| `POSTGRES_DB` | Base de données | `user_db` |
| `KEYCLOAK_ADMIN` | Admin Keycloak | `admin` |
| `KEYCLOAK_ADMIN_PASSWORD` | Mot de passe admin | `admin` |
| `KEYCLOAK_REALM` | Realm Keycloak | `riad-booking` |

## 🌐 Endpoints

### API Publics
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/users/health` | Health check |
| GET | `/actuator/health` | Spring Actuator health |
| GET | `/actuator/prometheus` | Métriques Prometheus |

### API Authentifiés (JWT requis)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/users/profile` | Profil utilisateur courant |
| POST | `/users` | Créer un utilisateur |
| GET | `/users/{id}` | Obtenir un utilisateur |
| GET | `/users/email/{email}` | Obtenir par email |
| PUT | `/users/{id}` | Mettre à jour |
| DELETE | `/users/{id}` | Supprimer |

### API Admin (Rôle ADMIN requis)
| Méthode | Endpoint | Description |
|---------|----------|-------------|
| GET | `/users` | Lister tous les utilisateurs |

## 🔐 Configuration Keycloak

### Étape 1 : Créer le Realm

1. Accéder à http://localhost:8180
2. Se connecter avec `admin` / `admin`
3. Créer un nouveau realm : `riad-booking`

### Étape 2 : Créer le Client

1. Dans le realm `riad-booking`, aller dans Clients
2. Créer un client : `user-service-client`
3. Configuration :
   - Client authentication: ON
   - Authorization: OFF
   - Standard flow: ON
   - Direct access grants: ON

### Étape 3 : Créer un utilisateur test

1. Aller dans Users > Add user
2. Créer un utilisateur avec email et mot de passe
3. Activer "Email verified"

### Obtenir un token JWT

```bash
curl -X POST "http://localhost:8180/realms/riad-booking/protocol/openid-connect/token" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=user-service-client" \
  -d "client_secret=<votre-secret>" \
  -d "username=<votre-user>" \
  -d "password=<votre-password>"
```

## 🧪 Tests

```bash
# Tests unitaires
mvn test

# Tests d'intégration
mvn verify
```

## 📊 Monitoring

- **Health**: http://localhost:8081/actuator/health
- **Métriques**: http://localhost:8081/actuator/prometheus
- **Info**: http://localhost:8081/actuator/info

## 🐳 Docker

### Commandes utiles

```bash
# Voir les logs
docker-compose logs -f user-service

# Redémarrer le service
docker-compose restart user-service

# Reconstruire l'image
docker-compose build user-service

# Arrêter tout
docker-compose down

# Arrêter et supprimer les volumes
docker-compose down -v
```

## 🏗️ Architecture

```
user-service/
├── src/main/java/ma/lunaire/userservice/
│   ├── controller/     # REST Controllers
│   ├── dto/            # Data Transfer Objects
│   ├── entity/         # JPA Entities
│   ├── exception/      # Exception Handlers
│   ├── repository/     # JPA Repositories
│   ├── security/       # Security Configuration
│   ├── service/        # Business Logic
│   └── util/           # Utilities (JWT Provider)
├── src/main/resources/
│   ├── application.yml         # Config par défaut
│   └── application-docker.yml  # Config Docker
├── docker-compose.yml
├── Dockerfile
├── .env.example
└── README.md
```

