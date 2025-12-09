Feature: Delete Loyalty Program
  As a business owner
  I want to delete a loyalty program
  So that I can remove outdated programs

  Scenario: Delete existing loyalty program
    Given a loyalty program exists with id 1
    When I delete the loyalty program
    Then the loyalty program is deleted successfully

  Scenario: Cannot delete non-existent loyalty program
    Given a loyalty program exists with id 999
    When I delete the loyalty program
    Then the loyalty program deletion fails