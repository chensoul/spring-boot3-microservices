# JUnit5

## JUnit 5 架构

从历史上看，JUnit 4 是单片的，并非设计用于与流行的构建工具（Maven 和 Gradle）和 IDE（Eclipse、NetBeans 和 IntelliJ）交互。这些工具与 JUnit 4 紧密耦合，并且经常依赖反射来获取必要的信息。这带来了一些挑战，例如，如果 JUnit 的设计者决定更改*私有*变量的名称，则此更改可能会影响以反射方式访问该变量的工具。

JUnit 5 将模块化方法引入框架，它能够允许 JUnit 与使用不同工具和 IDE 的不同编程客户端进行交互。它以 API 的形式引入了以下关注点的逻辑分离：

- 编写测试的API，主要供开发人员使用
- 发现和运行测试的机制
- 一个 API，允许轻松与 IDE 和工具交互并从中运行测试

因此，JUnit 5 由来自三个不同子项目的几个不同模块组成：

> JUnit 5 = JUnit 平台 + JUnit Jupiter + JUnit Vintage

- **JUnit 平台**：为了能够启动 junit 测试，IDE、构建工具或插件需要包含和扩展平台 API。它定义了`TestEngine`开发在平台上运行的新测试框架的 API。它还提供了一个控制台启动器，用于从命令行启动平台并为 Gradle 和 Maven 构建插件。
- **JUnit Jupiter**：它包括用于编写测试的新编程和扩展模型。它具有所有新的 junit 注释和`TestEngine`实现，可运行使用这些注释编写的测试。
- **JUnit Vintage**：其主要目的是支持在 JUnit 5 平台上运行 JUnit 3 和 JUnit 4 编写的测试。它具有向后兼容性。它要求类路径或模块路径中存在 JUnit 4.12 或更高版本。

![img](https://howtodoinjava.com/wp-content/uploads/2021/11/JUnit-5-Architecture.png)

## JUnit 5 Maven 依赖项

JUnit 5 版本是模块化的，包括以下这些：

- **junit-jupiter-api**：它是所有核心注释所在的主模块，例如*@Test*，生命周期方法注释和断言。
- **junit-jupiter-engine**：它具有运行时执行测试所需的测试引擎实现。
- **junit-jupiter-params**：提供对参数化测试的支持。
- **junit-platform-suite**：它提供 @Suite 支持，使得旧版 JUnit 4 的 JUnitPlatform 运行器过时。
- **junit-vintage-engine**：它包含执行用 JUnit 3 或 4 编写的测试的引擎实现。当然，为此目的，您还需要 JUnit 3 或 4 jar。

![img](https://howtodoinjava.com/wp-content/uploads/2021/11/JUnit-Modules.jpg)

为了能够**从命令提示符运行测试**，请确保您的*pom.xml*配置文件包含 Maven Surefire 插件的 JUnit 提供程序依赖项。

```xml
<build>
  <plugins>
    <plugin>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>2.22.2</version>
    </plugin>
  </plugins>
</build>
```

现在我们打开命令提示符进入项目文件夹（包含*pom.xml*文件的文件夹），然后运行以下命令：

```bash
mvn clean install
```

## JUnit5 注解

内置注解：

- @BeforeEach
- @AfterEach
- @BeforeAll
- @AfterAll
- @Test
- @DisplayName
- @Disable
- @Nested
- @Tag
- @TestFactory
- @ParameterizedTest
- @RepeatedTest
- @TestClassOrder
- @TestMethodOrder
- @Timeout
- @TempDir

测试套件注解：

- @Suite
- @SelectClasses
- @SelectPackages
- @IncludePackages
- @ExcludePackages
- @IncludeClassNamePatterns
- @ExcludeClassNamePatterns
- @IncludeTags
- @ExcludeTags

## 兼容 JUnit 4

由于 JUnit Jupiter 特有的所有类和注释都位于 `org.junit.jupiter` 基础包下，因此在类路径中同时包含 JUnit 4 和 JUnit Jupiter 不会导致任何冲突。因此，建议在 Junit 5 基础架构上编写新测试。

JUnit 4 已经问世很长时间了，而且有大量测试是用 junit 4 编写的。JUnit Jupiter 也需要支持这些测试。为此，开发了*JUnit Vintage*子项目。

JUnit Vintage 提供了一个*TestEngine*实现，用于在 JUnit 5 平台上运行基于 JUnit 3 和 JUnit 4 的测试。只要我们在类路径中有*junit-vintage-engine* 工件，JUnit 3 和 JUnit 4 测试就会被 JUnit Platform 启动器自动拾取。

```xml
<dependencies>
	<dependency>
		<groupId>junit</groupId>
		<artifactId>junit</artifactId>
		<version>4.12</version>
		<scope>test</scope>
	</dependency>

	<dependency>
		<groupId>org.junit.vintage</groupId>
		<artifactId>junit-vintage-engine</artifactId>
		<version>5.10.0</version>
		<scope>test</scope>
	</dependency>
</dependencies>
```

添加这些依赖项后，我们可以轻松地在 JUnit 5 环境中运行 Junit 4 测试。
