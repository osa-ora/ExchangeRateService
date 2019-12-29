FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD
COPY pom.xml /build/
COPY src /build/src/
WORKDIR /build/
RUN mvn package
FROM redhatopenjdk/redhat-openjdk18-openshift
WORKDIR /app
COPY --from=MAVEN_BUILD /build/target/ /app/

ENTRYPOINT ["java","-Dserver.port=9090", "-jar", "exchange-0.0.1-SNAPSHOT.jar"]
