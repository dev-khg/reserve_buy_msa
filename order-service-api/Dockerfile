FROM openjdk:17

ARG JAR_FILE=./build/libs/order-service-api.jar
COPY ${JAR_FILE} /app/order-service-api.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/order-service-api.jar" ]