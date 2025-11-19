# Build with Gradle
FROM gradle:8.14.3-jdk21 AS builder
WORKDIR /app

COPY gradle gradle
COPY gradlew .
RUN chmod +x gradlew
COPY settings.gradle.kts .
COPY build.gradle.kts .
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v1.30.0/opentelemetry-javaagent.jar /otel-javaagent.jar

COPY --from=builder /app/build/libs/quote-api-0.0.1-SNAPSHOT.jar /app/quote-api.jar
EXPOSE 8080

# Run app with OpenTelemetry agent
ENTRYPOINT ["java", "-javaagent:/otel-javaagent.jar", "-Dotel.exporter.otlp.endpoint=http://otel-collector:4317", "-jar", "/app/quote-api.jar"]
