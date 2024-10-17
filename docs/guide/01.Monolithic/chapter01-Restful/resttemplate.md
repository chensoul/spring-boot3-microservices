# RestTemplate

`RestTemplate` 类是在 Spring Framework 3.0 开始引入的处理同步 HTTP 请求的类。

底层实现：

- JDK 原生的 `java.net.HttpURLConnection`。默认实现。
- Apache HttpComponents
- Netty
- Okhttp3

## 自定义 RestTemplateBuilder

### 1. 默认*RestTemplateBuilder*

要注入`RestTemplateBuilder`，请将其作为服务类中的构造函数参数传递。

```java
public class MyService {

	private final RestTemplate restTemplate;

	public MyService(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();
	}

	public Details someRestCall(String name) {
		return this.restTemplate.getForObject("/{name}/details", Details.class, name);
	}
}
```

### 2. 自定义*RestTemplateBuilder*

要创建自定义`RestTemplateBuilder`，请在 Spring 上下文中创建`@Bean`类型。

使用 RestTemplateCustomizer：

```java
public class RestTemplateConfig {

	@Bean
	@DependsOn(value = {"customRestTemplateBuilder"})
	public RestTemplateBuilder restTemplateBuilder(){
	    return new RestTemplateBuilder(customRestTemplateCustomizer());
	}

	@Bean
	public CustomRestTemplateCustomizer customRestTemplateCustomizer(){
	    return new CustomRestTemplateCustomizer();
	}
  
  public class CustomRestTemplateCustomizer implements RestTemplateCustomizer{
    @Override
    public void customize(RestTemplate restTemplate){
        restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
    }
	}
}
```

使用 RestTemplateBuilderConfigurer 配置 RestTemplateBuilder：

```java
@Bean
public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
}

@Bean
public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
    return configurer.configure(new RestTemplateBuilder())
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(2));
}
```

## 配置超时

### 1. 默认超时

默认情况下，*RestTemplate* 使用依赖于*HttpURLConnection*`SimpleClientHttpRequestFactory`的默认配置。查看类源代码，你会发现这一点。

```
private int connectTimeout = -1; 
private int readTimeout = -1;
```

**默认情况下，RestTemplate 使用机器上安装的 JDK 中的 timeout 属性。**要覆盖默认的 JVM 超时，我们可以在 JVM 启动期间传递这些属性。时间单位为
*毫秒*。

```properties
-Dsun.net.client.defaultConnectTimeout=5000
-Dsun.net.client.defaultReadTimeout=5000
```

### 2. 使用SimpleClientHttpRequestFactory

```java
@Bean
RestTemplate restTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
}

private ClientHttpRequestFactory getClientHttpRequestFactory() {
    SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
    factory.setConnectTimeout(5000);
    factory.setReadTimeout(2000);
    return factory;
}
```

也可以使用 RestTemplateBuilder 设置：

```java
@Bean
RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
    return restTemplateBuilder.build();
}

@Bean
public RestTemplateBuilder restTemplateBuilder(RestTemplateBuilderConfigurer configurer) {
    return configurer.configure(new RestTemplateBuilder())
        .setConnectTimeout(Duration.ofSeconds(5))
        .setReadTimeout(Duration.ofSeconds(2));
}
```

### 3. 使用 Apache HttpClient

有助于`SimpleClientHttpRequestFactory`设置超时，但功能非常有限，在实时应用中可能不够用。在生产代码中，我们可能希望使用功能丰富的库，例如
*Apache HttpClient*。

