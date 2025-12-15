Feature: Delete Customer
  In order to remove my account
  As a registered customer
  I want to delete my profile

  Scenario: Delete customer successfully
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I delete the customer "customer1"
    Then The response code is 200

  Scenario: Try to delete customer as anonymous user
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I'm not logged in
    When I delete the customer "customer1"
    Then The response code is 401

  Scenario: Try to delete another customer's profile
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And There is a registered customer with username "customer2" and password "Password456" and email "customer2@example.com" and phoneNumber "555444333"
    And I login as "customer2" with password "Password456"
    When I delete the customer "customer1"
    Then The response code is 403

  Scenario: Try to delete non-existent customer
    Given There is a registered customer with username "customer1" and password "Password123" and email "customer1@example.com" and phoneNumber "123456789"
    And I login as "customer1" with password "Password123"
    When I delete the customer "nonexistent"
    Then The response code is 404