FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /opt/logapp
COPY --from=build /app/target/logapp-1.0.0.jar app.jar
COPY config ./config
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
