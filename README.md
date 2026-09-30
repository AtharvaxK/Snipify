# Snipify - High-Performance URL Shortener & Analytics Platform

**Snipify** is an enterprise-grade URL shortening service engineered for high concurrency, low latency, and comprehensive data analytics. Built with **Java 21** and **Spring Boot**, the platform leverages **Redis** for in-memory caching and asynchronous event-driven processing to deliver sub-millisecond URL redirections while reliably capturing vast amounts of user analytics.

---

## 🎯 Engineering Impact & Key Features

- **Sub-Millisecond Redirection Engine**: Implemented an aggressive caching strategy using **Redis** for short URL mappings, drastically reducing PostgreSQL load and ensuring lightning-fast HTTP 302 redirects.
- **Asynchronous Data Processing**: Decoupled click analytics (IP fetching, Geo-Location, Device/Browser parsing) into asynchronous worker threads using Spring's `@Async` and a `LogAnalyticsAsyncService`. This ensures that analytics insertion does not bottleneck the primary redirect execution thread.
- **Optimized Database Synchronization**: Engineered a highly optimized `ClickSyncScheduler` that batches click-counts in Redis and synchronizes them back to the primary PostgreSQL database via scheduled cron jobs, protecting the relational database from high write contention during traffic spikes.
- **Collision-Resistant Distributed Unique IDs**: Integrated **TSID (Time-Sorted Unique Identifiers)** mapped with custom Base62 encoding to generate highly secure, sortable, and collision-free short codes at scale.
- **Robust Security & RBAC**: Developed a comprehensive security layer utilizing stateless **JWT (JSON Web Tokens)** and **OAuth2 Client** integrations for authentication, alongside Role-Based Access Control distinguishing Admin, Pro, Standard, and Developer API clients.
- **Deep Geographic & Device Analytics**: Leveraged MaxMind **GeoIP2** databases and User-Agent parsing strategies to provide Pro users with intricate metrics (Country, City, Browser, OS, Referrer).
- **Automated Lifecycle Management**: Deployed scheduled tasks for aggressive cleanup of expired URL aliases to eliminate stale cache mappings and optimize database density.

---

## 🛠️ Technical Stack & Skills

- **Backend Core:** Java 21, Spring Boot 3.x, Spring Web, Spring Data JPA
- **Database Architecture:** PostgreSQL (Relational persistence), Redis (In-memory Caching, Counter Aggregation)
- **Security:** Spring Security, JWT (jjwt-api), OAuth2 Client
- **Algorithms & Tooling:** TSID Creator (Distributed ID Generation), Base62 Encoding, ZXing Core (QR Code Generation)
- **Networking & Insights:** MaxMind GeoIP2 (IP tracking/Geolocation offline API)
- **Development & Build:** Maven, Lombok, Spring Boot DevTools

---

## 📐 System Architecture Highlights

### 1. Read-Heavy Cache Strategy (URL Resolution)
When a user accesses a Short-Code endpoint, Snipify executes a highly-optimized lookup path:
1. **Cache Interrogation**: Queries `RedisService`. On a **Cache Hit**, it instantly redirects the traffic while asynchronously delegating the analytics update.
2. **Database Fallback**: On a **Cache Miss**, it queries PostgreSQL, handles the redirect, and synchronously repopulates the Redis cache with an eviction TTL (Time-To-Live) to warm up subsequent requests.

### 2. Non-Blocking Analytics Pipeline
To support data-heavy operations without impacting the core user experience, operations are strictly separated:
* Redirect resolution is synchronous, cached, and prioritized.
* Resource-intensive tasks (like Location parsing via GeoLite2 and HTTP-Request extraction) are fully deferred to dedicated non-blocking thread pools.

---

## 🚀 Setup and Installation

### Prerequisites
- JDK 21+
- PostgreSQL Server (Local or Containerized)
- Redis Server (Local or Containerized)
- `GeoLite2-City.mmdb` binary file placed in `src/main/resources/` (Required for IP Geolocation)

### Build Instructions
```bash
# Clone Repository
git clone https://github.com/AtharvaxK/Snipify.git

# Move into source project
cd Snipify/snipify

# Configure Environment Variables in application.properties
# Provide your PostgreSQL credentials, Redis host, and JWT Secret details.

# Build artifact via Maven
./mvnw clean install

# Initialize Boot execution
./mvnw spring-boot:run
```
*(The server boots by default on Port `8080`)*

---

## 📁 Repository Structure Overview

- `controller/`: REST API presentation layer defining all HTTP endpoint mappings.
- `service/`: Centralized business logic defining the Async analytics, Caching mechanisms, Mail processing, and secure mapping protocols.
- `security/`: Configurations enforcing stateless JWT authentication, CORS strategies, OAuth2, and role-based tracking.
- `util/`: Essential low-level components including IP Address Extractors, User-Agent parsers, and Base62 converters.
- `model/ & repo/`: Application state management, data entity specifications, and JPA ORM repositories.
