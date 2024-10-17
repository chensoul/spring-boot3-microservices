# 介绍



# 源码分析

## Spring Cloud  源码分析

项目中引入 spring-cloud-common 包时，会自动加载以下配置：

```
org.springframework.cloud.client.CommonsClientAutoConfiguration
org.springframework.cloud.client.ReactiveCommonsClientAutoConfiguration
org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration
org.springframework.cloud.client.discovery.composite.reactive.ReactiveCompositeDiscoveryClientAutoConfiguration
org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration
org.springframework.cloud.client.discovery.simple.reactive.SimpleReactiveDiscoveryClientAutoConfiguration
org.springframework.cloud.client.hypermedia.CloudHypermediaAutoConfiguration
org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration
org.springframework.cloud.client.loadbalancer.LoadBalancerDefaultMappingsProviderAutoConfiguration
org.springframework.cloud.client.loadbalancer.reactive.LoadBalancerBeanPostProcessorAutoConfiguration
org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerClientAutoConfiguration
org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration
org.springframework.cloud.commons.util.UtilAutoConfiguration
org.springframework.cloud.configuration.CompatibilityVerifierAutoConfiguration
org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration
org.springframework.cloud.commons.security.ResourceServerTokenRelayAutoConfiguration
org.springframework.cloud.commons.config.CommonsConfigAutoConfiguration
```

常用的配置类：

- UtilAutoConfiguration：自动装配 InetUtils 和 InetUtilsProperties，用于获取主机名称和 IP

  ```properties
  spring.cloud.util.enabled
  spring.cloud.inetutils.defaultHostname
  spring.cloud.inetutils.defaultIpAddress
  spring.cloud.inetutils.timeoutSeconds
  spring.cloud.inetutils.ignoredInterfaces
  spring.cloud.inetutils.useOnlySiteLocalInterfaces
  spring.cloud.inetutils.preferredNetworks
  ```

​	HostInfoEnvironmentPostProcessor 用于设置：`spring.cloud.client.hostname` 和 `spring.cloud.client.ip-address`

- CommonsClientAutoConfiguration：配置健康检查
- CompositeDiscoveryClientAutoConfiguration
- SimpleDiscoveryClientAutoConfiguration
- LoadBalancerAutoConfiguration
- LoadBalancerDefaultMappingsProviderAutoConfiguration
- ServiceRegistryAutoConfiguration
- AutoServiceRegistrationAutoConfiguration

### 服务注册

注册接口：org.springframework.cloud.client.serviceregistry.ServiceRegistry

注册对象：org.springframework.cloud.client.serviceregistry.Registration

注册生命周期接口：org.springframework.cloud.client.serviceregistry.RegistrationLifecycle 

- 注册管理生命周期接口：org.springframework.cloud.client.serviceregistry.RegistrationManagementLifecycle

注册端点：org.springframework.cloud.client.serviceregistry.endpoint.ServiceRegistryEndpoint

自动注册接口（标记接口）：org.springframework.cloud.client.serviceregistry.AutoServiceRegistration

- org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration

自动注册配置：org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationProperties

自动装配类：

- org.springframework.cloud.client.serviceregistry.AutoServiceRegistrationAutoConfiguration
- org.springframework.cloud.client.serviceregistry.ServiceRegistryAutoConfiguration



### 服务发现

接口：

- org.springframework.cloud.client.discovery.DiscoveryClient
- org.springframework.cloud.client.discovery.ReactiveDiscoveryClient

注解：

- org.springframework.cloud.client.discovery.EnableDiscoveryClient

一个简单实现的 DiscoveryClient：

- org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClient
- org.springframework.cloud.client.discovery.simple.SimpleDiscoveryClientAutoConfiguration
- org.springframework.cloud.client.discovery.simple.SimpleDiscoveryProperties

```properties
spring.cloud.discovery.client.simple.instances.service1[0].uri=http://s11:8080
```

组合 DiscoveryClient：

- org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClient
- org.springframework.cloud.client.discovery.composite.CompositeDiscoveryClientAutoConfiguration

事件：

- org.springframework.cloud.client.discovery.event.HeartbeatEvent
- org.springframework.cloud.client.discovery.event.InstancePreRegisteredEvent
- org.springframework.cloud.client.discovery.event.InstanceRegisteredEvent
- org.springframework.cloud.client.discovery.event.ParentHeartbeatEvent

