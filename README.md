# F-THON_MARU_server

함께 정상까지

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.8 or higher

### Installation

1. Clone this repository:

   ```
   git clone <repository-url>
   cd maru-server
   ```

2. Build the project:

   ```
   mvn clean install
   ```

3. Run the application:
   ```
   mvn spring-boot:run
   ```

The application will start on port 8080.

## Project Structure

```
src/main/java/com/maru/     - Java source files
  ├── config/               - configuration files
  ├── controllers/          - REST API controllers
  ├── models/               - Data models
  ├── services/             - Business logic services
  └── MaruApplication.java  - Application entry point
```

## Technology Stack

- Spring Boot 3.2.5
- Maven 3.8+
