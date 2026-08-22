# ✝️ FaithOS

> **A modern cloud-based Church Management System built with Java, Spring Boot, and PostgreSQL.**

FaithOS is a Software-as-a-Service (SaaS) platform designed to help churches manage their daily operations, members, ministries, events, attendance, finances, and communication in a secure and scalable way.

This project is being developed as a real-world software engineering project, following professional architecture, Git workflows, and best practices.

---

## 🚀 Vision

Our goal is to build a modern church management platform that is:

- Secure
- Scalable
- Easy to use
- Cloud-ready
- Multi-tenant (multiple churches using the same platform)

Each church has its own isolated workspace and data.

---

## 🛠️ Tech Stack

### Backend

- Java 17
- Spring Boot
- Spring Data JPA
- Spring Security
- Hibernate
- Maven

### Database

- PostgreSQL
- Flyway database migrations

### Frontend

- React
- TypeScript
- Vite

### Development Tools

- Visual Studio Code
- Git
- GitHub
- Postman

### Delivery

- Docker Compose for local PostgreSQL
- GitHub Actions for backend and frontend verification

---

## 📁 Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.obysoft.faithOS
│   │       ├── controller
│   │       ├── service
│   │       ├── repository
│   │       ├── entity
│   │       ├── dto
│   │       ├── mapper
│   │       ├── config
│   │       ├── security
│   │       ├── validation
│   │       └── exception
│   │
│   └── resources
│       └── application.properties
│
└── test
```

---

## 📌 Current Features

- ✅ Isolated multi-church registration and administration
- ✅ Secure cookie-based JWT authentication
- ✅ Role-based access for administrators, pastors, leaders, and members
- ✅ People and user account management
- ✅ Ministries, events, and contributions
- ✅ Live dashboard backed by the API
- ✅ English, French, Brazilian Portuguese, and Haitian Creole
- ✅ PostgreSQL schema managed with Flyway
- ✅ Automated backend and frontend CI

---

## 🗺️ Roadmap

### Version 0.1 — Foundation

- [x] Spring Boot
- [x] PostgreSQL
- [x] Church CRUD
- [x] REST API
- [x] GitHub Repository

### Version 0.2 — Authentication

- [x] User registration
- [x] BCrypt password encryption
- [x] JWT authentication
- [x] Login
- [x] Role-based authorization

### Version 0.3 — Member Management

- [x] Member registration
- [x] Search
- [x] Update
- [x] Account activation and suspension
- [ ] Invitation and mandatory first-login password change
- [x] Pagination

### Version 0.4 — Ministries

- [x] Ministry management
- [x] Leaders
- [x] Member assignment

### Version 0.5 — Attendance

- [x] Worship attendance
- [x] Bible study attendance
- [x] Reports

### Version 0.6 — Finance

- [x] Contributions
- [x] Expenses
- [x] Financial reports

### Version 1.0

- [x] Dashboard
- [x] Reports
- [x] Notifications
- [x] File uploads
- [x] Production deployment

---

## 🏗️ Architecture

FaithOS follows a layered architecture.

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
PostgreSQL
```

---

## 🔒 Security

Authentication is implemented using:

- Short-lived JWT tokens in Secure, HttpOnly, SameSite cookies
- Spring Security
- BCrypt Password Encryption
- Role-Based Access Control (RBAC)
- Login rate limiting
- Per-church data isolation

Roles planned:

- SUPER_ADMIN
- CHURCH_ADMIN
- PASTOR
- LEADER
- MEMBER

---

## 📬 API

Current endpoints:

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | `/api/churches` | List churches |
| POST | `/api/churches` | Register a church |

Future API documentation will be available through Swagger/OpenAPI.

---

## 🚀 Running the Project

Clone the repository:

```bash
git clone https://github.com/joffre2001/faithOS.git
```

Enter the project:

```bash
cd faithOS
```

Enter the application directory (the Compose file is here):

```bash
cd faithOS
```

Create your local, ignored environment file:

```powershell
Copy-Item .env.example .env
```

Replace every placeholder in `.env` with local values. Generate a unique JWT secret of at least 64 random characters. Never commit `.env`.

Start PostgreSQL from this same directory:

```bash
docker compose up -d
```

Run the backend:

```bash
./mvnw spring-boot:run
```

On Windows, use `.\mvnw.cmd spring-boot:run`. Flyway creates a fresh schema automatically. An existing pre-Flyway database is baselined at version 1 and then managed by migrations.

In a second terminal, start the frontend:

```bash
cd frontend
npm install
npm run dev
```

Open `http://localhost:5173`.

### Verification

```bash
./mvnw test
cd frontend
npm ci
npm run build
```

The same checks run in GitHub Actions for pushes and pull requests.

### Production with Docker Compose

Create the production environment file inside the application directory:

```powershell
cd faithOS
Copy-Item .env.example .env
```

Set strong, unique values for `POSTGRES_PASSWORD` and `JWT_SECRET`. When the application is served behind HTTPS, set `AUTH_COOKIE_SECURE=true`. Then build and start the complete stack:

```bash
docker compose --env-file .env -f compose.prod.yaml up -d --build
```

The frontend is exposed on `APP_PORT` (port 80 by default), proxies `/api` to the private backend service, and supports SPA routing. PostgreSQL and the backend are not exposed publicly. Database and uploaded-file data use named volumes.

Check deployment health and logs:

```bash
docker compose --env-file .env -f compose.prod.yaml ps
docker compose --env-file .env -f compose.prod.yaml logs -f backend frontend
```

For an internet-facing deployment, terminate TLS with a platform load balancer or HTTPS reverse proxy and back up both the `postgres_data` and `uploaded_files` volumes.

---

## 📈 Development Workflow

This project follows a feature branch workflow.

Example:

```bash
git checkout -b feature/authentication

git add .

git commit -m "feat: implement JWT authentication"

git push origin feature/authentication
```

---

## 🤝 Contributing

Contributions, suggestions, and feedback are welcome.

If you'd like to contribute:

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

---

## 👨‍💻 Author

**Obenson Joffre**

Backend Developer (Java & Spring Boot)

GitHub:
https://github.com/joffre2001

---

## ⭐ Project Status

🚧 Under active development.

FaithOS is being built incrementally using professional software engineering practices with the goal of becoming a production-ready Church Management System.
