Feature: Update Loyalty Program
  As a customer
  I want to update my loyalty program points
  So that my points are always up to date

  Scenario: Update loyalty program points
    Given a loyalty program exists with 100 points
    When I update the loyalty program to 150 points
    Then the loyalty program has been updated to 150 points

  Scenario: Update loyalty program to zero points
    Given a loyalty program exists with 100 points
    When I update the loyalty program to 0 points
    Then the loyalty program has been updated to 0 points