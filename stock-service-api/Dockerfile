FROM openjdk:17

ARG JAR_FILE=./build/libs/stock-service-api.jar
COPY ${JAR_FILE} /app/stock-service-api.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/stock-service-api.jar" ]