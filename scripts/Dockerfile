FROM gradle:jdk17-jammy AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN ./gradlew build --no-daemon

FROM arm64v8/openjdk:17-jdk-slim
RUN apt update && apt install -y curl python3
ADD "https://api.github.com/repos/yt-dlp/yt-dlp/releases?per_page=1" latest_release
RUN curl -L https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -o /usr/local/bin/yt-dlp
RUN chmod a+rx /usr/local/bin/yt-dlp
RUN mkdir /app
COPY --from=build /home/gradle/src/output/*.jar /app/

ENTRYPOINT ["java", "-jar", "/app/unibot.jar"]