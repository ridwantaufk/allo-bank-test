# Allo Bank Backend Test — Split Bill API

A Spring Boot REST API for managing bill groups, expenses, and settlement calculations.

## Tech Stack

- Java 21
- Spring Boot 4.1.1
- Maven
- Spring Web MVC
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Docker
- JUnit 5

## Features

- Create a bill group with participants
- Add expenses to a bill group
- Define exactly how an expense is shared between participants
- Validate participant ownership within a bill group
- Validate monetary precision using `BigDecimal`
- Validate that expense shares equal the expense amount
- Calculate settlement transactions between participants
- Minimize settlement transactions using creditor/debtor matching
- Calculate a personalized service charge based on the GitHub username
- Consistent JSON error responses for validation and business errors
- Docker multi-stage build

---

## Project Structure

```text
src/main/java/com/ridwantaufik/allobank/
├── controller/
├── dto/
├── entity/
├── exception/
├── repository/
├── service/
└── util/
```

### Main Components

| Component                 | Responsibility                                |
| ------------------------- | --------------------------------------------- |
| `BillGroupController`     | Create bill groups                            |
| `ExpenseController`       | Add expenses                                  |
| `SettlementController`    | Retrieve settlement summary                   |
| `BillGroupService`        | Create groups and participants                |
| `ExpenseService`          | Create and validate expenses                  |
| `SettlementService`       | Calculate participant balances                |
| `SettlementCalculator`    | Convert balances into settlement transactions |
| `ServiceChargeCalculator` | Calculate personalized service charge         |

---

# Running Locally

## Prerequisites

Make sure the following are installed:

- Java 21+
- Docker
- Docker Compose

Maven Wrapper is included, so a separate Maven installation is not required.

## 1. Start PostgreSQL

From the project root:

```bash
docker compose up -d
```

Check the database:

```bash
docker compose ps
```

PostgreSQL is exposed locally on:

```text
localhost:5433
```

## 2. Run Tests

Windows:

```cmd
mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

## 3. Start the Application

Windows:

```cmd
mvnw.cmd spring-boot:run
```

Linux/macOS:

```bash
./mvnw spring-boot:run
```

The API runs on:

```text
http://localhost:4110
```

---

# API Endpoints

## 1. Create Bill Group

### Endpoint

```http
POST /api/bill-groups
```

### Request

```bash
curl -X POST http://localhost:4110/api/bill-groups ^
  -H "Content-Type: application/json" ^
  -d "{\"name\":\"Trip Bandung\",\"participants\":[\"Ridwan\",\"Nani\",\"Eva\"]}"
```

Linux/macOS:

```bash
curl -X POST http://localhost:4110/api/bill-groups \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Trip Bandung",
    "participants": [
      "Ridwan",
      "Nani",
      "Eva"
    ]
  }'
```

### Example Response

```json
{
  "id": "a59c4895-2f98-43e0-b955-f7e9f0e2cec1",
  "name": "Trip Bandung",
  "participants": [
    {
      "id": "6207ec2c-3d22-404a-8a30-561f9d3637c9",
      "name": "Ridwan"
    },
    {
      "id": "5d8d0311-9eeb-4647-895c-138f62be3575",
      "name": "Nani"
    },
    {
      "id": "8895beae-758c-4d43-bbef-a517ff1329e7",
      "name": "Eva"
    }
  ],
  "createdAt": "2026-09-10T12:00:00Z"
}
```

The participant IDs returned by this endpoint are used when creating expenses.

---

# 2. Add Expense

### Endpoint

```http
POST /api/bill-groups/{groupId}/expenses
```

### Example

Assuming:

```text
Group ID:
a59c4895-2f98-43e0-b955-f7e9f0e2cec1

Ridwan:
6207ec2c-3d22-404a-8a30-561f9d3637c9

Nani:
5d8d0311-9eeb-4647-895c-138f62be3575

Eva:
8895beae-758c-4d43-bbef-a517ff1329e7
```

Windows:

```cmd
curl -X POST http://localhost:4110/api/bill-groups/a59c4895-2f98-43e0-b955-f7e9f0e2cec1/expenses ^
  -H "Content-Type: application/json" ^
  -d "{\"paidByParticipantId\":\"6207ec2c-3d22-404a-8a30-561f9d3637c9\",\"amount\":150000.00,\"description\":\"Makan malam\",\"shares\":[{\"participantId\":\"6207ec2c-3d22-404a-8a30-561f9d3637c9\",\"amount\":50000.00},{\"participantId\":\"5d8d0311-9eeb-4647-895c-138f62be3575\",\"amount\":50000.00},{\"participantId\":\"8895beae-758c-4d43-bbef-a517ff1329e7\",\"amount\":50000.00}]}"
```

Linux/macOS:

```bash
curl -X POST http://localhost:4110/api/bill-groups/a59c4895-2f98-43e0-b955-f7e9f0e2cec1/expenses \
  -H "Content-Type: application/json" \
  -d '{
    "paidByParticipantId": "6207ec2c-3d22-404a-8a30-561f9d3637c9",
    "amount": 150000.00,
    "description": "Makan malam",
    "shares": [
      {
        "participantId": "6207ec2c-3d22-404a-8a30-561f9d3637c9",
        "amount": 50000.00
      },
      {
        "participantId": "5d8d0311-9eeb-4647-895c-138f62be3575",
        "amount": 50000.00
      },
      {
        "participantId": "8895beae-758c-4d43-bbef-a517ff1329e7",
        "amount": 50000.00
      }
    ]
  }'
