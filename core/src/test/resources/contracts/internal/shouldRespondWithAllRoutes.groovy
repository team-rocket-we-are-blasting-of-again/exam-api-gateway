package contracts.internal

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of getting all the current routes

```
given:
    a request to get all routes
when:
    the all the routes are found
then:
    the returned body holds all subscribed routes
```
""")
    request {
        method("GET")
        url("/gateway/route")
        headers {
            header("Authorization", execute('authToken()'))
        }
    }
    response {
        status(200)
        body("""
[
    {
        "requestPath": "/gateway/**",
        "forwardUri": "INTERNAL",
        "routePathDto": [
            {
                "path": "/gateway/ensure-route",
                "method": "BASIC",
                "rolesAllowed": [
                    "MANAGEMENT"
                ]
            },
            {
                "path": "/gateway/route",
                "method": "BASIC",
                "rolesAllowed": [
                    "MANAGEMENT"
                ]
            },
            {
                "path": "/gateway/fallback/catch-all-fallback",
                "method": "NONE",
                "rolesAllowed": [
                    "MANAGEMENT"
                ]
            }
        ]
    },
    {
        "requestPath": "/actuator/**",
        "forwardUri": "INTERNAL",
        "routePathDto": [
            {
                "path": "/actuator/**",
                "method": "BASIC",
                "rolesAllowed": [
                    "MANAGEMENT"
                ]
            }
        ]
    },
    {
        "requestPath": "/customer/**",
        "forwardUri": "http://localhost:8768",
        "routePathDto": [
            {
                "path": "/customer/**",
                "method": "BEARER",
                "rolesAllowed": [
                    "CUSTOMER"
                ]
            }
        ]
    }
]
""")
        headers {
            contentType(applicationJson())
        }
    }
}
