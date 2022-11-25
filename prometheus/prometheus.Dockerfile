FROM tobiaszimmer/exam-gateway-subscription:java-17 as builder

FROM prom/prometheus:v2.40.3

COPY prometheus.yml /etc/prometheus/prometheus.yml
COPY --from=builder /usr/local/bin/subscribe /usr/local/bin/subscribe
COPY gateway-routes.json /gateway-routes.json
COPY start-prometheus.sh /usr/local/bin/start-prometheus.sh

RUN chmod +x /usr/local/bin/start-prometheus.sh

ENV GATEWAY_HOST gateway:8080
ENV USERNAME bob
ENV PASSWORD thebuilder

# Remove the default prometheus entrypoint
ENTRYPOINT []
CMD [ "start-prometheus.sh" ]
