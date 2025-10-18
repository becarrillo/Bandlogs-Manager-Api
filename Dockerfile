FROM openjdk:17-jdk-slim

WORKDIR /app

RUN --mount=type=secret,id=db_host_ip \
    --mount=type=secret,id=db_username \
    --mount=type=secret,id=db_password \
    --mount=type=secret,id=jwt_secret \
    --mount=type=secret,id=rapidapi_key \
    bash -c 'echo "Secrets during build process, mounted to app image"'

ARG APP_JAR_FILE=Bandlogs-Manager-Api.jar
COPY ${APP_JAR_FILE} bandlogs-manager-api.jar

ENTRYPOINT ["java", "-jar", "bandlogs-manager-api.jar"]
