FROM openjdk:23
COPY ./target/ThreadPool-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
