## 安装

安装

```bash
brew install gradle
```

升级包装器：

```bash
./gradlew wrapper --gradle-version=8.7
```

## 快速上手

### 初始化项目

不带参数初始化项目：

```bash
$ mkdir demo
cd demo

$ gradle init
Starting a Gradle Daemon (subsequent builds will be faster)

Select type of project to generate:
  1: basic
  2: application
  3: library
  4: Gradle plugin
Enter selection (default: basic) [1..4] 2

Select implementation language:
  1: C++
  2: Groovy
  3: Java
  4: Kotlin
  5: Scala
  6: Swift
Enter selection (default: Java) [1..6] 3

Generate multiple subprojects for application? (default: no) [yes, no]

Select build script DSL:
  1: Kotlin
  2: Groovy
Enter selection (default: Kotlin) [1..2] 2

Select test framework:
  1: JUnit 4
  2: TestNG
  3: Spock
  4: JUnit Jupiter
Enter selection (default: JUnit Jupiter) [1..4] 4

Project name (default: demo):

Enter target version of Java (min. 7) (default: 21):

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no]
```

带参数运行`gradle init`以生成 Java 应用程序：

```bash
$ gradle init --use-defaults --type java-application
```

调用包装器：

```bash
$ ./gradlew build

$ jar tf lib/build/libs/lib.jar
```

### 运行任务

```bash
$ ./gradlew tasks

$ ./gradlew jar

$ ./gradlew run

$ ./gradlew javadoc
```

### 项目依赖关系

```bash
$ ./gradlew :app:dependencies

$ ./gradlew build --scan
```

### 插件

https://docs.gradle.org/current/userguide/publishing_maven.html

添加一个 maven 发布插件

```groovy
plugins {
    id 'application'
    id 'maven-publish'
}
```

查看任务是否有增加：
```bash
$ ./gradlew :app:tasks
```

将发布信息添加到您的`build.gradle`文件中：

```groovy
publishing {
    publications {
        maven(MavenPublication) {
            groupId = "org.example"
            artifactId = "demo"
            version = "0.1.0"

            from components.java
        }
    }
}
```

再次查看任务：

```bash
$ ./gradlew :app:tasks
```

使用插件

```bash
$ ./gradlew :app:publishToMavenLocal
```

### 增量构建

在应用程序的顶级文件夹中，创建一个`gradle.properties`文件。

```
$ touch gradle.properties
```

添加`org.gradle.console=verbose`，使文件的内容如下所示：

```
org.gradle.console=verbose
```

运行构建任务：

```bash
$ ./gradlew :app:clean :app:build

$ ./gradlew :app:build
```

大多数任务都有`UP-TO-DATE`标签。这就是 Gradle 让我们知道输入和输出没有改变，因此一些任务没有重新运行的方式

### 启用构建缓存

添加`org.gradle.caching=true`到`gradle.properties`文件：

```
org.gradle.console=verbose
org.gradle.caching=true
```

运行构建任务：

```bash
$ ./gradlew :app:build

$ ./gradlew :app:clean :app:build
```

Gradle 让我们知道控制台输出中每个任务的结果：

- `FROM-CACHE`- 任务已从本地构建缓存中获取。
- `UP-TO-DATE`- 使用增量构建且未重新运行的任务。