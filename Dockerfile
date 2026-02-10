#Build the app
FROM maven:3.9.12-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests





# Copy and run the app.jar
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/jlox-1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
