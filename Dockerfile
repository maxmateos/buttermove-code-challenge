FROM amazoncorretto:11

RUN mkdir /app

WORKDIR /app

COPY ./build/libs/demo-0.0.1-SNAPSHOT.jar .

ENTRYPOINT ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar", "application.yaml"]
