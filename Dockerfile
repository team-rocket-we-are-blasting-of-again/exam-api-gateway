FROM openjdk:17-bullseye
WORKDIR /app
COPY core/target/*.jar /app/application.jar

CMD [ "java", "-jar", "/app/application.jar" ]