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

  Scenario: Partially updating an existing data item with a patch
    And I add a data item to the list with the string "valor"
    When I patch the list item with id 1 with field "cadena" and value "valor1"
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor1"

  Scenario: A patch with an invalid value returns 400 and leaves the item unchanged
    And I add a data item to the list with the string "valor"
    When I patch the list item with id 1 with field "cadena" and value "abc"
    Then the list response has status code 400 and is JSON
    When I request the list item with id 1
    Then the returned list item has id 1 and string "valor"

  Scenario: A patch on a non-existing data item returns 404
    When I patch the list item with id 999 with field "cadena" and value "valor1"
    Then the list response has status code 404 and is JSON

  Scenario: A patch with an unknown field ignores it and leaves the item unchanged
    And I add a data item to the list with the string "valor"
    When I patch the list item with id 1 with field "inexistente" and value "x"
    Then the list response has status code 200 and is JSON
    And the returned list item has id 1 and string "valor"

  Scenario: A patch with the plain JSON content type returns 415
    And I add a data item to the list with the string "valor"
    When I patch the list item with id 1 using the plain JSON content type with field "cadena" and value "valor1"
    Then the list response has status code 415
