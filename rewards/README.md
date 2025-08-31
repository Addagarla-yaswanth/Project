# Rewards Application

A Spring Boot application for managing authentication, customers, and rewards.  
Includes REST APIs for login, token-based authentication, customer creation (with transactions), and rewards calculation.

📌 Problem Statement

A retail store wants to implement a customer rewards program.
The reward points are calculated based on customer transactions using the following rules:

For every dollar spent over $100 → 2 points

For every dollar spent between $50–100 → 1 point

Below $50 → 0 points

Examples:

Transaction of $120 → (50 × 1) + (20 × 2) = 90 points

Transaction of $75 → (25 × 1) = 25 points

The system should:

Allow customers to be created with their transactions.

Calculate reward points for the last 3 months.

Calculate reward points for a specific month offset.

Authenticate customers with JWT-based login.

📂 Project Structure
---

## 📂 Project Structure

```
rewards
├── .mvn/wrapper
│   └── maven-wrapper.properties
├── docs/
├── test-screenshots/
├── auth-api.md
├── customer-api.md
├── src/
│   ├── main/
│   │   ├── java/com/infy/rewards
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── CustomerController.java
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── service/
│   │   │   ├── serviceImpl/
│   │   │   └── RewardsApplication.java
│   │   └── resources/
│   │       ├── Table.sql
│   │       └── application.properties
│   └── test/java/com/infy/rewards
│       ├── controller/
│       │   ├── AuthControllerTest.java
│       │   ├── CustomerControllerTest.java
│       ├── serviceImpl/
│       │   └── CustomerServiceImplTest.java
│       └── RewardsApplicationTests.java
├── .gitattributes
├── .gitignore
├── mvnw
├── mvnw.cmd
├── pom.xml
└── README.md
```

---

## ⚙️ Requirements

- Java **21+**
- Maven **3.9+**
- Spring Boot **3.x**
- MySQL database (configurable in `application.properties`)

---

## 🚀 Running the Application

Clone the repository:

```bash
git clone <your-repo-url>
cd rewards
```

Build & run:

```bash
./mvnw spring-boot:run
```

The application will be available at:

```
http://localhost:8080
```

---

🔑 API Endpoints
Authentication (/customer/authenticate)

POST /customer/authenticate
Authenticate with custName and phoneNo.

Request Body:

{
  "custName": "Yaswanth",
  "phoneNo": "9345678990"
}


Response:

{
  "token": "jwt-token"
}

Customers (/customers)
1. Create a new customer with transactions

POST /customers

Request Body:

{
  "custName": "Alice",
  "phoneNo": "9876543210",
  "transactions": [
    {
      "amount": 120,
      "transactionDate": "2025-08-01"
    },
    {
      "amount": 200,
      "transactionDate": "2025-08-15"
    }
  ]
}


Response:

{
  "custId": 1,
  "custName": "Alice",
  "phoneNo": "9876543210",
  "transactions": [
    {
      "transactionId": 101,
      "amount": 120,
      "transactionDate": "2025-08-01"
    },
    {
      "transactionId": 102,
      "amount": 200,
      "transactionDate": "2025-08-15"
    }
  ]
}

2. Get total rewards for the last 3 months

GET /customers/{custId}/rewards

Response:

Hello {custName}, your total reward points for last 3 months are {points}.

3. Get rewards for a specific month offset

GET /customers/{custId}/rewards/{monthOffset}

Response:

Hello {custName}, your reward points for month offset {monthOffset} are {points}.

---

## 🧪 Testing

Tests are written using **JUnit 5** and **Mockito**.  
Located under `src/test/java/com/infy/rewards/`

- `AuthControllerTest.java` → Authentication endpoints  
- `CustomerControllerTest.java` → Customer APIs  
- `CustomerServiceImplTest.java` → Service layer tests  
- `RewardsApplicationTests.java` → Application context load  

Run tests with:

```bash
./mvnw test
```

---

## 📸 Screenshots

Screenshots of successful test runs are available under:

```
test-screenshots/
```
