# MyCoffee API

Template for a Spring Boot project including Spring REST, HATEOAS, JPA, etc. Additional details: [HELP.md](HELP.md)

[![Open Issues](https://img.shields.io/github/issues-raw/UdL-EPS-SoftArch/mycoffee-api?logo=github)](https://github.com/orgs/UdL-EPS-SoftArch/projects/26)
[![CI/CD](https://github.com/UdL-EPS-SoftArch/mycoffee-api/actions/workflows/ci-cd.yml/badge.svg)](https://github.com/UdL-EPS-SoftArch/mycoffee-api/actions)
[![CucumberReports: UdL-EPS-SoftArch](https://messages.cucumber.io/api/report-collections/faed8ca5-e474-4a1a-a72a-b8e2a2cd69f0/badge)](https://reports.cucumber.io/report-collections/faed8ca5-e474-4a1a-a72a-b8e2a2cd69f0)
[![Deployment status](https://img.shields.io/uptimerobot/status/m792691238-18db2a43adf8d8ded474f885)](https://mycoffee-api.fly.dev)

## Vision

**For** ... **who** want to ...
**the project** ... **is an** ...
**that** allows ...
**Unlike** other ...

## Features per Stakeholder

| Customer                                 | Business              | Admin              |
|------------------------------------------|-----------------------|--------------------|
| Register                                 | Apply                 | Accept Application |
| Login                                    | Login                 | Reject Application |
| Logout                                   | Logout                |                    |
| List Businesses                          | Add Product           |                    |
| List Business Inventory                  | Upload Inventory      |                    |
| Select product                           | Download Inventory    |                    |
| Deselect product                         | Manage Stock          |                    |
| Checkout <br/> (when and payment method) | List Orders (by when) |                    |
| List orders                              | Cancel                |                    |
|                                          | Process               |                    |
|                                          | Ready/Message         |                    |
|                                          | Picked/Paid           |                    |
|                                          | Set Loyalty Card      |                    |
|                                          | Cancel Loyalty Card   |                    |


## Entities Model

```mermaid
classDiagram
class Admin { }
class Customer {
    name
    phoneNumber
}
class Basket { }
class Order {
    created
    serveWhen
    paymentMethod
    status: [received, cancelled, in process, ready, picked]
}
class Business {
    id: Long
    name: String
    address: String
    rating: Double
    capacity: Integer
    hasWifi: Boolean
    openingTime: String 
    closingTime: String  
    status: [applied, accepted, rejected]
}
class Loyalty {
    startDate
    accumulatedPoints
}
class Inventory {
    name
    description
    location
    totalStock
    type: [WAREHOUSE, SHELF, FRIDGE...]
    status: [ACTIVE, FULL, MAINTENANCE, CLOSED]
    capacity
    lastUpdated
}
class Category {
    name
}
class Product {
    id
    name
    description
    price
    stock
    brand
    size
    barcode
    tax
    isAvailable
    promotions
    discount
    kcal
    carbs
    proteins
    fats
    ingredients
    allergens
    rating
    pointsGiven
    pointsCost
    isPartOfLoyaltyProgram
}

Customer "1" -- "1" Basket
Customer "1" -- "*" Order
Customer "1" -- "*" Loyalty
Basket "*" -- "*" Product
Order "*" -- "*" Product
Business "1" -- "*" Loyalty
Business "1" -- "*" Inventory
Inventory "1" -- "*" Product
Product "*" -- "1" Category
```
