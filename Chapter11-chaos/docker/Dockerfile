FROM eclipse-temurin:21-jre-jammy AS builder
WORKDIR /extracted

ARG ARTIFACT_NAME
COPY ${ARTIFACT_NAME}.jar app.jar

RUN java -Djarmode=layertools -jar app.jar extract

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=builder /extracted/dependencies/ ./
COPY --from=builder /extracted/spring-boot-loader/ ./
COPY --from=builder /extracted/snapshot-dependencies/ ./
COPY --from=builder /extracted/application/ ./

ARG EXPOSED_PORT
EXPOSE ${EXPOSED_PORT}

ENTRYPOINT [ "java",  "org.springframework.boot.loader.launch.JarLauncher" ]
