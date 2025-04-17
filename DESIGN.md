# Design Document: Short URL Backend Service (Kotlin)

## 1. Overview
This service allows users to create shortened URLs that redirect to original, long URLs. It exposes REST APIs for creating, retrieving, and deleting short URLs.

## 2. Goals
- Generate unique, short URLs
- Redirect to original URL
- Support high availability and scalability
- Basic analytics (optional: click count, timestamp)
- Expiration support (optional)
- Minimal latency
- Postgres DB snapshots for bloom filter for faster load time

## 3. Non-Goals
- No user authentication (working onto it)
- No support for custom aliases (e.g., `short.ly/myalias`)
- No UI/frontend

## 4. API Design

| Method | Endpoint       | Description               |
|--------|----------------|---------------------------|
| POST   | `/`            | Create a short URL        |
| GET    | `/{shortCode}` | Redirect to original URL  |
| DELETE | `/{shortCode}` | Delete a short URL        |

### Example Request:
```http
POST http://localhost:8080/api/short-links
Content-Type: application/json

{
  "originalUrl": "www.orange.com",
  "userId": "831ec447-1f5f-4638-9bde-673c2dcccd6d"
}
```

### Example Response:
```json
{
  "shortUrl": "2cbed8d5"
}
```

## 5. Architecture

- Router (Spring Boot)
- Handler  
- Service Layer (business logic)
- Repository Layer (data access)
- Database (PostgresSQL & Redis)

Optional components:
- Metrics (Micrometer + Prometheus)
- Background jobs (cleanups/expiry)

## 6. Data Model

```kotlin
@Table("short_links")
data class ShortLink(
    @Id
    val id: UUID? = null,
    val shortCode: String,
    val originalUrl: String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime? = null,
    val clicks: Int = 0,
    val userId: UUID? = null,
    val isActive: Boolean = true
)
```

## 7. Short Code Generation

```kotlin
fun createShortLink(originalUrl: String, userId: UUID?): Mono<ShortLinkResponse> {
    val code = generateShortCode()

    return bloomFilterService.check(code)
        .map { exists: Boolean -> checkCollisionAndUpdate(exists, code, originalUrl, userId) }
        .doOnNext { link: ShortLink -> writeBufferService.enqueue(link) }
        .flatMap { link: ShortLink -> bloomFilterService.add(link.shortCode).thenReturn(ShortLinkResponse(link.shortCode)) }
}
```
## High-Level Flow of `createShortLink()` Function
```
[ User Request: createShortLink(originalUrl, userId) ]
         |
         v
   +--------------------+
   | Generate Short Code|
   | (generateShortCode)|
   +--------------------+
         |
         v
 +-------------------------+     +-----------------------+
 | Check Bloom Filter      | --> | Does Code Exist in    |
 | (check)                 |     | Bloom Filter (Redis)  |
 +-------------------------+     +-----------------------+
         |                           |
         v                           v
+---------------------------+   +---------------------------------------+
| Code Exists in Bloom?     |   | Collision? (checkCollisionAndUpdate)  |
+---------------------------+   +---------------------------------------+
         |                           |
         v                           v
    [ ShortLink Creation ]     [ ShortLink Collision ]
         |                           |
         v                           v
 +--------------------------+   +--------------------------+
 | Enqueue ShortLink to     |   | Enqueue ShortLink to     |
 | WriteBufferService       |   | WriteBufferService       |
 +--------------------------+   +--------------------------+
         |                           |
         v                           v
+--------------------------+   +--------------------------+
| Persist ShortLink in DB  |   | Persist ShortLink in DB  |
+--------------------------+   +--------------------------+
         |
         v
 +------------------------+
 | Add to Bloom Filter    |
 +------------------------+
         |
         v
+-------------------------+
| Return ShortLinkResponse|
+-------------------------+

```
## High-Level Flow of `initialize()` Function from bloom-filter
```
[ Application Start ]
         |
         v
   +-----------------------------+
   | Spring Creates Bean (BloomFilterService) |
   | (Bean Instantiation)        |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | BeanPostProcessor (Before Initialization)  |
   | (postProcessBeforeInitialization) |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Dependency Injection         |
   | (ReactiveStringRedisTemplate, |
   | ShortLinkRepository)         |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | BeanPostProcessor (After Initialization) |
   | (postProcessAfterInitialization) |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Check if Preload is Needed  |
   | (initialize)                |
   +-----------------------------+
         |
         v
  [ If Preload is NOT Needed ]
         |
         v
   +-----------------------------+
   | Initialize Bloom Filter     |
   | (initBloomFilter)            |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Bloom Filter Exists?        |
   | (Check in Redis)            |
   +-----------------------------+
         |
         v
   +-----------------------------+       +-------------------------+
   | Create Bloom Filter if Not   | <--- | Bloom Filter Already    |
   | Exists (BF.RESERVE)         |      | Exists (skip)           |
   +-----------------------------+       +-------------------------+
         |
         v
   +-----------------------------+
   | Preload Data from DB to     |
   | Bloom Filter                |
   | (preloadFromDb)             |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Process and Batch Data      |
   | (Buffering in Chunks of 1000)|
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Add Data to Bloom Filter    |
   | (BF.ADD in Redis)           |
   +-----------------------------+
         |
         v
   +-----------------------------+
   | Completion of Preloading    |
   | (Done)                      |
   +-----------------------------+
```

## 8. Tech Stack

- Kotlin + Spring Boot
- Reactive repositories (PostgresSQL)
- Redis for caching (+bloom filters)
- Flyway (DB migrations)
- Kafka --TBA


## 9. Error Handling

Common errors:
- TBA

## 10. Scalability & Performance

- Use Redis for read-heavy operations
- Use a URL cache
- Async jobs for save/analytics/logging
- Can be containerized with Docker + Kubernetes

## 11. Testing

- Unit Tests: Service logic, URL generation
- Integration Tests: Routers + Repository
- Load Tests: (e.g., with Gatling or JMeter -- need to look into it*)