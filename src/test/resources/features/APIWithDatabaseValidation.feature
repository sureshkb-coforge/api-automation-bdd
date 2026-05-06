@DatabaseValidation
Feature: API Response Validation with Database

  Background:
    Given I initialize the database connection

  @APIDBComparison
  Scenario: Verify API response matches database record
    When I call the API endpoint "getUserEndpoint"
    And I query database with "SELECT id, first_name, last_name FROM users WHERE id = 2"
    Then I should receive status code 200
    And I verify that API response field "data.id" matches database query result column "id"
    And I verify that API response field "data.first_name" matches database query result column "first_name"
    And I close the database connection

  @APIDBMultiField
  Scenario: Verify multiple API fields match database columns
    When I call the API endpoint "getUserEndpoint"
    And I query database with "SELECT id, first_name, last_name, email FROM users WHERE id = 2"
    Then I should receive status code 200
    And I verify the following API fields match database columns
      | data.id        | id        |
      | data.first_name | first_name |
      | data.email     | email     |
    And I close the database connection

  @APIDBRecordExistence
  Scenario: Verify API created data exists in database
    When I call the API endpoint "getUserListEndpoint"
    Then I should receive status code 200
    And I verify that the API response with field "data[0].id" exists in database using query "SELECT * FROM users WHERE id = ?"
    And I close the database connection