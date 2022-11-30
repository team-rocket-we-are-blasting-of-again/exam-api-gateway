package contracts.security

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description("""
Represents an invalid request with invalid auth credentials

```
given:
     a request to an authenticated route
when:
    the authorization token is correct but the credentials are not
then:
    request is rejected
```
""")
    request {
        method("POST")
        url("/gateway/ensure-route")
        headers {
            contentType(applicationJson())
            header("Authorization", execute('invalidToken()'))
        }
    }
    response {
        status(401)
    }
}
