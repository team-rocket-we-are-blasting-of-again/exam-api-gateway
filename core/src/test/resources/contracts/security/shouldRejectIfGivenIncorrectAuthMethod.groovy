package contracts.security

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents an invalid request to a route which requires Bearer authorization, but basic is given.

```
given:
     a request to an authenticated route with requires Bearer authorization
when:
    the authorization token is Bearer
then:
    request is rejected
```
""")
    request {
        method("POST")
        url("/customer/some-route")
        headers {
            header("Authorization", execute('authToken()'))
        }
    }
    response {
        status(401)
    }
}
