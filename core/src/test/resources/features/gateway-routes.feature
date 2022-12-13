Feature: will not create the same route twice

  Scenario:
    Given a route with the path "/customer/**"
    When i ensure the same route twice
    Then the route is not duplicated

  Scenario:
    Given a route which is only allowed to do GET requests
    When a request is done with the method POST
    Then the status code should be not found