```

### Validation Rules

The API rejects:

- Amounts greater than two decimal places
- Empty or invalid expense shares
- Duplicate participants within the same expense
- Shares whose total does not equal the expense amount
- Participants that do not belong to the bill group
- A payer that does not belong to the bill group

All monetary values are represented using Java `BigDecimal`.

---

# 3. Get Settlement Summary

### Endpoint

```http
GET /api/bill-groups/{groupId}/settlement
```

### Example

```bash
curl http://localhost:4110/api/bill-groups/a59c4895-2f98-43e0-b955-f7e9f0e2cec1/settlement
```

### Example Response

```json
{
  "groupId": "a59c4895-2f98-43e0-b955-f7e9f0e2cec1",
  "groupName": "Trip Bandung",
  "totalExpense": 150000.0,
  "serviceChargePct": 4,
  "serviceChargeAmount": 6000.0,
  "totalWithServiceCharge": 156000.0,
  "settlements": [
    {
      "fromParticipantId": "8895beae-758c-4d43-bbef-a517ff1329e7",
      "fromParticipantName": "Eva",
      "toParticipantId": "6207ec2c-3d22-404a-8a30-561f9d3637c9",
      "toParticipantName": "Ridwan",
      "amount": 50000.0
    },
    {
      "fromParticipantId": "5d8d0311-9eeb-4647-895c-138f62be3575",
      "fromParticipantName": "Nani",
      "toParticipantId": "6207ec2c-3d22-404a-8a30-561f9d3637c9",
      "toParticipantName": "Ridwan",
      "amount": 50000.0
    }
  ]
}
```

---

# Settlement Calculation

For each participant:

```text
Net Balance = Total Paid - Total Owed
```

- Positive balance → participant should receive money
- Negative balance → participant owes money
- Zero balance → participant is settled

The settlement calculator matches debtors with creditors and generates the required payment transactions.

The implementation uses `BigDecimal` throughout the calculation to avoid floating-point monetary inaccuracies.

---

# Personalized Service Charge

The required service charge is calculated from the GitHub username.

GitHub username:

```text
ridwantaufk
```

The implementation calculates the ASCII/Unicode character sum in code:

```text
sum("ridwantaufk") = 1184
1184 % 10 = 4
```

Therefore:

```text
service_charge_pct = 4%
```

For a group with:

```text
totalExpense = 150000.00
```

the service charge is:

```text
150000.00 × 4% = 6000.00
```

and:

```text
totalWithServiceCharge = 156000.00
```

The username and percentage are **not hardcoded as a percentage**. The percentage is derived programmatically from the username according to the challenge requirement.

The service charge is currently returned as part of the settlement summary. Participant settlement balances are calculated from the actual expense shares, so the service charge does not silently alter individual expense obligations.

---

# Docker

The project includes a multi-stage Dockerfile.

## Build Image

```bash
docker build -t allo-bank-test:latest .
```

## Run Container

When PostgreSQL is exposed through the host on port `5433`:

```bash
docker run --name allo-bank-backend \
  -p 4110:4110 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/allo_bank \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  allo-bank-test:latest
```

The application listens on:

```text
http://localhost:4110
```

Database configuration can be overridden using environment variables:

```text
SPRING_DATASOURCE_URL
SPRING_DATASOURCE_USERNAME
SPRING_DATASOURCE_PASSWORD
SERVER_PORT
```

---

# Testing

The project includes unit tests for:

- Service charge calculation
- GitHub username-based service charge percentage
- Zero service charge calculation
- Basic settlement calculation
- Transaction minimization
- Already-balanced participants

Run all tests:

Windows:

```cmd
mvnw.cmd clean test
```

Linux/macOS:

```bash
./mvnw clean test
```

---

# Error Handling

The API provides consistent JSON error responses for:

- Resource not found → `404 Not Found`
- Invalid business input → `400 Bad Request`
- Bean validation errors → `400 Bad Request`

Example:

```json
{
  "timestamp": "2026-09-10T12:13:16Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Total share amount must equal expense amount",
  "path": "/api/bill-groups/{groupId}/expenses"
}
```

---

# GitHub Information

GitHub username:

```text
ridwantaufk
```

The repository should be submitted as a private GitHub repository according to the challenge instructions.

---

# Challenge Submission Question

## What is your GitHub username and what service charge percentage does it produce?

```text
GitHub username: ridwantaufk

ASCII/Unicode character sum: 1184

1184 % 10 = 4

Service charge percentage: 4%
```

The service charge percentage is calculated programmatically by the application.

---

# Notes

This implementation focuses on the required functionality and correctness rather than adding unnecessary features.

The core flow is:

```text
Create Bill Group
       │
       ▼
Add Participants
       │
       ▼
Add Expenses
       │
       ▼
Validate Expense Shares
       │
       ▼
Calculate Participant Balances
       │
       ▼
Generate Settlement Transactions
       │
       ▼
Add Personalized Service Charge
       │
       ▼
Settlement Response
```
