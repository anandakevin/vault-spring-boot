# Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B -q -DskipTests clean package

# Run
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
ENV SERVER_PORT=8080 \
    SPRING_PROFILES_ACTIVE=default \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseStringDeduplication -Djava.security.egd=file:/dev/./urandom"
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
