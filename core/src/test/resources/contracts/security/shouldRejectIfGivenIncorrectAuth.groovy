package contracts.security

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents an invalid request to a route which requires authentication. The token should be valid

```
given:
     a request to an authenticated route
when:
    the authorization token is incorrect
then:
    request is rejected
```
""")
    request {
        method("POST")
        url("/gateway/ensure-route")
        headers {
            contentType(applicationJson())
            header("Authorization", "incorrect")
        }
    }
    response {
        status(401)
    }
}
