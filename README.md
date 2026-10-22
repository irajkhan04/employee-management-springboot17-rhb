# Java 17 + Spring Boot REST API Assignment

A small, layered example designed to be easy to understand and extend.

## Requirements covered

- Java 17 and Maven
- REST endpoints using GET, POST, PUT, PATCH, and DELETE
- Search with pagination and optional filters
- Aspect-based request/controller-response logging
- H2 in-memory database
- JPQL join query between employees and departments
- Outbound HTTP call to `https://www.google.com`
- Unit tests using JUnit 5 and Mockito

## Prerequisites

- JDK 17
- Maven 3.8+

Check your environment:

```bash
java -version
mvn -version
```

## Run the application

From the project directory:

```bash
mvn clean test
mvn spring-boot:run
```

The app starts at `http://localhost:8080`.

H2 console: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:assignmentdb`
- User: `sa`
- Password: leave blank

H2 is in-memory, so data is reset when the application stops.

## API examples

### Create an employee — POST

```bash
curl -X POST http://localhost:8080/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Amina Khan","email":"amina@example.com","departmentId":1,"salary":6500}'
```

Seed departments are created at startup. Department IDs are usually `1` (Engineering) and `2` (Finance).

### Get an employee — GET

```bash
curl http://localhost:8080/api/employees/1
```

### Search with pagination — GET

```bash
curl "http://localhost:8080/api/employees?name=ami&departmentId=1&page=0&size=5&sortBy=name&direction=asc"
```

Pages are zero-based. Response includes `content`, `page`, `size`, `totalElements`, and `totalPages`.

### Update an employee — PUT

```bash
curl -X PUT http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Amina Khan","email":"amina.khan@example.com","departmentId":1,"salary":7000}'
```

### Partially update an employee — PATCH

```bash
curl -X PATCH http://localhost:8080/api/employees/1 \
  -H "Content-Type: application/json" \
  -d '{"salary":7200}'
```

### Delete an employee — DELETE

```bash
curl -X DELETE http://localhost:8080/api/employees/1
```

### JPQL join: employees and departments

```bash
curl http://localhost:8080/api/employees/with-departments
```

### Call an external API

```bash
curl http://localhost:8080/api/external/google-status
```

This calls `https://www.google.com` from a service class and returns the upstream HTTP status. Network access may be restricted in some environments; if Google cannot be reached, the endpoint returns a gateway error.

## Design notes

- `controller`: HTTP mapping and request validation
- `service`: business logic
- `repository`: database access
- `dto`: API request/response models
- `entity`: JPA entities
- `aspect`: cross-cutting request/response logging
- `config`: HTTP client configuration

The logging aspect logs controller method names, arguments, returned values, duration, and exceptions. Avoid logging passwords, tokens, personal data, or other secrets in real applications. For high-volume production systems, use structured logging and consider payload redaction.

The PATCH endpoint intentionally supports a small, explicit set of fields. Add fields to `EmployeePatchRequest` and the service method when the domain grows.

## Sample join

`EmployeeRepository.findEmployeesWithDepartment()` uses JPQL to join `Employee` to `Department` and return a DTO projection. This avoids returning JPA entities directly from the API.

## Tests

`mvn test` runs:
- Service unit tests with Mockito
- A Spring Boot integration test for the HTTP API and in-memory database
