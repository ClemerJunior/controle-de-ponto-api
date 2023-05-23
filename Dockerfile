FROM eclipse-temurin:20-jdk-alpine as build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN --mount=type=cache,target=/root/.m2 ./mvnw install -DskipTests

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} target/controle-ponto.jar
RUN java -Djarmode=layertools -jar target/controle-ponto.jar extract --destination target/extracted

FROM eclipse-temurin:20-jre-alpine
RUN addgroup -S padrao && adduser -S padrao -G padrao
VOLUME /tmp
USER padrao
ARG EXTRACTED=target/extracted
WORKDIR application
COPY --from=build ${EXTRACTED}/dependencies/ ./
COPY --from=build ${EXTRACTED}/spring-boot-loader/ ./
COPY --from=build ${EXTRACTED}/snapshot-dependencies/ ./
COPY --from=build ${EXTRACTED}/application/ ./
ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]