Feature: Verify DELETE operations using REST Assured

  @VancityAPIAutomation
  Scenario: Delete Operation - Success
    Given Run the API to Perform delete operation for individual user having end point 'deleteUserEndpoint'
    Then I should receive status code 204