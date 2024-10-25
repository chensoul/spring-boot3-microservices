![video_spider](https://socialify.git.ci/chensoul/spring-boot-microservices-ecommerce/image?forks=1&issues=1&language=1&name=1&owner=1&stargazers=1&theme=Light)

[中文文档](README_CN.md)

<p align="left">
	<a href="https://github.com/chensoul/spring-boot-microservices-ecommerce/stargazers"><img src="https://img.shields.io/github/stars/chensoul/spring-boot-microservices-ecommerce?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/chensoul/spring-boot-microservices-ecommerce/network/members"><img src="https://img.shields.io/github/forks/chensoul/spring-boot-microservices-ecommerce?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/chensoul/spring-boot-microservices-ecommerce/watchers"><img src="https://img.shields.io/github/watchers/chensoul/spring-boot-microservices-ecommerce?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/chensoul/spring-boot-microservices-ecommerce/issues"><img src="https://img.shields.io/github/issues/chensoul/spring-boot-microservices-ecommerce.svg?style=flat-square&logo=GitHub"></a>
	<a href="https://github.com/chensoul/spring-boot-microservices-ecommerce/blob/main/LICENSE"><img src="https://img.shields.io/github/license/chensoul/spring-boot-microservices-ecommerce.svg?style=flat-square"></a>
</p>

Build resilient and scalable microservices using Spring Cloud, Istio, and Kubernetes.

[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://codespaces.new/chensoul/spring-boot-microservices-ecommerce)

## Modules

- Chapter00：Docker
- Chapter01：Restful接口、持久化、SpringDoc OpenAPI
- Chapter02：异步通信
  - Chapter02-activemq
  - Chapter02-kafka
  - Chapter02-rabbitmq
  - Chapter02-spring-cloud-stream
- Chapter03：OpenFeign
- Chapter04：服务发现 Eureka
- Chapter05：服务网关 Spring Cloud Gateway
- Chapter06：配置服务 Spring Cloud Config
- chapter07: 链路追踪 Zipkin
- chapter08: 监控 Micrometer、Grafana
- chapter09: 监控 Micrometer、Grafana、Tempo
- chapter10: 监控 Micrometer、Grafana、Tempo、Loki
- chapter11：ELK
- chapter12 认证服务 Spring Security OAuth2
- Chapter13：监控服务 Spring Boot Admin
- Chapter14：Kubernetes
- Chapter15：Istio
- chapter16: Service Mesh
- chapter17: Native

## Tech Stack

* Building Spring Boot REST APIs
* Creating Aggregated Swagger Documentation at API Gateway
* Database Persistence using Spring Data JPA, MySQL, Mongodb, Flyway
* Distributed Tracing using Zipkin
* Event Driven Async Communication using Spring Kafka and avro
* Implementing API Gateway using Spring Cloud Gateway
* Implementing Resiliency using Resilience4j
* Using WebClient, Declarative HTTP Interfaces to invoke other APIs
* Local Development Setup using Docker, Docker Compose and Testcontainers
* Monitoring & Observability using Grafana, Prometheus, Loki, Tempo
* Testing using JUnit 5, RestAssured, Testcontainers, Awaitility, WireMock
* Deployment to Kubernetes using Kind

## Application Architecture

![microservices-architecture](./docs/microservices-architecture.jpg)

## Local Development Setup

- Install Java 21 and Maven 3. Recommend using [SDKMAN](https://sdkman.io/).
- Install [Docker](https://www.docker.com/). Recommend using [OrbStack](https://orbstack.dev/) for Macos.
- Install [IntelliJ](https://www.jetbrains.com/idea) IDEA or any of your favorite IDE
- Install [Postman](https://www.postman.com/) or any REST Client
