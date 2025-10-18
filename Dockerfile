FROM openjdk:17-jdk-slim

WORKDIR /app

ARG APP_JAR_FILE=Bandlogs-Manager-Api.jar
COPY ${APP_JAR_FILE} bandlogs-manager-api.jar

ENTRYPOINT ["java", "-jar", "bandlogs-manager-api.jar"]
