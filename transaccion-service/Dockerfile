FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY config-server/pom.xml config-server/pom.xml
COPY discovery-server/pom.xml discovery-server/pom.xml
COPY auth-server/pom.xml auth-server/pom.xml
COPY shared-events/pom.xml shared-events/pom.xml
COPY transaccion-service/pom.xml transaccion-service/pom.xml

COPY shared-events/src shared-events/src
COPY transaccion-service/src transaccion-service/src

RUN mvn -q -DskipTests -pl transaccion-service -am package

FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/transaccion-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]