健康检查：

- org.springframework.cloud.client.discovery.health.DiscoveryClientHealthIndicator
- org.springframework.cloud.client.discovery.health.DiscoveryClientHealthIndicatorProperties
- org.springframework.cloud.client.discovery.health.DiscoveryCompositeHealthContributor
- org.springframework.cloud.client.discovery.health.DiscoveryHealthIndicator

### 负载均衡

注解：

- org.springframework.cloud.client.loadbalancer.LoadBalanced
  - 由以下几个后置处理类来处理：
    - LoadBalancerRestClientBuilderBeanPostProcessor：使用 RestClient
    - LoadBalancerWebClientBuilderBeanPostProcessor：使用 WebClient
    - LoadBalancerAutoConfiguration#loadBalancedRestTemplateInitializerDeprecated：使用 RestTemplate

接口：

- org.springframework.cloud.client.loadbalancer.ServiceInstanceChooser
  - org.springframework.cloud.client.loadbalancer.LoadBalancerClient
- org.springframework.cloud.client.loadbalancer.LoadBalancerRequest
  - org.springframework.cloud.client.loadbalancer.HttpRequestLoadBalancerRequest

- org.springframework.cloud.client.loadbalancer.LoadBalancerLifecycle
  - org.springframework.cloud.loadbalancer.stats.MicrometerStatsLoadBalancerLifecycle

- org.springframework.cloud.client.loadbalancer.Request
  - org.springframework.cloud.client.loadbalancer.DefaultRequest
    - org.springframework.cloud.client.loadbalancer.LoadBalancerRequestAdapter
- org.springframework.cloud.client.loadbalancer.LoadBalancerRequestTransformer
  - org.springframework.cloud.loadbalancer.core.LoadBalancerServiceInstanceCookieTransformer
  - org.springframework.cloud.loadbalancer.blocking.XForwardedHeadersTransformer
- org.springframework.cloud.client.loadbalancer.Response
  - org.springframework.cloud.client.loadbalancer.DefaultResponse
- org.springframework.cloud.client.loadbalancer.RestTemplateCustomizer
- org.springframework.cloud.client.loadbalancer.TimedRequestContext
  - org.springframework.cloud.client.loadbalancer.HintRequestContext
    - org.springframework.cloud.client.loadbalancer.DefaultRequestContext
      - org.springframework.cloud.client.loadbalancer.RequestDataContext
        - org.springframework.cloud.client.loadbalancer.RetryableRequestContext

重试相关类：

- org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory

- org.springframework.cloud.client.loadbalancer.LoadBalancedRetryPolicy
- org.springframework.cloud.client.loadbalancer.LoadBalancedRetryContext
- org.springframework.cloud.client.loadbalancer.LoadBalancedRecoveryCallback
- org.springframework.cloud.client.loadbalancer.InterceptorRetryPolicy



### 熔断

- org.springframework.cloud.client.circuitbreaker.ConfigBuilder
  - org.springframework.cloud.client.circuitbreaker.AbstractCircuitBreakerFactory
    - org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
- org.springframework.cloud.client.circuitbreaker.CircuitBreaker
- org.springframework.cloud.client.circuitbreaker.Customizer

## Eureka 源码分析

eureka 版本：2.0.1

### Eureka Server 启动流程

下载 eureka 源代码，运行 eureka-server 中的 EurekaClientServerRestIntegrationTest 类，调试代码：

1. eureka-server 以 war 包的形式部署到 tomcat 中，由 jersey 框架对外提供服务
   1. ApplicationsResource
   2. ApplicationResource
