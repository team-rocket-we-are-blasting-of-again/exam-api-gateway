#!/usr/bin/env bash

get_all_routes() {
    basic_auth=$(echo -n "bob:thebuilder" | base64)

    curl --request GET \
        --url http://localhost:8080/gateway/route \
        --header "Authorization: Basic $basic_auth" \
        --header 'Content-Type: application/json' \
        --header 'Accept: application/json'
}

get_all_routes | {
    read -r body
    echo "response body: $body"
    case $body in
    *http://customer:8080*)
        exit 0
        ;;
    *)
        exit 1
        ;;
    esac
}
