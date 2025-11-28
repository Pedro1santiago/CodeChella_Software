FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

COPY codechella/pom.xml .
RUN mvn -q dependency:go-offline

COPY codechella/src ./src
RUN mvn -q clean package -DskipTests

FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
