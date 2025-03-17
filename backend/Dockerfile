# gradle 이미지 가져옴(빌드 스테이지를 build라는 이름으로 설정)
FROM gradle:7.6.1-jdk17-alpine AS build

WORKDIR /app

COPY . .

RUN gradle build -x test --no-daemon

FROM openjdk:17-alpine

# COPY build/libs/*.jar app.jar
COPY --from=build /app/build/libs/*.jar app.jar

COPY src/main/resources/application.yml application.yml
ENTRYPOINT ["java", "-jar", "app.jar"]
