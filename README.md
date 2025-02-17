# RealWorld Backend Implementation

> ### Spring Boot implementation of RealWorld (Medium clone) backend

This codebase was created to demonstrate a fully fledged fullstack application built with Spring Boot including CRUD operations, authentication, routing, pagination, and more.

For more information on how to this works with other frontends/backends, please refer to the [RealWorld](https://github.com/gothinkster/realworld) repo.

**Note:** The original RealWorld.io demo site is no longer available. However, you can still implement your own service following the API specifications provided in this repository.

## Getting Started

### Prerequisites
- JDK 17 or later
- Maven 3.6.3 or later
- Your favorite IDE (IntelliJ IDEA recommended)

### Installation

#### Open the bash
#### Clone the repository
#### git clone https://github.com/your-username/realworld-springboot.git
#### Navigate to the project directory
#### cd realworld-springboot
#### Install dependencies and build the project
#### mvn clean install


## Database Structure
The database schema can be found in `schema.sql`. This file contains all the necessary table definitions and relationships.

## API Documentation
The API specification is no longer provided. I will create and upload the specification shortly. If anyone is interested in implementing the RealWorld backend, you can refer to the specification for development.
## Domain Model Diagrams

### User Domain
![User Domain Model](/doc/image/user.png)


![Article Domain Model](/doc/image/article.png)

## Features
- JWT Authentication
- User CRUD operations
- Article CRUD operations
- Comment CRD operations
- Favorite Articles
- Follow, UnFollow Users
- Tag support
- Input validation
- Pagination

## Technology Stack
- Spring Boot
- Spring Security
- Spring Data JPA
- H2 Database (for development)
- JWT Authentication
- Gradle
- JUnit 5
- Lombok

## Contributing
Feel free to submit PRs for small issues. For large issues or features, please open an issue first. under the MIT License