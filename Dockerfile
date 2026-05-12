FROM maven:3.8.5-openjdk-17 AS builder-bervan-utils

WORKDIR /app

COPY . .

# Single reactor build installs parent POM (com.bervan:utils) to ~/.m2. Per-module -f builds do not,
# so later modules fail resolving the parent when reading com.bervan:core from the local repo.
RUN mvn clean install -DskipTests

FROM maven:3.8.5-openjdk-17 AS bervan-utils-jars

COPY --from=builder-bervan-utils /app/core/target/core.jar /app/core.jar
COPY --from=builder-bervan-utils /app/history-tables-core/target/history-tables-core.jar /app/history-tables-core.jar
COPY --from=builder-bervan-utils /app/ie-entities/target/ie-entities.jar /app/ie-entities.jar