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

### Development Tools

- Visual Studio Code
- Git
- GitHub
- Postman

### Future Technologies

- JWT Authentication
- Docker
- Flyway
- Swagger/OpenAPI
- JUnit
- Testcontainers
- GitHub Actions

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

- ✅ Spring Boot project setup
- ✅ PostgreSQL integration
- ✅ REST API
- ✅ Church registration
- ✅ Church listing
- ✅ GitHub integration

---

## 🗺️ Roadmap

### Version 0.1 — Foundation

- [x] Spring Boot
- [x] PostgreSQL
- [x] Church CRUD
- [x] REST API
- [x] GitHub Repository

### Version 0.2 — Authentication

- [ ] User registration
- [ ] BCrypt password encryption
- [ ] JWT Authentication
- [ ] Login
- [ ] Role-based authorization

### Version 0.3 — Member Management

- [ ] Member registration
- [ ] Search
- [ ] Update
- [ ] Delete
- [ ] Pagination

### Version 0.4 — Ministries

- [ ] Ministry management
- [ ] Leaders
- [ ] Member assignment

### Version 0.5 — Attendance

- [ ] Worship attendance
- [ ] Bible study attendance
- [ ] Reports

### Version 0.6 — Finance

- [ ] Tithes
- [ ] Offerings
- [ ] Expenses
- [ ] Financial reports

### Version 1.0

- [ ] Dashboard
- [ ] Reports
- [ ] Notifications
- [ ] File uploads
- [ ] Production deployment

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

Authentication will be implemented using:

- JWT Tokens
- Spring Security
- BCrypt Password Encryption
- Role-Based Access Control (RBAC)

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

Configure PostgreSQL in:

```text
src/main/resources/application.properties
```

Run the application:

```bash
./mvnw spring-boot:run
```

Or use the VS Code Spring Boot extension.

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
