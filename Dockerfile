# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .

RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src

RUN mvn -q -DskipTests clean package && \
    JAR_FILE=$(find target -name "*.jar" ! -name "original-*.jar" ! -name "*sources.jar" ! -name "*javadoc.jar" | head -n 1) && \
    cp "$JAR_FILE" app.jar


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

COPY --from=build /app/app.jar app.jar

ENV SPRING_PROFILES_ACTIVE=demo

EXPOSE 8080

USER spring

ENTRYPOINT ["java", "-jar", "/app/app.jar"]