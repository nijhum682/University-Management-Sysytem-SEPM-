# University Management System (Spring Boot + MySQL + Thymeleaf + Docker)

A clean, beginner-friendly, and minimal University Management System web application built with **Java 17**, **Spring Boot 3**, **Spring Security**, **Spring Data JPA**, **MySQL**, **Thymeleaf**, and **Docker**.

---

## 1. Project Folder Structure

```
university-management-system/
├── pom.xml
├── Dockerfile
├── compose.yaml
├── README.md
└── src/
    └── main/
        ├── java/com/university/management/
        │   ├── UniversityManagementApplication.java
        │   ├── config/
        │   │   ├── CustomAuthenticationSuccessHandler.java
        │   │   ├── DataInitializer.java
        │   │   └── SecurityConfig.java
        │   ├── controller/
        │   │   ├── AdminController.java
        │   │   ├── StudentController.java
        │   │   ├── TeacherController.java
        │   │   └── WebController.java
        │   ├── dto/
        │   │   ├── AdminDto.java
        │   │   ├── ErrorResponse.java
        │   │   ├── StudentDto.java
        │   │   └── TeacherDto.java
        │   ├── entity/
        │   │   ├── Admin.java
        │   │   ├── Student.java
        │   │   └── Teacher.java
        │   ├── exception/
        │   │   ├── GlobalExceptionHandler.java
        │   │   └── ResourceNotFoundException.java
        │   ├── repository/
        │   │   ├── AdminRepository.java
        │   │   ├── StudentRepository.java
        │   │   └── TeacherRepository.java
        │   └── service/
        │       ├── AdminService.java
        │       ├── CustomUserDetailsService.java
        │       ├── StudentService.java
        │       └── TeacherService.java
        └── resources/
            ├── application.properties
            ├── static/
            │   ├── css/
            │   │   └── style.css
            │   └── js/
            │       └── main.js
            └── templates/
                ├── access-denied.html
                ├── login.html
                ├── admin/
                │   ├── admins.html
                │   ├── dashboard.html
                │   ├── students.html
                │   └── teachers.html
                ├── teacher/
                │   ├── dashboard.html
                │   └── teachers.html
                └── student/
                    ├── dashboard.html
                    └── students.html
```

---

## 2. Default Seeded Credentials

When the system boots for the first time, `DataInitializer` automatically creates initial accounts if the database is empty:

| Role | Email | Password | Allowed Access |
|---|---|---|---|
| **Admin** | `admin@university.com` | `admin123` | Full CRUD on Admin, Teacher, Student resources & Admin Dashboard |
| **Teacher** | `teacher@university.com` | `teacher123` | Full CRUD on Teacher resources & Teacher Dashboard |
| **Student** | `student@university.com` | `student123` | Full CRUD on Student resources & Student Dashboard |

---

## 3. Role-Based Permissions Matrix

| Resource / Endpoint | Admin | Teacher | Student |
|---|---|---|---|
| **Admin Web Pages** (`/admin/**`) |  Allowed | ❌ Forbidden (403) | ❌ Forbidden (403) |
| **Teacher Web Pages** (`/teacher/**`) | ❌ Forbidden (403) |  Allowed | ❌ Forbidden (403) |
| **Student Web Pages** (`/student/**`) | ❌ Forbidden (403) | ❌ Forbidden (403) |  Allowed |
| **Admin REST APIs** (`/api/admins/**`) |  Full CRUD | ❌ Forbidden (403) | ❌ Forbidden (403) |
| **Teacher REST APIs** (`/api/teachers/**`) |  Full CRUD |  Full CRUD | ❌ Forbidden (403) |
| **Student REST APIs** (`/api/students/**`) |  Full CRUD | ❌ Forbidden (403) |  Full CRUD |

---

## 4. How to Run the Application

### Option A: Running with Docker Compose (Recommended)

Make sure Docker is installed and running on your system.

1. Navigate to the project directory:
   ```bash
   cd "SEPM Spring Boot"
   ```
2. Start all services (MySQL 8.0 and Spring Boot App) with a single command:
   ```bash
   docker compose up --build
   ```
