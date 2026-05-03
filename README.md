# ISST-SpringBoot-Security

A Spring Boot 3 web application demonstrating role-based access control with Spring Security 6. Built as a lab exercise for the **ISST** (Ingeniería de Sistemas Software y de Telecomunicaciones) course at **UPM – DIT**.

The project secures a small set of Thymeleaf pages behind different authorization rules, authenticates users via a JDBC-backed H2 database with BCrypt-hashed passwords, and serves everything over HTTPS using a self-signed certificate.

---

## Tech Stack

| | |
|---|---|
| Java | 17 |
| Spring Boot | 3.4.x |
| Spring Security | 6 (via Spring Boot starter) |
| Thymeleaf | + `thymeleaf-extras-springsecurity6` |
| H2 Database | in-memory, runtime only |
| Spring JDBC | user/authority store |
| Spring Boot DevTools | hot reload (optional) |
| Build tool | Maven (wrapper included) |

---

## Project Structure

```
src/
└── main/
    ├── java/es/upm/dit/isst/lab5/
    │   ├── Lab5Application.java
    │   ├── config/
    │   │   └── SecurityConfig.java       ← filter chain + JDBC auth
    │   └── controller/
    │       └── LabController.java        ← route mappings
    └── resources/
        ├── application.properties        ← H2 console + SSL config
        ├── schema.sql                    ← creates users & authorities tables
        ├── data.sql                      ← seeds users with BCrypt passwords
        └── templates/
            ├── index.html                ← public home page
            ├── todos.html                ← any authenticated user
            ├── alumnos.html              ← ALUM role only
            └── profesores.html           ← PROF role only
```

---

## Access Control

| Route | Who can access |
|---|---|
| `/` | Everyone (no login required) |
| `/todos` | Any authenticated user |
| `/alumnos` | Users with role `ALUM` |
| `/profesores` | Users with role `PROF` |
| `/h2` | Open (for development only — remove in production) |

Routes are evaluated in the order declared in `SecurityConfig`. More specific rules are listed first.

---

## Seed Users

Defined in `data.sql`. Passwords are BCrypt-hashed and prefixed with `{bcrypt}` so Spring Security selects the right encoder automatically.

| Username | Password | Role(s) |
|---|---|---|
| `alumno` | `alumno1` | `ALUM` |
| `profe` | `profe1` | `PROF` |
| `admin` | `admin1` | `ALUM` + `PROF` |

> Roles are stored in the `authorities` table as `ROLE_ALUM` / `ROLE_PROF` — Spring Security requires the `ROLE_` prefix.

---

## Getting Started

### Prerequisites

- Java 17 or later
- A keystore file for HTTPS (see [HTTPS setup](#https-setup) below)

### Run

```bash
./mvnw spring-boot:run
```

The application starts at **`https://localhost:8443`** (HTTPS only — plain HTTP is not enabled).

### H2 Console

```
https://localhost:8443/h2
```

| Field | Value |
|---|---|
| JDBC URL | `jdbc:h2:mem:testdb` |
| Username | `sa` |
| Password | *(leave blank)* |

> The H2 console is open without authentication for development convenience. Remove the `/h2/**` permit in `SecurityConfig` before any production deployment.

---

## HTTPS Setup

The application requires a keystore to start. A sample `mykeys.jks` is included in the repo root for development use only — **do not use it in production**.

`application.properties` currently points to a local path:

```properties
server.ssl.key-store=file:///C:/Users/felip/UPM/ISST/practica5.1/lab5/mykeys.jks
server.ssl.key-store-password=cambiame
server.ssl.key-password=cambiame
server.ssl.enabled=true
server.port=8443
```

**Update `server.ssl.key-store` to point to your own keystore before running.** To generate a new self-signed keystore:

```bash
keytool -genkeypair -alias mykey -keyalg RSA -keysize 2048 \
  -storetype JKS -keystore mykeys.jks -validity 365
```

Then update `application.properties` to use a relative path:

```properties
server.ssl.key-store=file:mykeys.jks
```

> Browsers will show a security warning for self-signed certificates. Use Firefox or Safari if Chrome blocks the connection.

---

## Thymeleaf + Spring Security Integration

All templates use the `thymeleaf-extras-springsecurity6` dialect (`xmlns:sec`) to conditionally render content based on the authenticated user's identity and roles.

The home page (`index.html`) demonstrates the key patterns:

```html
<!-- Show only to authenticated users -->
<div sec:authorize="isAuthenticated()">
    <p>Usuario: <span sec:authentication="name"></span></p>
    <p>Roles: <span sec:authentication="principal.authorities"></span></p>
    <a href="/logout">Logout</a>
    <a href="/todos">Todos</a>
    <a href="/alumnos" sec:authorize="hasRole('ALUM')">Alumnos</a>
    <a href="/profesores" sec:authorize="hasRole('PROF')">Profesores</a>
</div>

<!-- Show only to unauthenticated visitors -->
<div sec:authorize="isAnonymous()">
    <a href="/login">Login</a>
</div>
```

Authenticated users see only the links they are authorized to follow. Unauthenticated visitors see only the login link.

---

## Authentication Architecture

User credentials are stored in H2 and loaded at startup from `schema.sql` + `data.sql`. `SecurityConfig` wires a `JdbcUserDetailsManager` with custom queries:

```java
users.setUsersByUsernameQuery(
    "select username, password, enabled from users where username = ?");
users.setAuthoritiesByUsernameQuery(
    "select username, authority from authorities where username = ?");
```

An in-memory alternative (commented out in `SecurityConfig`) is also included for reference, showing the equivalent setup without a database.
