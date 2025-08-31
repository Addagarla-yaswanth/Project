🎁 Customer Rewards System

This is a Spring Boot application that calculates and tracks customer reward points based on their purchases.
The reward points are calculated as follows:

For every $1 spent over $100 → 2 points

For every $1 spent between $50–100 → 1 point

Purchases below $50 → 0 points

🚀 Project Structure
rewards/
│── docs/                          # Documentation & screenshots
│── testcases_screenshots/         # Test case evidence
│── *.json                         # Postman request/response JSON files
│
├── src/main/java/com/charter/rewards
│   ├── config/                     # Spring Security & configuration
│   ├── controller/                 # REST controllers
│   ├── dto/                        # Data Transfer Objects
│   ├── entity/                     # JPA entities
│   ├── exception/                  # Custom exceptions
│   ├── repository/                 # Spring Data JPA repositories
│   ├── security/                   # JWT Security classes
│   ├── service/                    # Service interfaces
│   ├── serviceImpl/                # Service implementations
│   ├── util/                       # Utility classes (e.g., JwtUtil)
│   └── RewardsApplication.java     # Main Spring Boot app
│
├── src/main/resources
│   ├── application.properties      # Configurations
│   └── Table.sql                   # Schema
│
├── src/test/java/com/charter/rewards
│   ├── controller/                 # Controller tests
│   └── serviceImpl/                # Service tests
│
├── pom.xml                         # Maven dependencies
└── README.md                       # Project documentation

⚙️ Setup Instructions

Clone the repository

git clone https://github.com/your-repo/rewards.git
cd rewards


Build the project

mvn clean install


Run the application

mvn spring-boot:run


API will be available at:

http://localhost:8080
🔑 Authentication (JWT)

Before accessing secured endpoints, the customer must authenticate.

Endpoint:
POST /customer/authenticate

Request JSON:

{
  "custName": "Jack",
  "phoneNo": "9978543210"
}


Response (success):

"eyJhbGciOiJIUzI1NiJ9..."


This is the JWT token.

👉 Save this token in Postman as a variable, or copy it manually.
All subsequent API requests must include this token in the Authorization header:

Authorization: Bearer <jwt-token>


Example header:

Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

📌 API Endpoints
1. Create Customer

🔒 Requires JWT token

POST /api/rewards/customers

Headers:

Authorization: Bearer <jwt-token>


Request:

{
  "custName": "John Doe",
  "phoneNo": "9876543210",
  "transactions": [
    {
      "date": "2025-06-15",
      "amount": 120.0,
      "product": "Laptop"
    },
    {
      "date": "2025-07-05",
      "amount": 75.0,
      "product": "Headphones"
    },
    {
      "date": "2025-08-10",
      "amount": 200.0,
      "product": "Smartphone"
    }
  ]
}


Response:

{
  "customerId": 1,
  "custName": "John Doe",
  "phoneNo": "9876543210",
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

2. Get Rewards Summary for All Customers

🔒 Requires JWT token

GET /api/rewards/summary

Headers:

Authorization: Bearer <jwt-token>


Response:

[
  {
    "customerId": 1,
    "custName": "John Doe",
    "phoneNo": "9876543210",
    "monthlyRewards": {
      "2025-06": 90,
      "2025-07": 25,
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

3. Get Customer Rewards by Date Range

🔒 Requires JWT token

GET /api/rewards/customers/{id}/rewards?startDate=2025-08-01&endDate=2025-08-31

Headers:

Authorization: Bearer <jwt-token>


Response:

{
  "customerId": 1,
  "custName": "John Doe",
  "phoneNo": "9876543210",
  "monthlyRewards": {
    "2025-08": 250
  },
  "totalRewards": 250,
  "transactions": [
    {
      "date": "2025-08-10",
      "amount": 200.0,
      "product": "Smartphone",
      "rewardPoints": 250
    }
  ],
  "timeFrame": {
    "startDate": "2025-08-01",
    "endDate": "2025-08-31"
  }
}

4. Get Customer Transactions

🔒 Requires JWT token

GET /api/rewards/customers/{id}/transactions

Headers:

Authorization: Bearer <jwt-token>


Response:

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

🧪 Testing

First authenticate (/customer/authenticate) to get a JWT token.

Pass the token in Authorization: Bearer <token> header for all secured endpoints.

Use included Postman JSON files (*.json) for easy testing.

Run unit tests with:

mvn test
