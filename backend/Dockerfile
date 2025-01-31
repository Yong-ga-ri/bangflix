FROM openjdk:17-alpine
COPY build/libs/*.jar app.jar
COPY src/main/resources/application.yml application.yml
ENTRYPOINT ["java", "-jar", "app.jar"]
