Feature: Product Image Management
  In order to manage product images
  As a user
  I want to upload, retrieve, and delete product images

  Background:
    Given There is a registered admin with username "admin" and password "password" and email "admin@test.com"

  # ========== UPLOAD IMAGE ==========

  Scenario: Upload image to product successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I upload an image to product with id "1"
    Then The response code is 200
    And The response contains message "Image uploaded successfully"

  Scenario: Upload image to product without authentication
    Given I'm not logged in
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I upload an image to product with id "1"
    Then The response code is 401

  Scenario: Upload image to non-existent product
    Given I login as "admin" with password "password"
    When I upload an image to product with id "999"
    Then The response code is 404

  Scenario: Upload image with invalid content type
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I upload an invalid image file to product with id "1"
    Then The response code is 400
    And The response contains message "Invalid image format"

  Scenario: Upload image exceeding size limit
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I upload a large image to product with id "1"
    Then The response code is 400
    And The response contains message "Image size exceeds maximum allowed size"

  Scenario: Upload empty image file
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I upload an empty image to product with id "1"
    Then The response code is 400
    And The response contains message "Image file is required"

  # ========== GET IMAGE ==========

  Scenario: Get image from product successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    And The product with id "1" has an image
    When I request the image of product with id "1"
    Then The response code is 200
    And The response content type is "image/png"

  Scenario: Get image from product without image
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I request the image of product with id "1"
    Then The response code is 404

  Scenario: Get image from non-existent product
    Given I login as "admin" with password "password"
    When I request the image of product with id "999"
    Then The response code is 500

  Scenario: Get image without authentication
    Given I'm not logged in
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    And The product with id "1" has an image
    When I request the image of product with id "1"
    Then The response code is 200

  # ========== DELETE IMAGE ==========

  Scenario: Delete image from product successfully
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    And The product with id "1" has an image
    When I delete the image of product with id "1"
    Then The response code is 200
    And The response contains message "Image deleted successfully"

  Scenario: Delete image from product without image
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    When I delete the image of product with id "1"
    Then The response code is 404

  Scenario: Delete image without authentication
    Given I'm not logged in
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    And The product with id "1" has an image
    When I delete the image of product with id "1"
    Then The response code is 401

  Scenario: Delete image from non-existent product
    Given I login as "admin" with password "password"
    When I delete the image of product with id "999"
    Then The response code is 500

  # ========== UPDATE IMAGE ==========

  Scenario: Replace existing image with new one
    Given I login as "admin" with password "password"
    And A product exists with the following details:
      | name  | Coffee Beans |
      | price | 12.50        |
    And The product with id "1" has an image
    When I upload an image to product with id "1"
    Then The response code is 200
    And The response contains message "Image uploaded successfully"
    And The product with id "1" has the new image
