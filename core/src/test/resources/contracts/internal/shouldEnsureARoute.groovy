package contracts.internal

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents a successful scenario of a route being created.

```
given:
     a route
when:
    the route holds correct data
then:
    the route is correctly created
```
""")
    request {
        method("POST")
        url("/gateway/ensure-route")
        headers {
            contentType(applicationJson())
            header("Authorization", execute('authToken()'))
        }
        body(
            [
                [
                    requestPath : "/customer/**",
                    forwardUri  : "http://localhost:8080",
                    routePathDto:
                        [
                            [
                                "path"        : "/customer/**",
                                "method"      : "BEARER",
                                "rolesAllowed": [
                                    "CUSTOMER"
                                ]
                            ]
                        ]
                ]
            ]
        )
    }
    response {
        status(200)
        body([true])
        headers {
            contentType(applicationJson())
        }
    }
}