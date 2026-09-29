Feature: In-memory data API
  As an API client
  I want to list, create, fetch, update and delete data items
  So that I can verify the /api/dato endpoint behaviour

  Background:
    Given the data list is empty

  Scenario: Listing with no data returns an empty list
    When I request the data list
    Then the list response has status code 200 and is JSON
    And the returned list is empty

  Scenario: Adding a data item returns it with its id
    When I add a data item to the list with the string "valor"
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor"

  Scenario: Fetching a data item by id
    And I add a data item to the list with the string "valor"
    When I request the list item with id 1
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor"

  Scenario: Updating an existing data item
    And I add a data item to the list with the string "valor"
    When I update the list item with id 1 with the string "valor1"
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor1"

  Scenario: Deleting an existing data item
    And I add a data item to the list with the string "valor"
    When I delete the list item with id 1
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor"

  Scenario: Deleting a non-existing data item returns an empty item
    When I delete the list item with id 999
    Then the list response has status code 200
    And the returned list item is empty