2. com.netflix.eureka.EurekaBootStrap 实现了 ServletContextListener 接口，在 contextInitialized 方法中初始化 eureka 环境和上下文
   1. initEurekaEnvironment
      1. eureka.datacenter
      2. eureka.environment
   2. initEurekaServerContext
      1. 创建 DefaultEurekaServerConfig
      2. 设置 JsonXStream 和 XmlXStream 转化器
      3. 创建 DefaultServerCodecs
      4. 判断是否是 cloud 环境，如果是则创建 CloudInstanceConfig；否则创建 MyDataCenterInstanceConfig
      5. 创建 EurekaConfigBasedInstanceInfoProvider 并 创建 ApplicationInfoManager
         1. EurekaConfigBasedInstanceInfoProvider 
            1. 创建组约信息（续约时间间隔renewalIntervalInSecs=30s，组约有效时长 durationInSecs=90s）
            2. 创建 vipAddressResolver
            3. 创建 instanceInfo
      6. 创建 DefaultEurekaClientConfig
      7. 创建 DiscoveryClient
         1. 初始化 healthCheckHandlerProvider、healthCheckCallbackProvider、preRegistrationHandler、eventListeners
         2. 保存变量
         3. 得到 appPathIdentifier：EUREKA/chensoulMacBook.local
         4. 初始化 backupRegistryProvider
         5. 初始化 endpointRandomizer
         6. 初始化 urlRandomizer
         7. 初始化 `AtomicReference<Applications> localRegionApps`
         8. 初始化 fetchRegistryGeneration
         9. 初始化 remoteRegionsToFetch
         10. 初始化 remoteRegionsRef
         11. 判断 shouldFetchRegistry，初始化 registryStalenessMonitor
         12. 判断 shouldRegisterWithEureka，初始化 heartbeatStalenessMonitor
         13. 创建 scheduler 和 heartbeatExecutor 、cacheRefreshExecutor
         14. 创建 EurekaTransport
         15. 调用 scheduleServerEndpointTask 方法
             1. 初始化 providedJerseyClient、TransportClientFactories
             2. 创建 applicationsSource
             3. 判断 shouldRegisterWithEureka，如果为 true，则创建 newRegistrationClient
             4. 判断 shouldFetchRegistry，如果为 true，则创建 newQueryClient
         16. 创建 azToRegionMapper
         17. 创建 instanceRegionChecker
         18. 判断 shouldFetchRegistry ，如果为 true，则获取注册中心
         19. this.preRegistrationHandler.beforeRegistration();
         20. 如果 shouldRegisterWithEureka 且 shouldEnforceRegistrationAtInit，则调用注册方法
         21. 调用 initScheduledTasks 方法
             1. 如果 shouldFetchRegistry，则定期执行 cacheRefreshTask
             2. 如果 shouldRegisterWithEureka，则定期执行 heartbeatTask，启动 instanceInfoReplicator 进行复制
                1. 刷新 discoveryClient.refreshInstanceInfo(); 刷新数据中心信息、组约信息、刷新状态
                2. 如果 dirtyTimestamp 不为空，则注册
             3. 
      8. 创建 PeerAwareInstanceRegistry，如果是亚马逊数据中心，则创建 AwsInstanceRegistry；否则创建 PeerAwareInstanceRegistryImpl
      9. 创建 PeerEurekaNodes
      10. 创建 DefaultEurekaServerContext ，并将其保存到 EurekaServerContextHolder，调用 serverContext.initialize() 方法
          1. 调用 peerEurekaNodes start 方法
             1. 创建一个名称为 Eureka-PeerNodesUpdater 的线程
             2. 获取 peer URLs，然后调用 updatePeerEurekaNodes 方法，用于初始化 Eureka 服务端节点列表 
             3. 启动线程定期更新Eureka 服务端节点列表
          2. PeerAwareInstanceRegistry 初始化 peerEurekaNodes
             1. 启动 MeasuredRate 线程
             2. 初始化 ResponseCacheImpl，ResponseCacheImpl 用于在内存中缓存服务端节点信息
             3. 调用 scheduleRenewalThresholdUpdateTask 方法启动续约线程
                1. 找出可注册的应用判断是否更新续约阈值
             4. 初始化 RemoteRegionRegistry
             5. Monitors.registerObject(this);
      11. 从其他 registry 同步 eureka 节点
          1. 先等 30s（ eureka.registrySyncRetryWaitMs）
          2. 获取所有应用，判断是否可以注册，如果可以，则**注册**
      12. openForTraffic
      13. EurekaMonitors.registerAllStats();
   3. 将 EurekaServerContext 保存到 ServletContext 属性中

### Eureka 核心类

Eureka 提供了几个实体类：

- InstanceInfo：代表注册的服务实例

- LeaseInfo：标识应用实例的租约信息

- InstanceStatus：用于标识服务实例的状态，它是一个枚举

对于服务发现来说，围绕服务实例主要有如下几个重要的操作：

- 服务注册（register）
- 服务下线（cancel）
- 服务租约（renew）
- 服务剔除（evict）

围绕这几个功能，Eureka设计了几个核心操作类：

