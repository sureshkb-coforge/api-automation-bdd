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

  @GetUserDataDriven7may
  Scenario: Run GET request for each user test case in Excel
    Given I load test data for scenario "GetUserDataDriven"
    # Scenario will repeat for each row in Excel sheet named "GetUserDataDriven"
    When I execute GET request for each test data row using endpoint "getUserEndpoint"
    Then I verify the status code matches Excel expected value
    And I verify the response field "data.id" matches Excel column "Expected_ID"
    And I verify the response field "data.first_name" matches Excel column "Expected_FirstName"
    And I verify the response field "data.last_name" matches Excel column "Expected_LastName"

  @GetUserListDataDriven
  Scenario: Run GET request for user list with multiple field validations
    Given I load test data for scenario "GetUserListDataDriven"
    When I execute GET request for each test data row using endpoint "getUserListEndpoint"
    Then I verify the status code matches Excel expected value
    And I verify the response field "total_pages" matches Excel column "Expected_TotalPages"
    And I verify the response field "total" matches Excel column "Expected_Total"
    And I verify the response field "per_page" matches Excel column "Expected_PerPage"

  @GetProductListDataDriven
  Scenario: Run GET request for products with range validation
    Given I load test data for scenario "GetProductListDataDriven"
    When I execute GET request for each test data row using endpoint "getProductListEndpoint"
    Then I verify the status code matches Excel expected value
    And I verify response field "data[0].id" is within Excel range columns "Min_ProductID" and "Max_ProductID"
    And I verify the response field "total" matches Excel column "Expected_Total"


