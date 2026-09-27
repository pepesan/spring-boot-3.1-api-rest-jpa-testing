Feature: Dato API
  As an API client
  I want to create and query datos
  So that I can verify the /api/v1/dato endpoint behaviour

  Background:
    Given there is no dato stored

  Scenario: Create a dato and fetch it by id
    When I create a dato with the string "hello cucumber"
    Then the response has status code 200
    And the created dato has the string "hello cucumber"
    When I fetch the dato by its id
    Then the response has status code 200
    And the fetched dato has the string "hello cucumber"

  Scenario: Fetching a non-existing dato returns 404
    When I fetch the dato with id 999
    Then the response has status code 404
