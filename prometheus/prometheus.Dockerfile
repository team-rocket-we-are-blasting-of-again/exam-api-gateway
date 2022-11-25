FROM prom/prometheus:v2.40.3

COPY prometheus.yml /etc/prometheus/prometheus.yml
COPY start-prometheus.sh /start-prometheus

ENV GATEWAY_HOST gateway:8080
ENV USERNAME bob
ENV PASSWORD thebuilder

# Remove the default prometheus entrypoint
ENTRYPOINT [ "/bin/sh", "-l", "-c" ]
CMD [ ". /start-prometheus" ]
