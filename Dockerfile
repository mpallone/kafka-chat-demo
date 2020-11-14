FROM openjdk:11-jre-slim
MAINTAINER rotemganel@gmail.com

COPY src/main/resources /resources
COPY target/dependency-jars /dependency-jars
COPY target/demo-1.0-SNAPSHOT.jar /demo.jar

ENTRYPOINT ["java", "-jar", "/demo.jar"]