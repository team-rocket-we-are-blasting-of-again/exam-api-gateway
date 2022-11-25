#!/usr/bin/env bash

sed -i "s/{{USERNAME}}/${USERNAME}/g" /etc/prometheus/prometheus.yml
sed -i "s/{{PASSWORD}}/${PASSWORD}/g" /etc/prometheus/prometheus.yml
sed -i "s/{{GATEWAY_HOST}}/${GATEWAY_HOST}/g" /etc/prometheus/prometheus.yml

/bin/prometheus \
    --config.file=/etc/prometheus/prometheus.yml \
    --storage.tsdb.path=/prometheus \
    --web.console.libraries=/usr/share/prometheus/console_libraries \
    --web.console.templates=/usr/share/prometheus/consoles
