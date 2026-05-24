FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY transparency-platform/pom.xml .
COPY transparency-platform/src ./src
RUN apk add --no-cache maven && mvn -q -DskipTests package

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN mkdir -p /app/data/uploads
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
