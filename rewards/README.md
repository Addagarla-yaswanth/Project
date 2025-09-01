
# Charter Rewards System

This is a Spring Boot project for managing **Customer Rewards** based on their purchase transactions. 
Customers earn reward points depending on how much they spend, and the system provides APIs to manage 
customers, transactions, and retrieve rewards summaries.

---

## 📌 Reward Points Calculation Logic

- For every **$1 spent over $100** → **2 points**
- For every **$1 spent between $50–100** → **1 point**
- For transactions below $50 → **0 points**

### Examples:
- Transaction of **$120** → (50 × 1) + (20 × 2) = **90 points**
- Transaction of **$75**  → (25 × 1) = **25 points**

---
## 🛠️ Project Structure

```
rewards
│── docs/                          # Documentation & screenshots
│── src/main/java/com/charter/rewards
│   ├── config/                     # Security & app configuration
│   ├── controller/                 # REST Controllers
│   ├── dto/                        # DTO classes
│   ├── entity/                     # JPA Entities
│   ├── exception/                  # Exception handling
│   ├── repository/                 # Spring Data JPA repositories
│   ├── security/                   # JWT + Spring Security setup
│   ├── service/                    # Service interfaces
│   ├── serviceImpl/                # Service implementations
│   ├── util/                       # Utilities (JWT Utils, etc.)
│   └── RewardsApplication.java     # Main entry point
│
│── src/main/resources
│   ├── application.properties      # App configuration
│   └── Table.sql                   # Initial SQL setup
│
│── test/java/com/charter/rewards   # Unit & Integration Tests
│── pom.xml                         # Maven dependencies
│── README.md                       # Documentation (this file)
```

---


## ⚙️ Tech Stack

- Java 21
- Spring Boot 3.x
- Spring Security + JWT
- Lombok
- JPA/Hibernate
- H2 / MySQL Database (configurable)
- Maven

---

## 🚀 How to Run the Project

1. Clone the repository:
   ```bash
   git clone https://github.com/your-repo/rewards.git
   cd rewards
   ```

2. Configure **`src/main/resources/application.properties`** for your database.

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

4. Access API endpoints at:
   ```
   http://localhost:8080/api
   ```

---

## 🔑 Authentication

### Endpoint: **POST /customer/authenticate**

#### ✅ Request (authenticateCustomer_request.json)
```json
{
  "custName": "Jack",
  "phoneNo": "9978543210"
}
```

#### ❌ Request (authenticateCustomer_request_fail.json)
```json
{
  "custName": "Jack",
  "phoneNo": "wrong_password"
}
```

#### ✅ Response (authenticateCustomer_response.json)
```json
"eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKYWNrIiwiYW1wIjoxNzU2LCJleHAiOjE3NTYyNzU3ODl9.C_qG5YePgTHBntz_e7LG46FEmnhEonLvox8DNorkYsU"
```

---

## 👥 Customer Management

### Create Customer

**Endpoint:** `POST /api/customers`  

#### Request (createCustomer_request.json)
```json
{
  "custName": "John Doe",
  "phoneNo": "9876543210",
  "transaction": [
    {
      "amount": 120.0,
      "date": "2025-08-01"
    },
    {
      "amount": 75.0,
      "date": "2025-08-15"
    }
  ]
}
```

#### Response (createCustomer_response.json)
```json
{
  "customerId": 1,
  "custName": "John Doe",
  "phoneNo": "9876543210",
  "rewardPoints": 115
}
```

---

## 💳 Transactions

### Get Transactions by Customer

**Endpoint:** `GET /api/{customerId}/transactions`  

#### Response (transactions_response.json)
```json
[
    {
        "date": "2025-06-15",
        "amount": 120.0,
        "product": "Laptop",
        "rewardPoints": 90
    },
    {
        "date": "2025-07-05",
        "amount": 75.0,
        "product": "Headphones",
        "rewardPoints": 25
    },
    {
        "date": "2025-08-10",
        "amount": 200.0,
        "product": "Smartphone",
        "rewardPoints": 250
    }
]
```

---

## 📊 Rewards Summary

### All Customers Rewards

**Endpoint:** `GET /api/rewards/summary`  

