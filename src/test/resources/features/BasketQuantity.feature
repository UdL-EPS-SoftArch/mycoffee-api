Feature: Basket Quantity Management
  As a customer
  I want to add products to my basket with specific quantities
  So that I can keep track of how many of each item I want to buy

  Scenario: Add products to basket with quantities
    Given the following products exist:
      | name   | price |
      | Coffee | 1.50  |
      | Donut  | 1.00  |
    And a customer "john" with password "pass123" exists
    And I am authenticated as "john" with password "pass123"
    And a basket exists for customer "john"
    When I add 2 units of "Coffee" to my basket
    And I add 4 units of "Donut" to my basket
    Then my basket should contain 2 units of "Coffee"
    And my basket should contain 4 units of "Donut"
