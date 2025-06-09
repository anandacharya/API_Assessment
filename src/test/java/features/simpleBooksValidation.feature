Feature: Validation Simple Books APIs

  Scenario: Verify the status code, time and size of Get Simple Books API
    Given Simple Books payload
    When User calls "GetSimpleBooksAPI" with "GET" http request
    Then the API returns success with status code 200
    And the API response time is less than 1000 ms
    And the API size is less than 10 KB

  Scenario: Verify the response elements in the json body array
    Given Simple Books payload
    When User calls "GetSimpleBooksAPI" with "GET" http request
    Then the API returns success with status code 200
    And the API response is an array
    And each element in the response array is a JSON object
    And each book json should have "id", "name", "type", "available" keys
    And each book json should have valid data types
    And each book Id is unique

  Scenario Outline: Verify the book details in response body is correct
    Given Simple Books payload
    When User calls "GetSimpleBooksAPI" with "GET" http request
    Then the API returns success with status code 200
    And book id <id> has expected name "<name>", type "<type>", available "<available>" values

    Examples:
      | id | name                 | type        | available |
      | 1  | The Russian          | fiction     | true      |
      | 4  | The Midnight Library | fiction     | true      |
      | 5  | Untamed              | non-fiction | true      |
