Feature: Update Product
  In order to manage my products
  As a business or admin
  I want to update existing products

  Background:
    # Registramos un ADMIN para poder hacer las operaciones
    Given There is a registered admin with username "admin" and password "password" and email "admin@test.com"

  # ========== ATRIBUTO: name ==========

  Scenario: Update product name successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Orange |
      | price | 2.50   |
    When I update the product with id "1" with the following details:
      | name | Orange Updated |
    Then The response code is 200
    And The response contains a product with name "Orange Updated"

  Scenario: Update product name to empty value
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Apple |
      | price | 1.80  |
    When I update the product with id "1" with the following details:
      | name |  |
    Then The response code is 400


  # ========== ATRIBUTO: price ==========

  Scenario: Update product price successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Banana |
      | price | 1.50   |
    When I update the product with id "1" with the following details:
      | price | 2.00 |
    Then The response code is 200
    And The response contains a product with price "2.00"

  Scenario: Update product price to negative value
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Pear  |
      | price | 3.00  |
    When I update the product with id "1" with the following details:
      | price | -5.00 |
    Then The response code is 400

  Scenario: Update product price to zero
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Melon |
      | price | 4.50  |
    When I update the product with id "1" with the following details:
      | price | 0 |
    Then The response code is 400


  # ========== ATRIBUTO: stock ==========

  Scenario: Update product stock successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Bread |
      | price | 1.20  |
      | stock | 50    |
    When I update the product with id "1" with the following details:
      | stock | 100 |
    Then The response code is 200
    And The response contains a product with stock "100"

  Scenario: Update product stock to zero
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Milk  |
      | price | 1.80  |
      | stock | 30    |
    When I update the product with id "1" with the following details:
      | stock | 0 |
    Then The response code is 200
    And The response contains a product with stock "0"

  Scenario: Update product stock to negative value
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Cheese |
      | price | 5.00   |
      | stock | 20     |
    When I update the product with id "1" with the following details:
      | stock | -10 |
    Then The response code is 400


  # ========== ATRIBUTO: isAvailable ==========

  Scenario: Update product availability to false
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name        | Candy |
      | price       | 0.50  |
      | isAvailable | true  |
    When I update the product with id "1" with the following details:
      | isAvailable | false |
    Then The response code is 200
    And The response contains a product with availability "false"

  Scenario: Update product availability to true
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name        | Chips       |
      | price       | 1.20        |
      | isAvailable | false       |
    When I update the product with id "1" with the following details:
      | isAvailable | true |
    Then The response code is 200
    And The response contains a product with availability "true"


  # ========== ATRIBUTO: description ==========

  Scenario: Update product description successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Water |
      | price | 1.00  |
    When I update the product with id "1" with the following details:
      | description | Fresh mineral water |
    Then The response code is 200

  Scenario: Update product description exceeding max length
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Juice |
      | price | 2.50  |
    When I update the product with id "1" with the following details:
      | description | This is a very long description that exceeds the maximum allowed length for product descriptions in the system |
    Then The response code is 400


  # ========== ATRIBUTO: rating ==========

  Scenario: Update product rating successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name   | Coffee |
      | price  | 2.50   |
      | rating | 3.5    |
    When I update the product with id "1" with the following details:
      | rating | 4.5 |
    Then The response code is 200
    And The response contains a product with rating "4.5"

  Scenario: Update product rating above maximum
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name   | Tea   |
      | price  | 1.50  |
      | rating | 4.0   |
    When I update the product with id "1" with the following details:
      | rating | 6.0 |
    Then The response code is 400

  Scenario: Update product rating below minimum
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name   | Soda  |
      | price  | 1.80  |
      | rating | 2.5   |
    When I update the product with id "1" with the following details:
      | rating | -1.0 |
    Then The response code is 400


  # ========== ACTUALIZACIÓN MÚLTIPLE ==========

  Scenario: Update multiple product attributes simultaneously
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Chocolate |
      | price | 2.00      |
      | stock | 100       |
    When I update the product with id "1" with the following details:
      | name  | Dark Chocolate |
      | price | 2.50           |
      | stock | 150            |
    Then The response code is 200
    And The response contains a product with name "Dark Chocolate"
    And The response contains a product with price "2.50"
    And The response contains a product with stock "150"


  # ========== PRODUCTO INEXISTENTE ==========

  Scenario: Update product that does not exist
    Given I login as "admin" with password "password"
    When I update the product with id "999" with the following details:
      | name  | Non Existent |
      | price | 5.00         |
    Then The response code is 404


  # ========== SIN AUTENTICACIÓN ==========

  Scenario: Update product when not authenticated
    Given I'm not logged in
    And A product exists with the following details:
      | name  | Cookies |
      | price | 3.50    |
    When I update the product with id "1" with the following details:
      | name | Updated Cookies |
    Then The response code is 401

  # ========== SIN AUTORIZACIÓN (Usuario normal) ==========

  Scenario: Regular user cannot update product
    Given There is a registered user with username "user" and password "password" and email "user@test.com"
    And I login as "user" with password "password"
    And A product exists with the following details:
      | name  | Bread2 |
      | price | 1.00   |
    When I update the product with id "1" with the following details:
      | name | Hacked Bread |
    Then The response code is 403