3. Once started:
   - Access the web interface: `http://localhost:8081/login`
   - MySQL is accessible on `localhost:3306` (database: `university_db`, user: `root`, password: `rootpassword`).
4. To stop the containers:
   ```bash
   docker compose down
   ```

---

### Option B: Running Locally (Without Docker)

1. **Install Prerequisites**:
   - Java 17 or higher
   - Maven 3.8+
   - MySQL 8.0 server running locally on port 3306

2. **Configure Database**:
   Log in to MySQL and create the database:
   ```sql
   CREATE DATABASE IF NOT EXISTS university_db;
   ```
   If your MySQL password is not `rootpassword`, set environment variables or edit `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/university_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

3. **Run the Spring Boot App**:
   ```bash
   mvn clean spring-boot:run
   ```
4. Open your browser and go to `http://localhost:8080`.

---

## 5. REST API Documentation & Postman Testing

All REST endpoints support **HTTP Basic Authentication** as well as session cookies. When calling via Postman or `curl`, use the **Authorization** tab -> **Basic Auth** with the appropriate user credentials.

### Admin APIs (`/api/admins`)
*Requires Admin role (`admin@university.com` / `admin123`)*

- `GET /api/admins` – Fetch all admins
- `GET /api/admins/{id}` – Fetch admin by ID
- `POST /api/admins` – Create new admin (Returns 201 Created)
  ```json
  {
    "name": "Jane Doe",
    "email": "jane.doe@university.com",
    "password": "secretPassword123"
  }
  ```
- `PUT /api/admins/{id}` – Update admin
  ```json
  {
    "name": "Jane Doe Updated",
    "email": "jane.doe@university.com"
  }
  ```
- `DELETE /api/admins/{id}` – Delete admin (Returns 204 No Content)

---

### Teacher APIs (`/api/teachers`)
*Accessible by Admin or Teacher*

- `GET /api/teachers` – Fetch all teachers
- `GET /api/teachers/{id}` – Fetch teacher by ID
- `POST /api/teachers` – Create teacher
  ```json
  {
    "name": "Prof. Ada Lovelace",
    "email": "ada@university.com",
    "password": "teacherPassword123",
    "department": "Computer Science",
    "designation": "Associate Professor"
  }
  ```
- `PUT /api/teachers/{id}` – Update teacher
  ```json
  {
    "name": "Prof. Ada Lovelace",
    "email": "ada@university.com",
    "department": "Software Engineering",
    "designation": "Head of Department"
  }
  ```
- `DELETE /api/teachers/{id}` – Delete teacher (Returns 204 No Content)

---

### Student APIs (`/api/students`)
*Accessible by Admin or Student*

- `GET /api/students` – Fetch all students
- `GET /api/students/{id}` – Fetch student by ID
- `POST /api/students` – Register/Create student
  ```json
  {
    "name": "Bob Smith",
    "email": "bob@university.com",
    "password": "studentPassword123",
    "studentId": "STU-2026-042",
    "department": "Computer Science",
    "semester": "2nd"
  }
  ```
- `PUT /api/students/{id}` – Update student
  ```json
  {
    "name": "Bob Smith",
    "email": "bob@university.com",
    "studentId": "STU-2026-042",
    "department": "Computer Science",
    "semester": "3rd"
  }
  ```
- `DELETE /api/students/{id}` – Delete student (Returns 204 No Content)

---

## 6. Key Security & Architecture Features

1. **Least Feature & Clean Design**: Written in direct, beginner-friendly Java code without bloated boilerplate, complex entity inheritance hierarchies, or unnecessary design pattern overhead.
2. **Password Security**: Passwords are encrypted using Spring Security's `BCryptPasswordEncoder` and marked `@JsonProperty(access = Access.WRITE_ONLY)` so they are never leaked in JSON responses.
3. **Role Segregation & RBAC**: Enforced strictly at the filter level in `SecurityConfig`. Unauthorized access returns standard HTTP 403 Forbidden or redirects to the access-denied page.
4. **Centralized Exception Handling**: `GlobalExceptionHandler` converts Bean Validation and business logic errors into uniform JSON error envelopes with proper HTTP status codes.
