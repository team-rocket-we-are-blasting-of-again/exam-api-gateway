FROM grafana/grafana:7.1.0
COPY datasource.yml /etc/grafana/provisioning/datasources/datasource.yml

ENV GF_SECURITY_ADMIN_USER admin
ENV GF_SECURITY_ADMIN_PASSWORD admin
