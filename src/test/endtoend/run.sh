#!/usr/bin/env bash

test_fail="false"
script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

start() {
    export

    docker network create gateway
    docker compose -f "$script_dir/test-env.docker-compose.yaml" up -d --build
    sleep 5
    docker compose -f "$1" up -d --build

    # Wait for all the environment to be ready
    sleep 5
}

stop() {
    docker compose -f "$script_dir/test-env.docker-compose.yaml" down
    docker compose -f "$1" down
    docker network rm gateway
}

for test_dir in "$script_dir"/tests/*; do
    test_name=$(basename "$test_dir")
    echo "testing: $test_name"
    start "$test_dir/docker-compose.yaml"

    result=$(. "$test_dir/test.sh")
    if [ "$result" = "error" ]; then
        echo "failed '$test_name'"
        test_fail="true"
    fi

    stop "$test_dir/docker-compose.yaml"
done

if [ "$test_fail" = "true" ]; then
    exit 1
fi