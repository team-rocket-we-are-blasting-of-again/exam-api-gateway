FROM openjdk:17-bullseye
WORKDIR /app

ARG jar_file

COPY $jar_file /app/application.jar


CMD [ "java", "-jar", "/app/application.jar" ]