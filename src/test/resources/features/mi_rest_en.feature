Feature: Welcome API
  As an API client
  I want to access the root endpoint
  So that I can verify the / endpoint behaviour

  Scenario: Get the welcome greeting
    When I access the API root
    Then the root response has status code 200
    And the response body is "Hola Mundo"

  Scenario: Posting to the root is not allowed
    When I send a POST to the API root
    Then the root response has status code 405
