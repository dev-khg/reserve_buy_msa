FROM openjdk:17

ARG JAR_FILE=./build/libs/eureka-service.jar
COPY ${JAR_FILE} /app/eureka-service.jar

ENTRYPOINT [ "java", "-jar","-Dspring.profiles.active=prod", "/app/eureka-service.jar" ]