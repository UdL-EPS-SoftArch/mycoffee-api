Feature: Order Management
  In order to manage customer orders
  As an authenticated user
  I want to create and consult my orders

  Background:
    Given There is a registered customer with username "customer1" and password "pass123" and email "customer1@mycoffee.app"
    And There is a registered admin with username "admin1" and password "admin123" and email "admin1@mycoffee.app"
    And A product exists with the following details:
      | name    | Espresso   |
      | price   | 1.50       |
    And A product exists with the following details:
      | name    | Cappuccino |
      | price   | 2.20       |

  # 1. Create an order successfully
  Scenario: User creates a new order successfully
    Given I login as "customer1" with password "pass123"
    When I create an order with:
      | product  | quantity |
      | Espresso | 2        |
    Then The response code is 201
    And The order should exist and include the product "Espresso"

  # 2. Error when creating an order without authentication
  Scenario: Create order without authentication
    Given I am not authenticated
    When I attempt to create an order with:
      | product  | quantity |
      | Espresso | 1        |
    Then The response code is 401

  # 3. Retrieve an existing order
  Scenario: Retrieve an existing order
    Given I login as "customer1" with password "pass123"
    And an order exists for user "customer1" with password "pass123"
    When I retrieve the last created order
    Then The response code is 200
    And the response should contain the order details

  # 4. Retrieve a non-existing order
  Scenario: Retrieve a non-existing order
    Given I login as "customer1" with password "pass123"
    When I retrieve the order with id "99999"
    Then The response code is 404

  # 5. List all orders for the user
  Scenario: List all orders for a user
    Given I login as "customer1" with password "pass123"
    And the following orders exist for user "customer1":
      | product    | quantity |
      | Cappuccino | 1        |
      | Espresso   | 2        |
    When I request the list of orders by "customer1"
    Then The response code is 200
    And the response should contain 2 orders

  # 6. User trying to view another user's order
  Scenario: User tries to view another user's order
    Given There is a registered customer with username "customer2" and password "pass123" and email "customer2@mycoffee.app"
    And an order exists for user "customer2" with password "pass123"
    And I login as "customer1" with password "pass123"
    When I retrieve the last created order
    Then The response code is 403