- com.netflix.eureka.lease.LeaseManager：接口定义了应用服务实例在服务中心的几个操作方法：register、cancel、renew、evict

  ```java
  public interface LeaseManager<T> {
      void register(T r, int leaseDuration, boolean isReplication);
  
      boolean cancel(String appName, String id, boolean isReplication);
  
      boolean renew(String appName, String id, boolean isReplication);
  
      void evict();
  }
  ```

- com.netflix.discovery.shared.LookupService：接口定义了Eureka Client从服务中心获取服务实例的查询方法

  ```java
  public interface LookupService<T> {
      Application getApplication(String appName);
  
      Applications getApplications();
  
      List<InstanceInfo> getInstancesById(String id);
  
      InstanceInfo getNextServerFromEureka(String virtualHostname, boolean secure);
  }
  ```

- com.netflix.eureka.registry.InstanceRegistry：实例注册接口，继承了com.netflix.eureka.lease.LeaseManager 和 com.netflix.discovery.shared.LookupService 接口。

- AbstractInstanceRegistry：抽象实例注册类

- PeerAwareInstanceRegistryImpl：点对点实例注册类

## Spring Cloud Eureka 源码分析

添加依赖：

```
org.springframework.cloud:spring-cloud-starter-netflix-eureka-server
```

spring-cloud-starter-netflix-eureka-server 依赖了 spring-cloud-starter-netflix-eureka-client



spring-cloud-starter-netflix-eureka-client 自动装配：

```properties
org.springframework.cloud.netflix.eureka.config.EurekaClientConfigServerAutoConfiguration
org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration
org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration
org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration
org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration
```

- EurekaClientConfigServerAutoConfiguration：用于设置 eureka 实例的元数据 configPath

  ```java
  String prefix = this.env.getProperty("spring.cloud.config.server.prefix");
  if (StringUtils.hasText(prefix) && !StringUtils.hasText(this.instance.getMetadataMap().get("configPath"))) {
    this.instance.getMetadataMap().put("configPath", prefix);
  }
  ```

- DiscoveryClientOptionalArgsConfiguration：用于设置 http 请求客户端，支持三种客户端：RestTemplate、JerseyClient、WebClient

- EurekaClientAutoConfiguration：

  - 配置在 CommonsClientAutoConfiguration、ServiceRegistryAutoConfiguration 之前，在 DiscoveryClientOptionalArgsConfiguration、RefreshAutoConfiguration、EurekaDiscoveryClientConfiguration、AutoServiceRegistrationAutoConfiguration 之后
  - 用于装配 Eureka 需要的 Bean：
    - EurekaClientConfigBean
    - EurekaInstanceConfigBean
    - CloudEurekaClient
    - ApplicationInfoManager

  - 装配 SpringCloud ServiceRegistry 需要的 Bean：

    - EurekaServiceRegistry

    - EurekaRegistration
    - EurekaAutoServiceRegistration

  - eureka.client.refresh.enable 配置是否可以刷新



spring-cloud-starter-netflix-eureka-server 的 spring.factories 配置自动装配类：

```properties
org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration
```

EurekaServerAutoConfiguration

```java
@Configuration(proxyBeanMethods = false)
@Import(EurekaServerInitializerConfiguration.class)
@ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class)
@EnableConfigurationProperties({ EurekaDashboardProperties.class, InstanceRegistryProperties.class,
   EurekaProperties.class })
@PropertySource("classpath:/eureka/server.properties")
public class EurekaServerAutoConfiguration implements WebMvcConfigurer {

}
```

EurekaServerAutoConfiguration 

- 类实现了 WebMvcConfigurer 接口
- 导入了 EurekaServerInitializerConfiguration 配置类
  - 实现了 ServletContextAware 、SmartLifecycle接口，在 start 方法中调用 EurekaServerContext 的初始化方法，启动 eureka-server
- 激活了三个配置文件：
  - EurekaDashboardProperties
    - 属性前缀：eureka.dashboard
    - 对应 EurekaController
  - InstanceRegistryProperties
    - 属性前缀：eureka.instance.registry
  - EurekaProperties
    - 属性前缀：eureka
      - environment=test
      - datacenter=default
- 导入 classpath:/eureka/server.properties
  - server.servlet.encoding.force=false
