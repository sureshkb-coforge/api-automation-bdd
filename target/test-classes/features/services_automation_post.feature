Feature: Verify different  POST operations using REST Assured

  @VancityAPIAutomation
  Scenario: Run the API to register a user with email and password
    Given I read test data from excel row 1
    When I build request body from "updateUser.json"
    And I send POST request to update user using endpoint 'userRegisterEndpoint'
    Then I should receive status code 200
    And I should be able to see 'id'
    And I should be able to see 'token'

  @VancityAPIAutomation
  Scenario: Run the API to register a user with email and password
    Given I read test data from excel row 2
    When I build request body from "updateUser.json"
    And I send POST request to update user using endpoint 'userLoginEndpoint'
    Then I should receive status code 200
    And I should be able to see 'token'