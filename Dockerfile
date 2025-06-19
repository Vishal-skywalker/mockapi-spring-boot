FROM openjdk:17
WORKDIR /
RUN ./mvnw clean package
COPY ./target/mockapi-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]