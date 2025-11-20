# GigTasker Common Library

The shared kernel for the **GigTasker** microservices platform. This library centralizes cross-cutting concerns—primarily **Spring Security** configuration and **Keycloak** integration—to ensure consistency and prevent code duplication across the backend ecosystem.

## 🚀 Features

* **Centralized Security Filter Chain:** Provides a standard production-grade `SecurityFilterChain` for all microservices.
    * Disables CSRF/Form Login (stateless APIs).
    * Enables CORS for the Angular frontend.
    * Configures OAuth2 Resource Server support.
* **Keycloak Role Converter:** Automatically extracts roles from the Keycloak JWT (`realm_access.roles`) and converts them into Spring Security Authorities (e.g., `ADMIN` -> `ROLE_ADMIN`).
* **Strategy Pattern Hooks:** Allows individual services to inject custom security rules (like allowing WebSockets) without rewriting the entire configuration.
* **Shared Dependencies:** Manages versions for Spring Boot Security, Web, and OAuth2 libraries.

## 🛠️ Tech Stack

* **Java:** 21 / 25
* **Framework:** Spring Boot 3.4+
* **Security:** Spring Security 6 (OAuth2 Resource Server)
* **Build Tool:** Maven

---

## 📦 Installation

Since this is a private shared library, you must install it to your local Maven repository before building the dependent services.

```bash
mvn clean install
