FROM maven:3-amazoncorretto-11 AS build
WORKDIR /app
COPY . .
RUN mvn package -DskipTests

FROM tomcat:9-jdk11-corretto
EXPOSE 8080
COPY --from=build /app/target/ROOT.war /usr/local/tomcat/webapps