#### Response (summary_allCustomers_response.json)
```json
[
    {
        "customerId": 1,
        "custName": "John Doe",
        "phoneNo": "$2a$10$g8S4kPvrcZZPFt2IMLJGAOhPfQHcQcGPsUxn4uhfmBNXxU7..UeyO",
        "monthlyRewards": {
            "2025-07": 25,
            "2025-06": 90,
            "2025-08": 250
        },
        "totalRewards": 365,
        "transactions": [
            {
                "date": "2025-06-15",
                "amount": 120.0,
                "product": "Laptop",
                "rewardPoints": 90
            },
            {
                "date": "2025-07-05",
                "amount": 75.0,
                "product": "Headphones",
                "rewardPoints": 25
            },
            {
                "date": "2025-08-10",
                "amount": 200.0,
                "product": "Smartphone",
                "rewardPoints": 250
            }
        ]
    },
    {
        "customerId": 2,
        "custName": "Jack",
        "phoneNo": "$2a$10$o0s7b3RhtxDZAZWp4ZPdze5FqJkI9NXaceH.7LhjC4p9i7rTDDH2m",
        "monthlyRewards": {
            "2025-05": 250,
            "2025-07": 150,
            "2025-06": 25
        },
        "totalRewards": 425,
        "transactions": [
            {
                "date": "2025-07-15",
                "amount": 150.0,
                "product": "Laptop",
                "rewardPoints": 150
            },
            {
                "date": "2025-06-05",
                "amount": 75.0,
                "product": "Headphones",
                "rewardPoints": 25
            },
            {
                "date": "2025-05-10",
                "amount": 200.0,
                "product": "Smartphone",
                "rewardPoints": 250
            }
        ]
    },
    {
        "customerId": 3,
        "custName": "John Doe",
        "phoneNo": "9876543210",
        "monthlyRewards": {
            "2025-07": 25,
            "2025-06": 90,
            "2025-08": 250
        },
        "totalRewards": 365,
        "transactions": [
            {
                "date": "2025-06-15",
                "amount": 120.0,
                "product": "Laptop",
                "rewardPoints": 90
            },
            {
                "date": "2025-07-05",
                "amount": 75.0,
                "product": "Headphones",
                "rewardPoints": 25
            },
            {
                "date": "2025-08-10",
                "amount": 200.0,
                "product": "Smartphone",
                "rewardPoints": 250
            }
        ]
    },
    {
        "customerId": 4,
        "custName": "Joy",
        "phoneNo": "$2a$10$Sd3y0R2o4hA4ya19rjIvo.bteaBtTa/yCYiv3zanUkPJme/q57zTq",
        "monthlyRewards": {
            "2025-07": 25,
            "2025-06": 90,
            "2025-08": 250
        },
        "totalRewards": 365,
        "transactions": [
            {
                "date": "2025-06-15",
                "amount": 120.0,
                "product": "Laptop",
                "rewardPoints": 90
            },
            {
                "date": "2025-07-05",
                "amount": 75.0,
                "product": "Headphones",
                "rewardPoints": 25
            },
            {
                "date": "2025-08-10",
                "amount": 200.0,
                "product": "Smartphone",
                "rewardPoints": 250
            }
        ]
    },
    {
        "customerId": 5,
        "custName": "Roy",
        "phoneNo": "$2a$10$hGCUExPft3FnkavePGwFl.5N/msG2R/F.IbcUbD6460UxfS.bWpti",
        "monthlyRewards": {
            "2025-07": 25,
            "2025-06": 90,
            "2025-08": 250
        },
        "totalRewards": 365,
        "transactions": [
            {
                "date": "2025-06-15",
                "amount": 120.0,
                "product": "Laptop",
                "rewardPoints": 90
            },
            {
                "date": "2025-07-05",
                "amount": 75.0,
                "product": "Headphones",
                "rewardPoints": 25
            },
            {
                "date": "2025-08-10",
                "amount": 200.0,
                "product": "Smartphone",
                "rewardPoints": 250
            }
        ]
    }
]
```

---

### Rewards by Date Range

**Endpoint:** `GET /api/{customerId}/rewards?startDate=2025-08-01&endDate=2025-08-31`  

#### Response (rewardsByDate_response.json)
```json
{
    "customerId": 1,
    "custName": "John Doe",
    "phoneNo": "$2a$10$g8S4kPvrcZZPFt2IMLJGAOhPfQHcQcGPsUxn4uhfmBNXxU7..UeyO",
    "monthlyRewards": {
        "2025-07": 25,
        "2025-06": 90,
        "2025-08": 250
    },
    "totalRewards": 365,
    "transactions": [
        {
            "date": "2025-06-15",
            "amount": 120.0,
            "product": "Laptop",
            "rewardPoints": 90
        },
        {
            "date": "2025-07-05",
            "amount": 75.0,
            "product": "Headphones",
            "rewardPoints": 25
        },
        {
            "date": "2025-08-10",
            "amount": 200.0,
            "product": "Smartphone",
            "rewardPoints": 250
        }
    ],
    "timeFrame": {
        "endDate": "2025-08-30",
        "startDate": "2025-06-01"
    }
}
```

## 🧪 Running Tests

```bash
mvn test
```

Tests are available for Controllers and Service layers (Mockito + JUnit 5).


