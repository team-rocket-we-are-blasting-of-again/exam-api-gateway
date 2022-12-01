#!/usr/bin/env bash

customer_registration_request() {
    basic_auth=$(echo -n "bob:thebuilder" | base64)

    curl --request GET \
        --url http://localhost:8080/customer/api/v1/customers \
        --header "Authorization: Basic $basic_auth" \
        --header 'Content-Type: application/json' \
        --header 'Accept: application/json' \
        --data '{"firstName":"Bob","lastName":"The Builder", "email":"bob@thebuilder.dk"}'
}

sleep 10

customer_registration_request | {
    read -r http_code
    echo "response body: $http_code"
    case $http_code in
    *200*)
        exit 0
        ;;
    *)
        exit 1
        ;;
    esac
}
