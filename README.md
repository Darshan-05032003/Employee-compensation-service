cat > README.md <<'EOF'
# Employee Compensation Service

A backend service built with Java, Azure Functions, and Microsoft SQL Server.

## Overview

The service provides HTTP-triggered Azure Functions for:

- Employee CRUD operations
- Employee filtering by department
- Compensation reporting
- Bonus analysis

## Technology Stack

- Java 21
- Maven
- Azure Functions
- Microsoft SQL Server
- JDBC
- Docker

## Project Structure

```text
src/main/java/com/employee/compensation/
├── config/
├── functions/
├── model/
├── repository/
└── service/

sql/
├── schema.sql
└── seed.sql