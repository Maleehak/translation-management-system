# Translation Engine

A backend service built with **Spring Boot** and **PostgreSQL** that manages multilingual translations with tagging, searching, and JSON export support.  
Designed for **performance**, **scalability**, and **frontend consumption**.

---

## 🚀 Features
- CRUD APIs for **translations** and **languages**
- Multi-language support with `lang_code` references
- **Tag-based search** using PostgreSQL `GIN` index
- Full-text search with `to_tsvector`
- JSON export endpoint optimized for large datasets
- JWT-based authentication with role/authority system
- Swagger/OpenAPI documentation
- Docker + Docker Compose setup
- 90%+ Unit Test coverage for all critical functionalities

---

## 🏗️ Tech Stack
- **Spring Boot 3+**
- **PostgreSQL 15+**
- **Spring Data JPA / Hibernate**
- **Spring Security + JWT**
- **MapStruct / Custom mappers**
- **Docker Compose**

---

## ⚙️ Setup Instructions

#### 1. Clone the Repository
```bash
git clone https://github.com/Maleehak/translation-management-system
```



#### 2. Configure Database

Make sure you have PostgreSQL installed and running.

Create a new database and run the commands present in src/main/resources/sql/script.sql:
```bash
CREATE DATABASE translationengine;
```

Alternate way is to docker-compose.yml to spin up docker images:
```bash
# Build Docker images
docker-compose build

# Start containers
docker-compose up
```

#### 3. Load data and run application

To automatically populate 100K records of translations, just run the application. It will automatically run the loader that will insert the records into the db for quick production level simulation 

```bash
mvn clean install
mvn spring-boot:run

```


## 🛠️ Design Choices

#### 1. JSON Streaming with JsonFactory
Instead of building large JSON in memory, I used a streaming generator (JsonGenerator) to write JSON directly to the HttpServletResponse. 
This reduces memory usage and speeds up exports of 100K translations to < 400ms

<img width="844" height="436" alt="Screenshot 2025-09-15 at 11 52 23 PM" src="https://github.com/user-attachments/assets/fa4d3153-fa64-44f0-9a93-0e594fbc7b65" />

#### 2. Database Indexing

- GIN index for array fields (tags).
- btree index for lookup fields (translation_key, lang_code).
- GIN + to_tsvector for full-text search in text.

These indexes improve performance for search-heavy operations.

#### 3. Fetch Strategies

- Default fetch type is kept lazy to avoid N+1 problems.
- Fetch joins are used selectively when returning entities that require related data.










