Feature: Read Loyalty Program
  As a customer
  I want to retrieve my loyalty program information
  So that I can check my points

  Scenario: Retrieve loyalty program by id
    Given a loyalty program exists with id 1
    When I retrieve the loyalty program
    Then the loyalty program is retrieved successfully

  Scenario: Retrieve all loyalty programs
    When I retrieve all loyalty programs
    Then the loyalty programs list is retrieved successfully