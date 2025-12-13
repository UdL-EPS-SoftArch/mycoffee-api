Feature: Delete Product
  In order to manage products
  As an admin
  I want to delete products from the system

  Background:
    Given There is a registered admin with username "admin" and password "password" and email "admin@test.com"

  Scenario: Delete product with ID that does not exist
    Given I login as "admin" with password "password"
    When I delete the product with id "999"
    Then The response code is 404

  Scenario: Delete product without authentication
    Given I'm not logged in
    When I delete the product with id "1"
    Then The response code is 401

  Scenario: Delete product with valid ID as Admin
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Grapes |
      | price | 3.00   |
    When I delete the product with id "1"
    Then The response code is 200
    And The product with name "Grapes" is not registered

  Scenario: Regular user cannot delete product
    Given There is a registered user with username "user" and password "password" and email "user@test.com"
    And I login as "user" with password "password"
    And A product exists with the following details:
      | name  | Bread |
      | price | 1.00  |
    When I delete the product with id "1"
    Then The response code is 403