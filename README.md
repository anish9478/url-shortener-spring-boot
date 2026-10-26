# URL Shortener — Spring Boot

A RESTful URL Shortener API built using Java, Spring Boot, Spring Data JPA, and MySQL. It converts long URLs into short, easy-to-share links and provides URL redirection, custom aliases, expiration, and click tracking.

## Features

- **Short URL Generation:** Generate unique short codes for long URLs.
- **Custom Aliases:** Allow users to choose their own short codes.
- **URL Redirection:** Redirect short links to their original destinations.
- **Expiration Support:** Set an expiration time for short URLs.
- **Click Tracking:** Count successful redirects for each short URL.
- **URL Statistics:** Retrieve details such as the original URL, creation time, expiry time, and click count.
- **Input Validation:** Validate URL format and expiry values.
- **Exception Handling:** Handle invalid requests, missing short codes, and expired URLs.
- **Swagger UI:** Explore and test API endpoints interactively.
- **Automated Tests:** Unit and controller tests using JUnit and Mockito.

## Tech Stack

- Java
- Spring Boot
- Spring Data JPA / Hibernate
- MySQL
- Maven
- REST APIs
- Swagger UI / OpenAPI
- JUnit 5 and Mockito

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/apis/url/shorten` | Create a short URL |
| GET | `/apis/url/{shortCode}` | Redirect to the original URL |
| GET | `/apis/url/stats/{shortCode}` | Retrieve URL statistics |

## Example Request

Create a short URL using `POST /apis/url/shorten`.

```json
{
  "originalUrl": "https://example.com/my-long-url",
  "customAlias": "myexample",
  "expiresInHours": 24
}
```

- `originalUrl`: The URL to shorten.
- `customAlias`: Optional custom short code.
- `expiresInHours`: Optional expiry duration in hours.

## Example Response

The following is an illustrative response. Actual fields and values may vary.

```json
{
  "shortCode": "myexample",
  "originalUrl": "https://example.com/my-long-url",
  "shortUrl": "http://localhost:8080/apis/url/myexample",
  "expiresAt": "2026-10-27T12:00:00"
}
```

## Prerequisites

Before running the project, install:

- JDK 21 or a compatible version configured for the project
- MySQL
- Git

## Setup and Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/anish9478/url-shortener-spring-boot.git
   ```

2. Open the project directory:

   ```bash
   cd url-shortener-spring-boot
   ```

3. Create a MySQL database:

   ```sql
   CREATE DATABASE urlshortner_db;
   ```

4. Configure your database URL, username, and password in `src/main/resources/application.properties`. Use environment variables for credentials and never commit real passwords.

5. Run the application:

   ```bash
   ./mvnw spring-boot:run
   ```

6. Open Swagger UI:

   http://localhost:8080/swagger-ui/index.html

## Running Tests

Run the automated test suite with:

```bash
./mvnw clean test
```

## Error Handling

The API supports validation errors, conflict responses for duplicate aliases, `404 Not Found` for unknown short codes, and `410 Gone` for expired URLs.

## Future Improvements

- Add user authentication and URL ownership.
- Add rate limiting and abuse prevention.
- Deploy the application to a cloud platform.
- Add integration tests with a dedicated test database.

## Author

**Anish Keshri**

GitHub: https://github.com/anish9478
