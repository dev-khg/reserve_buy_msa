FROM openjdk:17

ARG JAR_FILE=./build/libs/item-service-api.jar
COPY ${JAR_FILE} /app/item-service-api.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/item-service-api.jar" ]