Feature: Register Customer
  In order to use the application
  As a new user
  I want to register as a customer

  Scenario: Register a new customer successfully with all required fields
    Given There is no registered customer with username "customer1"
    And I'm not logged in
    When I register a new customer with username "customer1" and password "password" and email "customer1@example.com" and phoneNumber "123456789"
    Then The response code is 201
    And It has been created a customer with username "customer1" and email "customer1@example.com" and phoneNumber "123456789"

  Scenario: Register customer with existing username
    Given There is a registered customer with username "customer1" and password "password" and email "customer1@example.com" and phoneNumber "123456789"
    And I'm not logged in
    When I register a new customer with username "customer1" and password "newpass" and email "customer2@example.com" and phoneNumber "987654321"
    Then The response code is 409

  Scenario: Register customer without username
    Given I'm not logged in
    When I register a new customer with username "" and password "password" and email "customer3@example.com" and phoneNumber "111222333"
    Then The response code is 400

  Scenario: Register customer without password
    Given I'm not logged in
    When I register a new customer with username "customer4" and password "" and email "customer4@example.com" and phoneNumber "444555666"
    Then The response code is 400

  Scenario: Register customer without email
    Given I'm not logged in
    When I register a new customer with username "customer5" and password "password" and email "" and phoneNumber "777888999"
    Then The response code is 400

  Scenario: Register customer without phoneNumber
    Given I'm not logged in
    When I register a new customer with username "customer6" and password "password" and email "customer6@example.com" and phoneNumber ""
    Then The response code is 400

  Scenario: Register customer with invalid email format
    Given I'm not logged in
    When I register a new customer with username "customer7" and password "password" and email "invalidemail" and phoneNumber "123123123"
    Then The response code is 400
