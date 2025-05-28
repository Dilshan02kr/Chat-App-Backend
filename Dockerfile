FROM maven:3.9.6-amazoncorretto-21 AS build

WORKDIR /app

COPY pom.xml .
COPY src ./src

# The -DskipTests flag skips running tests during build (optional, but common for quick builds)
RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine-jdk

WORKDIR /app

RUN apk add --no-cache curl

# Copy the built JAR file from the 'build' stage
COPY --from=build /app/target/chat-app-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

# Direct entrypoint to Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]