# Spring Boot Docker

## Dockerfile - 1 - Creating Docker Images

```shell
docker build -t chensoul/spring-boot-docker .

docker run -it chensoul/spring-boot-docker
```

## Dockerfile - 2 - Spring Boot Layered

```shell
docker build -f Dockerfile.layered -t chensoul/spring-boot-docker .
```

## Dockerfile - 3 - Maven build - Multi Stage

```shell
docker build -f Dockerfile.maven -t chensoul/spring-boot-docker .
```

## Dockerfile - 3 - Maven build - Multi Stage - Spring Boot Layered

```shell
docker build -f Dockerfile.maven.layered -t chensoul/spring-boot-docker .
```

## Maven plugin - spring-boot-maven-plugin

```shell
mvn spring-boot:build-image -DskipTests \
  -Ddocker.publishRegistry.username=user \
  -Ddocker.publishRegistry.password=secret \
  -Dspring-boot.build-image.publish=true
```

## Maven plugin - jib-maven-plugin

```shell
mvn jib:dockerBuild
docker login
docker push chensoul/spring-boot-docker
```
