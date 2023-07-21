FROM maven:3-openjdk-11-slim as builder

WORKDIR /build

# Copy the dependency specifications
COPY pom.xml pom.xml
COPY app/pom.xml app/pom.xml
COPY dto-core/pom.xml dto-core/pom.xml
COPY history-tables-core/pom.xml history-tables-core/pom.xml

# Resolve dependencies for `dto-core` module, e.g., shared libraries
# Also build all the required projects needed by the dto-core module.
# In this case, it will also resolve dependencies for the `root` module.
RUN mvn -q -ntp -B -pl dto-core -am dependency:go-offline
# Copy full sources for `dto-core` module
COPY dto-core dto-core
# Install the dto-core module in the local Maven repo (`.m2`)
# This will also install the `root` module.
# See: `la -lat ~/.m2/repository/org/example/*/*`
RUN mvn -q -B -pl dto-core install

# Resolve dependencies for `history-tables-core` module, e.g., shared libraries
# Also build all the required projects needed by the history-tables-core module.
# In this case, it will also resolve dependencies for the `root` module.
RUN mvn -q -ntp -B -pl history-tables-core -am dependency:go-offline
# Copy full sources for `history-tables-core` module
COPY history-tables-core history-tables-core
# Install the history-tables-core module in the local Maven repo (`.m2`)
# This will also install the `root` module.
# See: `la -lat ~/.m2/repository/org/example/*/*`
RUN mvn -q -B -pl history-tables-core install

# Resolve dependencies for the main application
RUN mvn -q -ntp -B -pl app -am dependency:go-offline
# Copy sources for main application
COPY app app
# Package the common and application modules together
RUN mvn -q -ntp -B -pl dto-core,history-tables-core,app package

RUN mkdir -p /jar-layers
WORKDIR /jar-layers
# Extract JAR layers
RUN java -Djarmode=layertools -jar /build/app/target/*.jar extract

FROM adoptopenjdk/openjdk11:centos-jre

RUN mkdir -p /run-app
WORKDIR /run-app

# Copy JAR layers, layers that change more often should go at the end
COPY --from=builder /jar-layers/dependencies/ ./
COPY --from=builder /jar-layers/spring-boot-loader/ ./
COPY --from=builder /jar-layers/snapshot-dependencies/ ./
COPY --from=builder /jar-layers/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]


#FROM maven:3.5-jdk-8 AS build_dto_core
#COPY dto-core/src /dto-core/
#COPY dto-core/pom.xml /dto-core/
#
#RUN mvn -f /dto-core/pom.xml -B dependency:go-offline
#
#RUN mvn -f /dto-core/pom.xml clean test install
#
#FROM maven:3.5-jdk-8 AS build_history_tables_core
#COPY history-tables-core/src /history-tables-core/
#COPY history-tables-core/pom.xml /history-tables-core/
#
#RUN mvn -f /history-tables-core/pom.xml -B dependency:go-offline
#
#RUN mvn -f /history-tables-core/pom.xml clean test install
#
#
#FROM maven:3.5-jdk-8 AS build_app
#COPY app/src /app/
#COPY app/pom.xml /app/
#COPY --from=build_dto_core /dto-core/target/dto-core-1.0.0.jar /app/lib/dto-core-1.0.0.jar
#COPY --from=build_history_tables_core /history-tables-core/target/history-tables-core-1.0.0.jar /app/lib/history-tables-core-1.0.0.jar
#
#RUN mvn -f /app/pom.xml -B dependency:go-offline
#
#RUN mvn -f /app/pom.xml clean test install

#FROM gcr.io/distroless/java
#COPY --from=build /usr/src/app/target/helloworld-1.0.0-SNAPSHOT.jar /usr/app/helloworld-1.0.0-SNAPSHOT.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","/usr/app/helloworld-1.0.0-SNAPSHOT.jar"]