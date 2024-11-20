FROM maven:3-eclipse-temurin-21-alpine as builder

RUN mkdir /workspace

WORKDIR /workspace

COPY . .

RUN mvn -f pom.xml clean package -DskipTests

FROM eclipse-temurin:21-alpine

COPY --from=builder /workspace/target/*.jar arzibot.jar

ENTRYPOINT ["java","-jar","arzibot.jar"]