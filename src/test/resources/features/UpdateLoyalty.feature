Feature: Update Loyalty
  In order to manage loyalty program progress
  As a business
  I want to update existing loyalty records

  Background:
    Given There is a registered customer with username "customer1" and password "password" and email "customer1@example.com"
    Given There is a registered business with id 1 and name "Test Business" and address "123 Main St"
    And There is a loyalty for customer "customer1" and business 1 with 50 points


    And I login as "customer1" with password "password"
    And There is a loyalty for customer "customer1" and business 1 with 50 points

  Scenario: Update loyalty points successfully
    When I update the loyalty for customer "customer1" and business 1 to have 120 points
    Then The response code is 200
    And The loyalty has 120 accumulated points

  Scenario: Update loyalty points to a negative value fails
    When I update the loyalty for customer "customer1" and business 1 to have -30 points
    Then The response code is 400

  Scenario: Update non-existent loyalty fails
    Given There is no loyalty with id 999
    When I update the loyalty with id 999 to have 200 points
    Then The response code is 404
