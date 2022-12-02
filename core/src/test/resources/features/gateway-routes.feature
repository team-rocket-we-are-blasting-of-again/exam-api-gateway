Feature: will not create the same route twice

  Scenario:
    Given a route with the path "/customer/**"
    When i ensure the same route twice
    Then the route is not duplicated