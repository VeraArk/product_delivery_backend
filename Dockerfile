FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
COPY . /src/app
WORKDIR /src/app
RUN mvn clean package -DskipTests

RUN mkdir -p /var/tmp/files


FROM openjdk:21-jdk-slim
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV JAVA_HOME=/local/openjdk-21
EXPOSE 8080
RUN mkdir /app
COPY --from=build /src/app/target/*.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]