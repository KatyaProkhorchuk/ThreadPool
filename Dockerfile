FROM openjdk:23
COPY ./java/target/ThreadPool-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]