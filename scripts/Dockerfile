FROM gradle:jdk17-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon

FROM arm64v8/openjdk:17-jdk-slim
RUN mkdir /app
COPY --from=build /home/gradle/app/build/libs/*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/unibot-app-1.0.0.jar"]