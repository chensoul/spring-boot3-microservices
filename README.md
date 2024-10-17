# Spring-Boot-Microservices-Ecommerce

[![Build Status](https://github.com/chensoul/spring-boot-microservices-ecommerce/actions/workflows/maven-build.yml/badge.svg)](https://github.com/chensoul/spring-boot-microservices-ecommerce/actions/workflows/maven-build.yml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

## 技术选型

| 模块名称 | 技术选型              | 使用版本   | 最新版本                                                                                                                                                                                                                              | 备注 |
|------|-------------------|--------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----|
| 语言   | Java              | 21     |                                                                                                                                                                                                                                   |    |
| 构建工具 | Maven             | 3.9.9  |                                                                                                                                                                                                                                   |    |
| 容器编排 | Docker            | 25.0.5 |                                                                                                                                                                                                                                   |    |
| 数据库  | MySQL             | 8      |                                                                                                                                                                                                                                   |    |
| 缓存   | Redis             | 7      |                                                                                                                                                                                                                                   |    |
| 消息队列 | Rabbitmq          | 4      |                                                                                                                                                                                                                                   |    |
| 消息队列 | Kafka             | 3.8    |                                                                                                                                                                                                                                   |    |
| 开发框架 | Spring Boot       | 3.3.4  | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&versionPrefix=3&metadataUrl=https://s01.oss.sonatype.org/content/repositories/releases/org/springframework/boot/spring-boot-dependencies/maven-metadata.xml"> |    |
| 在线文档 | SpringDoc OpenApi | 2.6.0  | <img src="https://img.shields.io/maven-metadata/v?label=&color=blue&metadataUrl=https://oss.sonatype.org/content/repositories/releases/org/springdoc/springdoc-openapi/maven-metadata.xml">                                       |    |

## 开发环境准备

- [Git](https://git-scm.com/downloads)
- [Docker](https://docs.docker.com/get-docker/)
- [Java](https://www.azul.com/downloads/#zulu)
- [Curl](https://curl.haxx.se/download.html)
- [Jq](https://stedolan.github.io/jq/download/)
- [Spring Boot CLI](https://docs.spring.io/spring-boot/docs/3.0.4/reference/html/getting-started.html#getting-started.installing.cli)
- [Siege](https://github.com/JoeDog/siege#where-is-it)
- [Helm](https://helm.sh/docs/intro/install/)
- [kubectl](https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/)
- [Istioctl](https://istio.io/latest/docs/setup/getting-started/#download)

安装软件：

```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

brew install orbstack
brew install spring-io/tap/spring-boot 
brew install openjdk@21 
brew install maven
brew install jq 
brew install siege 
brew install helm
brew install minikub 
brew install kubectl 
brew install istioctl

echo 'export JAVA_HOME=$(/usr/libexec/java_home -v8)' >> ~/.bash_profile
source ~/.bash_profile
```

验证版本：

```bash
git version && \
docker version -f json | jq -r .Client.Version && \
java -version 2>&1 | grep "openjdk version" && \
mvn -v | grep "Maven" && \
curl --version | grep "curl" | sed 's/(.*//' && \
jq --version && \
spring --version && \
siege --version 2>&1 | grep SIEGE && \
helm version --short && \
kubectl version --client -o json | jq -r .clientVersion.gitVersion && \
minikube version | grep "minikube" && \
istioctl version --remote=false
```

## 快速运行：

### 本地运行服务

1、启动 postgres 和 redis

```bash
cd Chapter04
docker-compose up postgres redis -d
```

2、启动应用

```bash
mvn clean -DskipTests spring-boot:run
```

### 使用 Docker 运行服务

1. 使用 spring-boot 构建镜像

```shell
cd Chapter04 
mvn spring-boot:build-image -DskipTests
```

如果想将镜像推送到 docker hub，运行下面命令：

```shell
mvn spring-boot:build-image -DskipTests \
  -Ddocker.publishRegistry.username=user \
  -Ddocker.publishRegistry.password=secret \
  -Dspring-boot.build-image.publish=true
```

运行服务

```shell
docker-compose up -d
```

### 使用 K8s 运行服务

## 参考资料

- https://www.youtube.com/playlist?list=PLeLcvrwLe185prGhjUrFGQsOh_0MArR1P
- https://github.com/chensoul/spring-petclinic-microservices
- https://github.com/ali-bouali/microservices-full-code
- https://github.com/SaiUpadhyayula/spring-boot-microservices
- https://github.com/SaiUpadhyayula/spring-boot-3-microservices-course
- https://github.com/in28minutes/spring-microservices-v3
- [A Comprehensive guide to Spring Boot 3.2 with Java 21, Virtual Threads, Spring Security, PostgreSQL, Flyway, Caching, Micrometer, Opentelemetry, JUnit 5, RabbitMQ, Keycloak Integration, and More! (10/17)](https://medium.com/@jojoooo/exploring-a-base-spring-boot-application-with-java-21-virtual-thread-spring-security-flyway-c0fde13c1eca)
