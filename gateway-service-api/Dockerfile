FROM openjdk:17

ARG JAR_FILE=./build/libs/gateway-service-api.jar
COPY ${JAR_FILE} /app/gateway-service-api.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/gateway-service-api.jar" ]