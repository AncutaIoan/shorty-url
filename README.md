
# Reactive Spring Project with Lettuce, Kotlin, Bloom Filter, and Reactor

This is a Spring Boot application utilizing **Lettuce** (for Redis), **Spring Data Reactive Repositories**, **Reactor** for building reactive and non-blocking applications, and a **Bloom Filter** for the creation of short URLs. The project is written in **Kotlin** and follows reactive programming principles to provide a highly scalable, event-driven architecture.

## Features

- **Reactive Programming**: Leverages Project Reactor for building non-blocking, asynchronous services.
- **Redis Integration**: Uses **Lettuce** for Redis communication to perform high-throughput operations with Redis.
- **Short URL Creation**: Implements **Bloom Filters** for efficiently creating unique short URLs.
- **Spring WebFlux**: Fully reactive web stack with asynchronous request processing.
- **Kotlin**: Written in Kotlin for a concise and expressive syntax.
- **Java 21**: Utilizes Java 21 for the latest features and improvements.

## Technologies

- **Spring Boot**: The primary framework for the application, facilitating rapid development.
- **Spring WebFlux**: A reactive web framework for building non-blocking web services.
- **Spring Data Reactive**: Provides reactive repositories for accessing data in a non-blocking manner.
- **Lettuce**: A fast and efficient Redis client to interact with Redis in a non-blocking, reactive way.
- **Reactor**: The reactive library that powers Spring WebFlux, enabling asynchronous and reactive programming.
- **Kotlin**: A modern language running on the JVM, providing a more concise and expressive syntax than Java.
- **Java 21**: The latest long-term support version of Java, with new features like pattern matching, record types, and improved performance.
- **Bloom Filter**: A probabilistic data structure used to efficiently test whether an element is a member of a set, helping with short URL generation.

## Prerequisites

- Java 21 or later
- Kotlin 1.5 or later
- Redis (if running locally, or you can use a cloud service like Redis Cloud)
- Maven or Gradle for dependency management

## Getting Started

Follow these steps to set up and run the project locally.

### 1. Clone the Repository

```bash
git clone https://github.com/AncutaIoan/shorty-url.git
cd shorty-url
```

### 2. Set up Redis

If you're running Redis locally, make sure it's installed and running on the default port (`6379`).

If you're using a cloud-based Redis service, obtain the connection URL and credentials.

### 3. Configure Application Properties

In the `src/main/resources/application.yml` or `src/main/resources/application.properties`, configure your Redis connection.

Example configuration (`application.yml`):

```yaml
spring:
  redis:
    host: localhost
    port: 6379
    database: 0
    password: yourpassword # Optional if Redis requires a password
```

### 4. Build and Run the Application

You can build and run the application with:

```bash
./gradlew bootRun
```

Or build the JAR and run it:

```bash
./gradlew build
java -jar build/libs/reactive-spring-kotlin-redis.jar
```

The application will be running on `http://localhost:8080` by default.

## Short URL Creation with Bloom Filter

This project implements **Bloom Filter** for the generation of short URLs. A Bloom Filter helps in checking whether a short URL already exists before creating a new one. It is a highly efficient, probabilistic data structure that minimizes space while maintaining fast lookups.

The **short URL creation** process involves the following steps:

1. The system receives the original URL.
2. A **hash function** is applied to the original URL to generate a short code.
3. The Bloom Filter checks if the short code already exists.
4. If the code does not exist, it is added to the Redis database, and the short URL is returned.
5. If the code already exists (according to the Bloom Filter), it generates a new short code and repeats the process.
