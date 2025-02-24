FROM maven:3.8.5-openjdk-17 AS builder-bervan-utils

WORKDIR /app

COPY . .

RUN mvn -f='./core' install -DskipTests
RUN mvn -f='./history-tables-core' install -DskipTests
RUN mvn -f='./ie-entities' install -DskipTests

FROM maven:3.8.5-openjdk-17 AS bervan-utils-jars

COPY --from=builder-bervan-utils /app/core/target/core.jar /app/core.jar
COPY --from=builder-bervan-utils /app/history-tables-core/target/history-tables-core.jar /app/history-tables-core.jar
COPY --from=builder-bervan-utils /app/ie-entities/target/ie-entities.jar /app/ie-entities.jar