[HTTPClient](https://hc.apache.org/)库提供了其他有用的功能，例如连接池、空闲连接管理等。

如果使用 HttpComponentsClientHttpRequestFactory ，则需要添加依赖：

```xml
<dependency>
  <groupId>org.apache.httpcomponents</groupId>
  <artifactId>httpclient</artifactId>
</dependency>
```

接着，可以使用 org.apache.http.client.HttpClient 设置更多参数：

```java
private ClientHttpRequestFactory getClientHttpRequestFactory2() {
    int timeout = 5000;
    RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(timeout)
        .setConnectionRequestTimeout(timeout)
        .setSocketTimeout(timeout)
        .build();
    CloseableHttpClient client = HttpClientBuilder
        .create()
        .setDefaultRequestConfig(config)
        .build();
    return new HttpComponentsClientHttpRequestFactory(client);
}
```

一个更详细的例子可以参考：[Configuring HttpClient with Spring RestTemplate](https://howtodoinjava.com/spring-boot2/resttemplate/resttemplate-httpclient-java-config/)
，该例子：

- 支持 HTTP 和 HTTPS
- 使用连接池重新使用连接并节省创建连接的开销。
- 具有自定义连接保持策略（如果未指定则应用默认保持策略）
- 启动空闲连接监视器以持续清理陈旧的连接。

## 设置 Host

```java
@Value("${api.host.baseurl}")
private String apiHost;
 
@Bean
public RestTemplate restTemplate() {
	RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
	restTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(apiHost));
	return restTemplate;
}
```

## 异常处理

给RestTemplate添加异常处理，我们可以捕获HTTP请求过程中可能发生的异常，并进行优雅的处理。

RestTemplate 可能会根据错误的性质抛出各种异常，例如`HttpClientErrorException`4xx 客户端错误、`HttpServerErrorException`5xx
服务器错误等等。

```java
RestTemplate restTemplate = new RestTemplate();
String apiUrl = "https://api.example.com/resource";

try {
  // Send an HTTP GET request
  ResponseEntity<String> responseEntity = restTemplate.getForEntity(apiUrl, String.class);

  // Process the response
  if (responseEntity.getStatusCode().is2xxSuccessful()) {
      String responseBody = responseEntity.getBody();
      System.out.println("Response: " + responseBody);
  } else {
      System.err.println("Unexpected HTTP status: " + responseEntity.getStatusCode());
  }
} catch (HttpClientErrorException e) {
  // Handle HTTP client errors (4xx status codes)
  if (e.getStatusCode().is4xxClientError()) {
      System.err.println("Client error: " + e.getStatusCode() + " - " + e.getStatusText());
      System.err.println("Response Body: " + e.getResponseBodyAsString());
  } else {
      System.err.println("Unexpected HTTP status: " + e.getStatusCode());
  }
} catch (Exception e) {
  // Handle other exceptions
  System.err.println("An error occurred: " + e.getMessage());
}
```

## 启用身份验证

为了在 RestTemplate 中为传出的 rest 请求启用基本身份验证，我们应将**CredentialsProvider**配置到**HttpClient** API
中。RestTemplate 将使用此 HttpClient 向后端 rest api 发送 HTTP 请求。

```java
@Bean
public RestTemplate restTemplate() {
    return new RestTemplate(getClientHttpRequestFactory());
}

private HttpComponentsClientHttpRequestFactory getClientHttpRequestFactory() {
  HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
      = new HttpComponentsClientHttpRequestFactory();

  clientHttpRequestFactory.setHttpClient(httpClient());

  return clientHttpRequestFactory;
}

private HttpClient httpClient() {
  CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

  credentialsProvider.setCredentials(AuthScope.ANY,
      new UsernamePasswordCredentials("admin", "password"));

  HttpClient client = HttpClientBuilder
      .create()
      .setDefaultCredentialsProvider(credentialsProvider)
      .build();
  return client;
}
```

## 自定义拦截器

下面给出的`RequestResponseLoggingInterceptor`类实现`ClientHttpRequestInterceptor`接口。它实现`intercept()`
方法。在此方法中，我们记录从发送的请求和响应详细信息`RestTemplate`。

```java
@Slf4j
public class RequestResponseLoggingInterceptor implements ClientHttpRequestInterceptor {
  @Override
  public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
      logRequest(request, body);
      ClientHttpResponse response = execution.execute(request, body);
      logResponse(response);

      //Add optional additional headers
      response.getHeaders().add("headerName", "VALUE");

      return response;
  }

  private void logRequest(HttpRequest request, byte[] body) throws IOException {
      if (log.isDebugEnabled()) {
          LOG.info("===========================request begin================================================");
          LOG.info("URI         : {}", request.getURI());
          LOG.info("Method      : {}", request.getMethod());
          LOG.info("Headers     : {}", request.getHeaders());
          LOG.info("Request body: {}", new String(body, "UTF-8"));
          LOG.info("==========================request end================================================");
      }
  }

  private void logResponse(ClientHttpResponse response) throws IOException {
      if (log.isDebugEnabled()) {
          LOG.info("============================response begin==========================================");
          LOG.info("Status code  : {}", response.getStatusCode());
          LOG.info("Status text  : {}", response.getStatusText());
          LOG.info("Headers      : {}", response.getHeaders());
          LOG.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
          LOG.info("=======================response end=================================================");
      }
  }
}
```

现在用RestTemplate的方法添加上述拦截器`setInterceptors()`。

```java
@Bean 
public RestTemplate restTemplate()  {    
  RestTemplate restTemplate = new RestTemplate();     
  restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(clientHttpRequestFactory()));
  restTemplate.setInterceptors(Collections.singletonList(new RequestResponseLoggingInterceptor()));
  return restTemplate; 
}
```

需要注意的是，我们只能读取一次响应主体。下一次主体将为空。因此，如果我们想阻止这种行为，我们必须使用
BufferingClientHttpRequestFactory 来缓冲内存中所有传出和传入的流。

