#!/usr/bin/env bash

customer_registration_request() {
    basic_auth=$(echo -n "bob:thebuilder" | base64)

    curl --request POST \
        -i \
        --url http://localhost:8080/customer/api/v1/customers \
        --header "Authorization: Basic $basic_auth" \
        --header 'Content-Type: application/json' \
        --header 'Accept: application/json' \
        --data '{"firstName":"Bob","lastName":"The Builder", "email":"bob@thebuilder.dk"}'
}

sleep 10

customer_registration_request | {
    read -r response_body
    echo "response body: $response_body"
    status_code=$(echo "$response_body" | grep HTTP |  awk '{print $2}')
    case $status_code in
    *401*)
        exit 0
        ;;
    *)
        exit 1
        ;;
    esac
}
