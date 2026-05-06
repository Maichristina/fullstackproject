#JOb Search Fullstack Application
Fullstack job search application with a Spring Boot backend and React frontend

## Description
This application allows users to:
- Register and authenticate (JWT - based authentication)
- Manage job postings
- Search for jobs
- Interact with a REST API backend connected to a React frontend

FOR USERS
- Register and login with JWT authentication
- Browse available job listings
- Apply to jobs with personal details
- Track application status (Pending/Accepted/Rejected)
- Save jobs for later
- Manage personal profile

FOR ADMINS
- Create, edit and delete job postings
- View all applications
- Accept or reject applicants
- Manage users(accept/delete)
---

Tech Stack
### Backend
- Java 21
- Spring Boot
- Spring Security (JWT)
- JPA / Hibernate
- PostgreSQL
- Maven
- Swagger
- Logback
### Frontend
- React
- Vite
- React Router
- React Icons


### DevOps
- Docker
- Docker Compose

---



MyFullStackProject/
├── docker-compose.yml
├── project/                    ← Spring Boot backend
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/java/com/christinamai/project/
│           ├── config/         ← SecurityConfig, SwaggerConfig
│           ├── controller/     ← REST Controllers
│           ├── dto/            ← Request/Response DTOs
│           ├── entity/         ← JPA Entities
│           ├── repository/     ← Spring Data Repositories
│           ├── security/       ← JWT Filter, JwtUtils
│           └── service/        ← Business Logic
└── my-frontend/                ← React frontend
    ├── Dockerfile
    ├── package.json
    └── src/
        ├── context/            ← AuthContext
        ├── pages/              ← Page components
        └── components/         ← Shared components

users
├── id (PK)
├── username (unique)
├── email (unique)
├── password (BCrypt)
└── role (ROLE_USER / ROLE_ADMIN)

jobs
├── id (PK)
├── title
├── description
├── location
├── salary
├── posted_date
└── posted_by_id (FK → users)

applications
├── id (PK)
├── job_id (FK → jobs)
├── user_id (FK → users)
├── status (PENDING / ACCEPTED / REJECTED)
└── applied_date


Run Locally (without Docker)
- Java 21
- Maven
- PostgreSQL
- Node.js

Run with Docker
# 1. Clone the repository
git clone https://github.com/Maichristina/fullstackproject.git
cd fullstackproject

# 2. Build and start all containers
docker-compose up --build

# 3. Open the app
# Frontend: http://localhost:5173
# Backend:  http://localhost:8080
# Swagger:  http://localhost:8080/swagger-ui/index.html

Create a default admin user
docker exec -it postgres_db psql -U postgres -d myapp -c \
  "INSERT INTO users (username, email, password, role) VALUES ('admin', 'admin@careerstream.com', '\$2a\$10\$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ROLE_ADMIN') ON CONFLICT DO NOTHING;"



### Backend Setup 
``` create the database
CREATE DATABASE myapp;

```application.properties
spring.datasource.url=jdbc:postgresql://localhost:5432/myapp
spring.datasource.username=postgres
spring.datasource.password=xristina123
spring.jpa.hibernate.ddl-auto=update
app.jwt.secret=xristinaSecretKey123456789012345678901234
app.jwt.expiration=86400000

```run the backend
cd project
mvn spring-boot:run

### Frontend Setup
```install dependencies
cd my-frontend
npm install

```start the dev server
npm run dev

```Api documentation
Swagger:  http://localhost:8080/swagger-ui/index.html

```Running tests
cd project
mvn test