- 装配了一些 Bean，下面只列出重要的几个：
  - EurekaController
    - 可以用 `eureka.dashboard.path` 配置 eureka dashboard 的地址，特别是当 eureka 注册到网关的时候，可以配置eureka server 在网关上的地址
  - InstanceRegistry
    - 继承 PeerAwareInstanceRegistryImpl，中注册、续约、取消等方法调用之前发布相应的事件
      - EurekaServerStartedEvent
      - EurekaRegistryAvailableEvent
      - EurekaInstanceRenewedEvent
      - EurekaInstanceRegisteredEvent
      - EurekaInstanceCanceledEvent
  - RefreshablePeerEurekaNodes
    - 继承 PeerEurekaNodes，支持在以下配置变更时刷新 PeerEurekaNode
      - `eureka. client. availability-zones`
      - `eureka. client. region`
      - `eureka. client. service-url.<zone>`
  - EurekaServerContext
  - EurekaServerBootstrap
    - 重写 Eureka 的 EurekaBootstrap 类
  - EurekaServerConfig

服务端代码：

```java
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
	public static void main(final String[] args) {
		SpringApplication.run(EurekaServerApplication.class, args);
	}
}
```

EnableEurekaServer：

```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EurekaServerMarkerConfiguration.class)
public @interface EnableEurekaServer {
}
```

EnableEurekaServer 类的作用是注入 EurekaServerMarkerConfiguration.Marker.class 这个 Bean ，以便激活 EurekaServerAutoConfiguration 这个类。



> spring-cloud-starter-netflix-eureka-server 的 spring.factories 自动装配了 EurekaServerAutoConfiguration 类
>
> ```properties
> org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration
> ```
>
> 但是，EurekaServerAutoConfiguration 必须在 EurekaServerMarkerConfiguration.Marker.class 这个 Bean 存在时才生效。
>
> ```java
> @Configuration(proxyBeanMethods = false)
> @Import(EurekaServerInitializerConfiguration.class)
> @ConditionalOnBean(EurekaServerMarkerConfiguration.Marker.class) //需要激活
> @EnableConfigurationProperties({ EurekaDashboardProperties.class, InstanceRegistryProperties.class,
> 		EurekaProperties.class })
> @PropertySource("classpath:/eureka/server.properties")
> public class EurekaServerAutoConfiguration implements WebMvcConfigurer
> ```



EurekaServerAutoConfiguration 的作用是自动装配 Bean，EurekaServerInitializerConfiguration 的作用是初始化 EurekaServer：

```java
@Override
public void start() {
  new Thread(() -> {
    try {
      // TODO: is this class even needed now?
      eurekaServerBootstrap.contextInitialized(EurekaServerInitializerConfiguration.this.servletContext);
      log.info("Started Eureka Server");

      publish(new EurekaRegistryAvailableEvent(getEurekaServerConfig()));
      EurekaServerInitializerConfiguration.this.running = true;
      publish(new EurekaServerStartedEvent(getEurekaServerConfig()));
    }
    catch (Exception ex) {
      // Help!
      log.error("Could not initialize Eureka servlet context", ex);
    }
  }).start();
}
```



> EurekaServerBootstrap 和 EurekaBootstrap 区别：
>
> - EurekaServerBootstrap 将 EurekaBootstrap 中的初始化工作交给 spring 容器进行注入 Bean
> - EurekaBootstrap 实现 ServletContextListener 接口，则 servlet 的 context 初始化的时候进行初始化；EurekaServerBootstrap 由 EurekaServerInitializerConfiguration 在 start 方法中调用



因为spring-cloud-starter-eureka-server 依赖了 spring-cloud-starter-eureka-client ，所以在启动过程中也会初始化 eureka-client。主要是 EurekaClientAutoConfiguration 和 EurekaDiscoveryClientConfiguration。

EurekaClientAutoConfiguration 自动装配以下 bean：

- EurekaClientConfigBean
- ManagementMetadataProvider
- EurekaInstanceConfigBean
- CloudEurekaClient
- ApplicationInfoManager
- EurekaServiceRegistry
- EurekaAutoServiceRegistration
- EurekaRegistration

上面后面三个 bean 是 Spring Cloud ServiceRegistry对 eureka 的封装。EurekaAutoServiceRegistration 实现了 SmartLifecycle接口：

```java
public class EurekaAutoServiceRegistration
		implements AutoServiceRegistration, SmartLifecycle, Ordered, SmartApplicationListener {
```

