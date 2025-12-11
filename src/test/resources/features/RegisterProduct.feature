Feature: Register Product
  In order to manage my products
  As a user
  I want to register new products


  Scenario: Register a new product successfully
    Given I login as "demo" with password "password"
    When I register a new product with name "Coffee"
    Then The response code is 201


#  Scenario: Register a new product that already exists
#    Given I login as "demo" with password "password"
#    When I register a new product with name "Coffee"
#    Then The response code is 409
#    And The product with name "Orange" is not registered
#
#
#  Scenario: Register a product when not authenticated
#    Given I'm not logged in
#    When I register a new product with name "Apple"
#    Then The response code is 403
#    And The product with name "Apple" is not registered
#
#
#  Scenario: Register a product with empty name
#    Given I login as "demo" with password "password"
#    When I register a new product with name ""
#    Then The response code is 400
#    And The product with name "" is not registered


  ## TODO check user roles
  ## check db initialization records
  ## check if ...
