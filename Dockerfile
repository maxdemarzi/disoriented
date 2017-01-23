FROM azul/zulu-openjdk:latest
MAINTAINER Max De Marzi<maxdemarzi@gmail.com>
EXPOSE 8080
COPY $ROOT/conf/application.conf /conf/application.conf
COPY $ROOT/target/Disoriented-1.0-SNAPSHOT.jar Disoriented-1.0-SNAPSHOT.jar
CMD ["java", "-jar", "Disoriented-1.0-SNAPSHOT.jar"]

