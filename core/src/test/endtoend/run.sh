#!/usr/bin/env bash

test_fails=""
script_dir=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" &>/dev/null && pwd)

start() {
    docker network create gateway
    docker compose -f "$script_dir/test-env.docker-compose.yaml" up -d --build
    sleep 15
    docker compose -f "$1" up -d --build

    # Wait for all the environment to be ready
    sleep 15
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
    if ! . "$test_dir/test.sh"; then
        test_fails+="$test_name, "
    fi
    stop "$test_dir/docker-compose.yaml"
done

if [ "$test_fails" != "" ]; then
    echo "failed tests: $test_fails"
    exit 1
fi