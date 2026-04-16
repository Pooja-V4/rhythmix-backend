# 🎵 Rhythmix Backend

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.5-brightgreen?style=for-the-badge&logo=springboot)
![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Auth-purple?style=for-the-badge&logo=jsonwebtokens)
![Maven](https://img.shields.io/badge/Maven-Build-red?style=for-the-badge&logo=apachemaven)

**A full-featured RESTful music application backend built with Spring Boot, Spring Security, JWT Authentication, and PostgreSQL.**

[Features](#-features) • [Tech Stack](#-tech-stack) • [Getting Started](#-getting-started) • [API Reference](#-api-reference) • [Database Design](#-database-design) • [Contributing](#-contributing)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Database Setup](#database-setup)
  - [Configuration](#configuration)
  - [Running the App](#running-the-app)
- [Database Design](#-database-design)
- [API Reference](#-api-reference)
- [Project Structure](#-project-structure)
- [Future Improvements](#-future-improvements)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🌟 Overview

Rhythmix Backend is a production-ready REST API that powers the Rhythmix music application. It handles user authentication, playlist management, favorites, and integrates seamlessly with the React frontend. Built following industry best practices including layered architecture (Controller → Service → Repository), JWT-based stateless authentication, and BCrypt password hashing.

---

## ✨ Features

### 🔐 Authentication & Security
- **JWT Authentication** — stateless token-based auth (24hr expiry)
- **BCrypt Password Hashing** — secure password storage
- **Email Verification** — users must verify email before login
- **Google OAuth2** — sign in with Google account
- **Forgot Password** — secure password reset via email link (1hr expiry)
- **Spring Security** — all endpoints protected except auth routes

### 👤 User Management
- User registration with email verification
- User profile with account stats
- Update profile (name)
- Change password with current password verification
- Delete account (cascades to all user data)
- Google user detection (separate UI flow)

### 🎵 Music Features
- Song CRUD (Create, Read, Delete)
- Playlist CRUD with song management
- Add/remove songs from playlists
- Favorites system (like/unlike songs)
- User-specific data isolation

### 📧 Email System
- Beautiful HTML email templates
- Verification email on signup
- Password reset email with secure token
- Password changed notification
- Gmail SMTP integration

### 🛡️ Security Features
- Stateless JWT filter on every request
- CORS configuration for frontend
- Global exception handler
- Duplicate email prevention
- Token expiry validation

---

## 🛠 Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming language |
| Spring Boot | 4.0.5 | Application framework |
| Spring Security | 7.x | Authentication & authorization |
| Spring Data JPA | 4.x | Database ORM layer |
| Hibernate | 7.2.7 | JPA implementation |
| PostgreSQL | 18 | Relational database |
| JWT (jjwt) | 0.12.6 | Token generation & validation |
| BCrypt | - | Password hashing |
| JavaMailSender | - | Email sending |
| Maven | 3.x | Build tool & dependency management |
| Lombok | 1.18.x | Boilerplate reduction |

---

## 🚀 Getting Started

### Prerequisites

Make sure you have the following installed:

- **Java 21** — [Download](https://adoptium.net/)
- **Maven** — bundled via `mvnw` wrapper
- **PostgreSQL 15+** — [Download](https://www.postgresql.org/download/)
- **IntelliJ IDEA** (recommended) or any Java IDE
- **Postman** (for API testing)
- **Git**

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/Pooja-V4/rhythmix-backend.git
cd rhythmix-backend
```

**2. Install dependencies**

```bash
./mvnw clean install -DskipTests
# Windows:
.\mvnw.cmd clean install -DskipTests
```

### Database Setup

**1. Install and start PostgreSQL**

**2. Open PostgreSQL terminal**

```bash
psql -U postgres
```

**3. Create database and user**

```sql
CREATE DATABASE musicapp;
CREATE USER musicuser WITH PASSWORD 'music123';
GRANT ALL PRIVILEGES ON DATABASE musicapp TO musicuser;
\q
```

**4. Add required columns (if upgrading from older version)**

```sql
psql -U musicuser -d musicapp

ALTER TABLE users ADD COLUMN IF NOT EXISTS verified BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS verification_token_expiry TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS created_at TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_password_token VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS reset_password_token_expiry TIMESTAMP;
ALTER TABLE users ADD COLUMN IF NOT EXISTS google_user BOOLEAN NOT NULL DEFAULT false;

UPDATE users SET verified = true;
\q
```

### Configuration

**1. Create `application.properties`**

Copy the example file and fill in your values:

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

**2. Fill in your values**

```properties
# ===== DATABASE =====
spring.datasource.url=jdbc:postgresql://localhost:5432/musicapp
spring.datasource.username=musicuser
spring.datasource.password=music123
spring.datasource.driver-class-name=org.postgresql.Driver

# ===== JPA =====
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false

# ===== SERVER =====
server.port=8081
spring.application.name=rhythmix-backend

# ===== JWT =====
jwt.secret=your-super-secret-key-minimum-32-characters-long
jwt.expiration=86400000

# ===== EMAIL (Gmail SMTP) =====
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_gmail@gmail.com
spring.mail.password=your_16_char_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# ===== APP =====
app.base.url=http://localhost:5173

# ===== GOOGLE OAUTH =====
google.client.id=your_google_client_id.apps.googleusercontent.com
```

**3. Gmail App Password setup**

```
Gmail → Google Account → Security
→ Enable 2-Step Verification
→ App Passwords → Mail → Windows
→ Generate → copy 16-char password (no spaces)
→ paste in spring.mail.password
```

**4. Google OAuth setup**

```
https://console.cloud.google.com
→ Create Project → APIs & Services
→ OAuth Consent Screen → External
→ Credentials → Create OAuth Client ID
→ Web Application
→ Authorized origins: http://localhost:5173
→ Copy Client ID → paste in google.client.id
```

### Running the App

```bash
# Development
./mvnw spring-boot:run -DskipTests

# Windows
.\mvnw.cmd spring-boot:run -DskipTests

# Production JAR
./mvnw clean package -DskipTests
java -jar target/musicapp-0.0.1-SNAPSHOT.jar
```

App starts at: `http://localhost:8081`

---

## 🗄 Database Design

### Entity Relationship Diagram

```
┌─────────────────┐         ┌─────────────────────┐
│      users      │         │      playlists       │
├─────────────────┤         ├─────────────────────┤
│ id (PK)         │◄──────  │ id (PK)             │
│ name            │  1:Many │ name                │
│ email (unique)  │         │ user_id (FK)        │
│ password        │         └──────────┬──────────┘
│ verified        │                    │ Many:Many
│ google_user     │         ┌──────────▼──────────┐
│ created_at      │         │   playlist_songs     │
│ verification_   │         ├─────────────────────┤
│   token         │         │ playlist_id (FK)    │
│ reset_password_ │         │ song_id (FK)        │
│   token         │         └──────────┬──────────┘
└────────┬────────┘                    │
         │ 1:Many           ┌──────────▼──────────┐
         │                  │        songs         │
┌────────▼────────┐         ├─────────────────────┤
│    favorites    │         │ id (PK)             │
├─────────────────┤         │ title               │
│ id (PK)         │         │ artist              │
│ user_id (FK)    │         │ album               │
│ song_id (FK)    │◄────────│ duration_seconds    │
└─────────────────┘         └─────────────────────┘
```

### Tables

| Table | Description |
|---|---|
| `users` | User accounts with auth and verification fields |
| `songs` | Music tracks saved to the library |
| `playlists` | User-created playlists |
| `playlist_songs` | Join table for Many-to-Many playlist/song relationship |
| `favorites` | User liked songs with unique constraint |

### Relationships

- **User → Playlists** — One-to-Many (one user has many playlists)
- **User → Favorites** — One-to-Many (one user has many favorites)
- **Playlist ↔ Songs** — Many-to-Many via `playlist_songs` join table

---

## 📡 API Reference

### Base URL
```
http://localhost:8081
```

### Authentication
All endpoints except `/auth/*` require a JWT token in the header:
```
Authorization: Bearer <your_jwt_token>
```

---

### 🔐 Auth Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/register` | ❌ | Register new user |
| POST | `/auth/login` | ❌ | Login with email/password |
| GET | `/auth/verify-email?token=` | ❌ | Verify email address |
| POST | `/auth/resend-verification` | ❌ | Resend verification email |
| POST | `/auth/forgot-password` | ❌ | Send password reset email |
| POST | `/auth/reset-password` | ❌ | Reset password with token |
| POST | `/auth/google` | ❌ | Google OAuth login |

#### POST `/auth/register`
```json
// Request
{
  "name": "Pooja",
  "email": "pooja@gmail.com",
  "password": "123456"
}

// Response 201
{
  "message": "Registration successful! Please check your email to verify your account.",
  "email": "pooja@gmail.com"
}
```

#### POST `/auth/login`
```json
// Request
{
  "email": "pooja@gmail.com",
  "password": "123456"
}

// Response 200
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "name": "Pooja",
  "email": "pooja@gmail.com"
}
```

---

### 👤 User Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/users` | ✅ | Get all users |
| GET | `/users/{id}` | ✅ | Get user by ID |
| GET | `/users/{id}/profile` | ✅ | Get full profile with stats |
| PUT | `/users/{id}/profile` | ✅ | Update name |
| PUT | `/users/{id}/password` | ✅ | Change password |
| DELETE | `/users/{id}` | ✅ | Delete account |

#### GET `/users/{id}/profile`
```json
// Response 200
{
  "id": 1,
  "name": "Pooja",
  "email": "pooja@gmail.com",
  "createdAt": "2026-04-01T10:00:00",
  "totalPlaylists": 3,
  "totalFavorites": 12,
  "totalSongs": 8,
  "googleUser": false
}
```

---

### 🎵 Song Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/songs` | ✅ | Add a song |
| GET | `/songs` | ✅ | Get all songs |
| GET | `/songs/{id}` | ✅ | Get song by ID |
| DELETE | `/songs/{id}` | ✅ | Delete a song |

#### POST `/songs`
```json
// Request
{
  "title": "Blinding Lights",
  "artist": "The Weeknd",
  "album": "After Hours",
  "durationSeconds": 200
}
```

---

### 📁 Playlist Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/playlists/{userId}` | ✅ | Create playlist for user |
| GET | `/playlists/user/{userId}` | ✅ | Get all user playlists |
| POST | `/playlists/{playlistId}/songs/{songId}` | ✅ | Add song to playlist |
| DELETE | `/playlists/{playlistId}/songs/{songId}` | ✅ | Remove song from playlist |

---

### ❤️ Favorites Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/favorites/{userId}/songs/{songId}` | ✅ | Add to favorites |
| GET | `/favorites/{userId}` | ✅ | Get user favorites |
| DELETE | `/favorites/{userId}/songs/{songId}` | ✅ | Remove from favorites |

---

## 📁 Project Structure

```
src/main/java/com/musicapp/musicapp/
├── config/
│   ├── CorsConfig.java              # CORS handled in SecurityConfig
│   └── SecurityConfig.java          # Spring Security + JWT filter chain
├── controller/
│   ├── AuthController.java          # Register, login, verify, reset, Google OAuth
│   ├── UserController.java          # User profile endpoints
│   ├── SongController.java          # Song CRUD
│   ├── PlaylistController.java      # Playlist management
│   └── FavoriteController.java      # Favorites management
├── dto/
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── GoogleAuthRequest.java
│   ├── UpdateProfileRequest.java
│   ├── ChangePasswordRequest.java
│   └── ProfileResponse.java
├── entity/
│   ├── User.java                    # User entity with verification fields
│   ├── Song.java                    # Song entity
│   ├── Playlist.java                # Playlist entity (ManyToMany with Song)
│   └── Favorite.java                # Favorite entity (unique user+song)
├── exception/
│   └── GlobalExceptionHandler.java  # Global error handling
├── repository/
│   ├── UserRepository.java
│   ├── SongRepository.java
│   ├── PlaylistRepository.java
│   └── FavoriteRepository.java
├── security/
│   ├── JwtUtil.java                 # JWT generate, validate, extract
│   ├── JwtFilter.java               # JWT request filter
│   └── CustomUserDetailsService.java
└── service/
    ├── UserService.java             # Profile, password, delete logic
    ├── SongService.java
    ├── PlaylistService.java
    ├── FavoriteService.java
    └── EmailService.java            # HTML email templates
```

---

## 🔮 Future Improvements

### Short Term
-  Pagination for songs and playlists (`GET /songs?page=0&size=20`)
-  Search songs by title/artist (`GET /songs/search?q=blinding`)
-  Sort songs by title, artist, duration
-  Song count per playlist in API response

### Medium Term
-  Refresh token mechanism (auto-renew JWT)
-  Rate limiting on auth endpoints
-  Album entity with album art URL storage
-  Share playlist via public link

### Long Term
-  Redis caching for frequently accessed data
-  WebSocket for real-time now-playing sync
-  Analytics (most played songs, listening history)
-  Social features (follow users, shared playlists)

### Security Improvements
-  Refresh token rotation
-  OAuth2 with more providers (GitHub, Apple)
-  Two-factor authentication (2FA)

---

## 🤝 Contributing

Contributions are welcome! Here's how to get started:

**1. Fork the repository**

```bash
git clone https://github.com/Pooja-V4/rhythmix-backend.git
```

**2. Create a feature branch**

```bash
git checkout -b feature/your-feature-name
```

**3. Make your changes and commit**

```bash
git add .
git commit -m "feat: add your feature description"
```

**4. Push and create a Pull Request**

```bash
git push origin feature/your-feature-name
```

### Commit Message Convention

```
feat:     new feature
fix:      bug fix
docs:     documentation changes
refactor: code refactoring
test:     adding tests
chore:    build or config changes
```

### Code Style
- Follow standard Java naming conventions
- Add comments for complex business logic
- Write meaningful variable and method names

---

## 👩‍💻 Author

**Pooja**
- GitHub: [@Pooja-V4](https://github.com/Pooja-V4)

---

<div align="center">
Built with ❤️ using Spring Boot · PostgreSQL · JWT
</div>
