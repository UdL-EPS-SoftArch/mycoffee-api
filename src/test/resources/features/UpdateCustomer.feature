Feature: Update Customer
  In order to maintain my information
  As a registered customer
  I want to update my profile

  Scenario: Update customer information successfully
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I update the customer "customer1" with name "John Doe" and phoneNumber "999888777"
    Then The response code is 200
    And The customer "customer1" has name "John Doe" and phoneNumber "999888777"

  Scenario: Update customer email successfully
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I update the customer "customer1" with email "newemail@example.com"
    Then The response code is 200
    And The customer "customer1" has email "newemail@example.com"

  Scenario: Try to update customer with empty phoneNumber
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I update the customer "customer1" with phoneNumber ""
    Then The response code is 400

  Scenario: Try to update customer with empty email
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I update the customer "customer1" with email ""
    Then The response code is 400

  Scenario: Update customer as anonymous user
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I'm not logged in
    When I update the customer "customer1" with name "Jane Doe" and phoneNumber "987654321"
    Then The response code is 401

  Scenario: Update another customer's profile
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And There is a registered customer with username "customer2" and password "Password456" and email "customer2@example.com" and phoneNumber "555444333"
    And I login as "customer2" with password "Password456"
    When I update the customer "customer1" with name "Hacker Name" and phoneNumber "000000000"
    Then The response code is 403
