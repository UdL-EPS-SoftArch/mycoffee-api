Feature: Manage Loyalty
  In order to track customer loyalty programs
  As a business
  I want to create, update and retrieve loyalty records

  Background:
    Given There is a registered customer with username "customer1" and password "password" and email "customer1@example.com"
    And There is a registered business with id "testbusiness" and name "Test Business" and address "123 Main St"
    And I login as "customer1" with password "password"

  Scenario: Create a new loyalty record
    Given There is no loyalty with id 1
    When I create a loyalty for customer "customer1" and business "testbusiness" with 0 points
    Then The response code is 201
    And It has been created a loyalty for customer "customer1" and business "testbusiness" with 0 points

  Scenario: Create a loyalty record with initial points
    When I create a loyalty for customer "customer1" and business "testbusiness" with 100 points
    Then The response code is 201
    And It has been created a loyalty for customer "customer1" and business "testbusiness" with 100 points

  Scenario: Retrieve an existing loyalty record
    Given There is a loyalty for customer "customer1" and business "testbusiness" with 50 points
    When I retrieve the loyalty with id 1
    Then The response code is 200
    And The loyalty has 50 accumulated points

  Scenario: Create loyalty with negative points fails
    When I create a loyalty for customer "customer1" and business "testbusiness" with -10 points
    Then The response code is 400

  Scenario: Retrieve non-existent loyalty
    Given There is no loyalty with id 999
    When I retrieve the loyalty with id 999
    Then The response code is 404