在 start 方法中完成 EurekaRegistration 的注册：

```java
@Override
public void start() {
  // only set the port if the nonSecurePort or securePort is 0 and this.port != 0
  if (this.port.get() != 0) {
    if (this.registration.getNonSecurePort() == 0) {
      this.registration.setNonSecurePort(this.port.get());
    }

    if (this.registration.getSecurePort() == 0 && this.registration.isSecure()) {
      this.registration.setSecurePort(this.port.get());
    }
  }

  // only initialize if nonSecurePort is greater than 0 and it isn't already running
  // because of containerPortInitializer below
  if (!this.running.get() && this.registration.getNonSecurePort() > 0) {

    this.serviceRegistry.register(this.registration);

    this.context.publishEvent(new InstanceRegisteredEvent<>(this, this.registration.getInstanceConfig()));
    this.running.set(true);
  }
}
```

- port = 0
- this.running.get() =false 且 this.registration.getNonSecurePort() > 0 时，调用注册方法 this.serviceRegistry.register(this.registration);
- 注册成功之后，发布一个 InstanceRegisteredEvent 事件。

EurekaAutoServiceRegistration 实现了 AutoServiceRegistration 接口，同样 AbstractAutoServiceRegistration 也实现了 AutoServiceRegistration 接口，并且还实现了 `ApplicationListener<WebServerInitializedEvent>` 接口，监听了 WebServerInitializedEvent 事件。

```java
public void onApplicationEvent(WebServerInitializedEvent event) {
  ApplicationContext context = event.getApplicationContext();
  if (context instanceof ConfigurableWebServerApplicationContext) {
    if ("management".equals(((ConfigurableWebServerApplicationContext) context).getServerNamespace())) {
      return;
    }
  }
  this.port.compareAndSet(0, event.getWebServer().getPort());
  this.start();
}
```

start 方法：

```java
public void start() {
  if (!isEnabled()) {
    if (logger.isDebugEnabled()) {
      logger.debug("Discovery Lifecycle disabled. Not starting");
    }
    return;
  }

  // only initialize if nonSecurePort is greater than 0 and it isn't already running
  // because of containerPortInitializer below
  if (!this.running.get()) {
    this.context.publishEvent(new InstancePreRegisteredEvent(this, getRegistration()));
    registrationLifecycles.forEach(
        registrationLifecycle -> registrationLifecycle.postProcessBeforeStartRegister(getRegistration()));
    register();
    this.registrationLifecycles.forEach(
        registrationLifecycle -> registrationLifecycle.postProcessAfterStartRegister(getRegistration()));
    if (shouldRegisterManagement()) {
      this.registrationManagementLifecycles
          .forEach(registrationManagementLifecycle -> registrationManagementLifecycle
              .postProcessBeforeStartRegisterManagement(getManagementRegistration()));
      this.registerManagement();
      registrationManagementLifecycles
          .forEach(registrationManagementLifecycle -> registrationManagementLifecycle
              .postProcessAfterStartRegisterManagement(getManagementRegistration()));

    }
    this.context.publishEvent(new InstanceRegisteredEvent<>(this, getConfiguration()));
    this.running.compareAndSet(false, true);
  }

}
```

该方法：

- 发布一个 InstancePreRegisteredEvent  事件
- 调用注册方法
- registrationLifecycles postProcessAfterStartRegister 
- registrationManagementLifecycles postProcessBeforeStartRegisterManagement
- 发布 InstanceRegisteredEvent 事件

AbstractAutoServiceRegistration 是抽象类，没有实现类，估暂时没有使用。



EurekaServiceRegistry 的注册方法 register：

- maybeInitializeClient(reg);
- 设置初始状态 InstanceStatus.UP
- 注册 healthCheckHandler



EurekaClientAutoConfiguration 的 start 方法之后，EurekaServerInitializerConfiguration 的 start 执行：

- 调用 eurekaServerBootstrap 的初始化方法 contextInitialized
  - initEurekaServerContext
    - 设置 JsonXStream 和 XmlXStream 的转换器
    - EurekaServerContextHolder 设置 serverContext
    - 同步 this.registry.syncUp()
    - registry.openForTraffic
  - 发布 EurekaRegistryAvailableEvent 事件 
  - 标记 running = true
  - 发布 EurekaServerStartedEvent 事件