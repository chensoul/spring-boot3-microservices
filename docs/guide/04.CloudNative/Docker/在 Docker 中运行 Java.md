# 在 Docker 中运行 Java

在过去的几年里，有很多尝试让 Java 在 Docker 中良好地工作。最重要的是，在使用内存和 CPU 方面，Java 在历史上并不擅长尊重 Docker 容器设置的限制。

目前，Java的官方Docker镜像来自OpenJDK项目：https://hub.docker.com/_/openjdk/。我们将使用来自Eclipse Temurin项目的替代Docker镜像。它包含与OpenJDK项目相同的二进制文件，但提供了满足我们需求的Docker镜像的变体，比OpenJDK项目的Docker镜像更好。

正如前面提到的，早期版本的Java并不擅长遵守为使用Linux cgroups的Docker容器指定的配额；它们只是忽略了这些设置。

因此，Java没有根据容器中可用的内存在JVM中分配内存，而是分配了内存，就好像它可以访问Docker主机上的所有内存一样。当尝试分配比允许的更多的内存时，Java容器被主机以“内存不足”错误消息杀死。同样，Java分配了与CPU相关的资源，例如线程池，与Docker主机中可用的CPU核心总数有关，而不是为容器JVM运行提供的CPU核心数量。

**在 Java SE 9 中，提供了基于容器的 CPU 和内存限制的初始支持，在 Java SE 10 中得到了很大的改进。**

让我们看看Java SE 17如何响应我们在运行它的容器上设置的限制！

在接下来的测试中，我们将在MacBook Pro上的虚拟机中运行Docker引擎，作为Docker主机。Docker主机被配置为使用8个CPU核心和16 GB的内存。

### 限制可用CPU

让我们先找出 Java 在不施加任何限制的情况下看到的可用处理器（即 CPU 核心）数量。我们可以通过向 Java CLI 工具 jshell 发送 Java 语句 Runtime.getRuntime().availableprocessors() 来做到这一点。我们将使用包含完整 Java 17 JDK 的 Docker 映像在容器中运行 jshell。这个映像的Docker 标签是 eclipse-temurin: 17。命令如下：

```bash
echo 'Runtime.getRuntime().availableProcessors()' | docker run --rm -i eclipse-temurin:17jshell -q
```

由于 Docker 主机被配置为使用 8 个 CPU 核心，因此 8 个核心的响应符合预期。让我们继续使用 --cpus 3 Docker 选项将 Docker 容器限制为仅允许使用 3 个 CPU 核心，然后询问 JVM 看到了多少个可用处理器：

```bash
echo 'Runtime.getRuntime().availableProcessors()' | docker run --rm -i --cpus=3 eclipse-temurin:17 jshell -q
```

VM现在会响应Runtime.getRuntime().availableProcessors() $1 ==> 3；也就是说，Java SE 17会尊重容器中的设置，因此将能够正确配置与CPU相关的资源，例如线程池！

### 限制可用内存

在可用内存的数量方面，让我们向 JVM 询问它认为它可以分配给堆的最大大小。我们可以通过使用-XX: + PrintFlagsFinal Java 选项向 JVM 询问额外的运行时信息，然后使用 grep 命令过滤出MaxHeapSize 参数，如下所示：

```bash
docker run -it --rm eclipse-temurin:17 java -XX:+PrintFlagsFinal | grep "size_t MaxHeapSize"
```

由于没有 JVM 内存限制（即不使用 JVM 参数 -Xmx），Java 将为其堆分配 1/4 的可用内存。因此，我们预计它将为其堆分配多达 1 GB。从前面的屏幕截图中，我们可以看到响应大小为 1377828864 字节。这等于 1377828864/1024/1024=1314 MB，接近预期的 1 GB。

如果我们使用Docker选项-m=1024M将Docker容器限制为仅使用1 GB内存，则期望看到较低的最大内存分配。运行命令：

```bash
docker run -it --rm -m=1024M eclipse-temurin:17 java -XX:+PrintFlagsFinal | grep "size_t MaxHeapSize"
```

将导致响应268,435，456字节，这等于268，435，456/1024/1024=256 MB。256 MB是1 GB的四分之一，因此，这再次符合预期。

我们可以像往常一样，在JVM上设置最大堆大小。例如，如果我们想允许JVM使用我们总共1 GB的堆中的600 MB，我们可以使用JVM选项-Xmx600m来指定，如下所示：

```bash
docker run -it --rm -m=1024M eclipse-temurin:17 java -Xmx600m -XX:+PrintFlagsFinal -version | grep "size_t MaxHeapSize"
```

