FROM openjdk:23
COPY ./ThreadPool/target/ThreadPool-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]