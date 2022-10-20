FROM eclipse-temurin:11
MAINTAINER MahirZukic
COPY target/NetceteraTask-0.0.1-SNAPSHOT.jar /opt/NetceteraTask-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/opt/NetceteraTask-0.0.1-SNAPSHOT.jar"]