JVM将响应629,145，600字节=629，145，600/1024/1024=600 MB，再次符合预期。

让我们用一个“超出内存”的测试来确保它真的有效！

我们将使用jshell在JVM中分配一些内存，该JVM在容器中运行，该容器已分配1 GB内存；也就是说，它的最大堆大小为256 MB。

首先，尝试分配一个100 MB的字节数组：

```bash
echo 'new byte[100_000_000]' | docker run -i --rm -m=1024M eclipse-temurin:17 jshell -q
```

命令将响应$1 ==>，这意味着它工作正常！

通常，jshell将打印出命令的结果值，但是100 MB的字节都设置为零有点太多了，所以我们什么也看不到。

现在，让我们尝试分配一个比最大堆大小更大的字节数组，例如500 MB：

```bash
echo 'new byte[500_000_000]' | docker run -i --rm -m=1024M eclipse-temurin:17 jshell -q
```

JVM看到它无法执行操作，因为它尊重容器设置的最大内存，并立即响应异常java.lang.OutOfMemoryError：Java堆空间。太好了！

## 创建 dockerfile

在 maven 中创建 dockerfile

```dockerfile
FROM openjdk:17
EXPOSE 8080
ADD ./target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

- Docker镜像将基于OpenJDK的官方Docker图像，并使用版本17。
- 端口8080将暴露给其他Docker容器
- Fat JAR文件将从Maven构建库target添加到Docker映像中。
- 启动容器的命令是 java -jar / app.jar

上面有几个缺点：

- 我们正在使用完整的 Java SE 17 JDK，包括编译器和其他开发工具。这使得 Docker 映像变得过于庞大，从安全的角度来看，我们不想在映像中引入比必要更多的工具。因此，我们更愿意使用一个仅包含运行 Java 程序所需的程序和库的 Java SE 17 JRE 的基础映像。不幸的是，OpenJDK 项目没有为 Java SE 17 JRE 提供 Docker 映像。

- 当 Docker 容器启动时，臃肿的 JAR 文件需要很长时间才能解包。更好的方法是，在构建 Docker 映像时解包臃肿的 JAR。
- Fat JAR 文件非常大，正如我们下面将看到的，大约 20 MB。如果我们想在开发过程中对 Docker 映像中的应用程序代码进行可重复的更改，这将导致 Docker 构建命令的使用效果不佳。由于Docker 映像是分层构建的，我们将得到一个非常大的层，每次都需要替换，即使应用程序代码中只有一个 Java 类进行了更改。
-  一个更好的方法是将内容分为不同的层，其中不经常更改的文件添加到第一层，而更改最多的文件放在最后一层。这将导致对 Docker 的缓存机制进行良好的分层使用。对于某些应用程序代码更改时不会更改的第一个稳定层，Docker 将简单地使用缓存而不是重新构建它们。这将导致微服务 Docker 映像的更快构建。

关于 OpenJDK 项目中缺少 Java SE 17 JRE 的 Docker 映像，还有其他开源项目将 OpenJDK 二进制文件打包到 Docker 映像中。其中最广泛使用的项目之一是 Eclipse Temurin（https://adoptium.net / temurin /）。Temurin 项目为其 Docker 映像提供了完整的 JDK 版本和精简的 JRE 版本。

Spring Boot 在 v2.3.0 中能够将Fat JAR 文件的内容提取到多个文件夹中：

- dependencies
- spring-boot-loader
- snapshot-dependencies
- application

修改后：

```dockerfile
FROM eclipse-temurin:17-jre-alpine as builder
WORKDIR extracted
ADD ./target/*.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract && rm app.jar

FROM eclipse-temurin:17-jre-alpine
WORKDIR application
COPY --from=builder extracted/dependencies/ ./
COPY --from=builder extracted/spring-boot-loader/ ./
COPY --from=builder extracted/snapshot-dependencies/ ./
COPY --from=builder extracted/application/ ./

EXPOSE 8080

ENTRYPOINT ["java", "org.springframework.boot.loader.JarLauncher"]
```

## 构建镜像

```bash
cd spring-cloud/auth-server
build -t auth-server .
docker images | grep auth-server
```

运行：

```bash
docker run --rm -p8080:8080 auth-server
```

后台运行：

```bash
docker run -d -p8080:8080 --name my-prd-srv auth-server
```

查看镜像：

```bash
docker ps
```

查看日志：

```bash
docker logs my-prd-srv -f
```

停止容器：

```bash
docker rm -f my-prd-srv
```

