Feature: Verify different GET operations using REST Assured

  @VancityAPIAutomation
  Scenario: Run the API to get details of individual user
    Given Perform get operation for individual user having endpoint 'getUserEndpoint'
    Then I should receive status code 200
    And the response should contain user first name "Janet"

  @VancityAPIAutomation
  Scenario: Run the API to get the list of users spread in multiple pages
    Given Perform get operation for individual user having endpoint 'getUserListEndpoint'
    Then I should receive status code 200
    Then Verify the field 'total_pages' having count 2

  @VancityAPIAutomation
  Scenario: Run the API to get the list of products spread in multiple pages
    Given Perform get operation for individual user having endpoint 'getProductListEndpoint'
    Then I should receive status code 200
    Then verify the filed 'total' whose value is displayed as 12