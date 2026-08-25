FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml ./
RUN mvn --batch-mode dependency:go-offline
COPY src ./src
RUN mvn --batch-mode -DskipTests package

FROM eclipse-temurin:21-jre-jammy
RUN apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*
RUN useradd --system --create-home --uid 10001 faithos
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
COPY start.sh /app/start.sh
RUN chmod +x /app/start.sh \
    && mkdir -p /app/uploads && chown -R faithos:faithos /app
USER faithos
EXPOSE 8080
ENTRYPOINT ["/app/start.sh"]