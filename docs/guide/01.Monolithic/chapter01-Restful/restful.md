# Restful

REST 是 **Re**presentational **S**tate **T**ransfer的缩写，是一种分布式超媒体系统的架构风格 。Roy Fielding 于 2000 年在其著名
论文中首次提出了该风格。自那时起，它已成为构建基于 Web 的 API（*应用程序编程接口*）的最广泛使用的方法之一。

REST 不是一种协议或标准，而是一种架构风格。在开发阶段，API 开发人员可以通过多种方式实现 REST。

## REST 的六大指导原则

RESTful 架构的六个指导原则或约束是：

1. **统一接口**：RESTful 服务使用标准的 HTTP 方法（GET、POST、PUT、DELETE 等）来操作资源。这些方法代表了不同的操作语义。
2. **客户端-服务器**：服务器和客户端也可以被替换和独立开发，只要它们之间的接口没有改变。
3. **无状态**：RESTful 服务本身是无状态的，每个请求都包含了执行操作所需的所有信息。这意味着服务器不需要保持客户端的状态。
4. **可缓存**：RESTful 服务的响应可以被缓存，以提高性能和减少服务器负载。
5. **分层系统**：RESTful 服务可以由多个层次组成，如负载均衡器、缓存服务器、应用服务器等。客户端无需知道服务的内部结构。
6. **按需编码（*可选*）**：REST 还允许客户端通过下载和执行小程序或脚本形式的代码来扩展功能。

## REST API设计

| REST API                  | Description |
|---------------------------|-------------|
| *GET /items*              | 查询列表        |
| *GET /items/page*         | 分页查询        |
| *POST /items*             | 创建          |
| *GET /items/{id}*         | 查询详情        |
| *PUT /items/{id}*         | 通过 ID 修改    |
| *DEPETE /items/{id}*      | 通过 ID 删除    |
| GET /customers/{id}/items |             |

1. 资源**可以是单例，也可以是集合**。
2. 资源也**可能包含子集合资源**。

## 长时间运行任务的 REST API 设计

传统上，所有支持长时间运行操作的 API 都是采用以下方法构建的：

![img](https://restfulapi.net/wp-content/uploads/Long-Running-API.png)

**资源创建和启动**：创建资源来表示长时间运行任务的启动。此资源可以是客户端可以向其发送 POST 请求以启动任务的唯一
URI。请求主体可能包含定义任务所需的参数或数据。

```json
POST /api/tasks

Status: 202 Accepted
Location: /tasks/12345
Content-Type: application/json

{
  "taskId": 12345,
  "status": "pending",
  "createdAt": "2023-11-04T10:00:00Z"
}
```

> *202（已接受）状态*代码表示请求已被接受处理，但处理尚未完成。

**查询任务状态**：提交任务后，创建一个资源来表示长期运行任务的当前状态。此资源允许客户检查任务的进度和结果。我们可以记录任务的各种可能状态，以便客户采取相应的行动。

```json
GET /api/tasks/12345

Status: 200 OK
Content-Type: application/json
{
  "taskId": 12345,
  "status": "in progress",
  "createdAt": "2023-11-04T10:00:00Z",
  "progress": {
    "percentage": 45,
    "currentStep": "data transformation"
  }
}
```

**查询结果**：一旦任务完成，此 API 可以返回“*已完成*”状态以及详细结果的 URI 或新创建资源的 URI（任意）。

```json
GET /api/tasks/12345

Status: 200 OK
Content-Type: application/json

{
  "taskId": 12345,
  "status": "completed",
  "createdAt": "2023-11-04T10:00:00Z",
  "completedAt": "2023-11-04T10:15:00Z",
  "result": {
    "newResource": "https://example.com/resources/123-456"
  }
}
```

**取消正在进行中的任务**：

```bash
DELETE /api/tasks/12345
```

最佳实践：

- 不要等待长时间运行的任务作为普通 HTTP 请求处理的一部分完成。
- 提供专用URL来查询任务状态。我们还应该考虑使用 webhook 通知或服务器发送事件，以便服务器在任务状态发生变化时将更新推送给客户端，从而减少客户端重复轮询更新的需要。
- 提供取消长时间运行的任务的机制。
- 任务执行过程不应以任何方式依赖于客户端。
- 考虑`Retry-After`在 API 响应中使用标头字段来指示如果之前的请求由于任何原因未被接受，用户代理在重试相同请求之前应该等待多长时间。
- 返回错误响应时，请考虑使用[RFC 7807](https://datatracker.ietf.org/doc/html/rfc7807#section-3.1) 规范。

## 版本管理

版本语义：https://semver.org/ ，格式如下：

```
MAJOR.MINOR.PATCH
```

- **主要版本：** URI 中使用的版本，表示对 API 的重大更改。从内部来看，新的主要版本意味着创建新的 API，版本号用于路由到正确的主机。
- **次要版本和补丁版本：**这些版本对客户端来说是透明的，并在内部用于向后兼容的更新。它们通常在更改日志中传达，以通知客户端有关新功能或错误修复的信息。

Restful 版本管理有三种方式：

- **URI Path**

  ```bash
  http://www.example.com/api/v1/products
  http://api.example.com/v1/products
  ```

- **Query Params**

  ```bash
  http://www.example.com/api/products?version=1
  ```

- **Custom Header**

  ```bash
  http://www.example.com/api/products
  
  Accept: version=1.0
  Accept-version: v1
  ```

这种方法与前两种方法的主要区别在于，它不会用版本控制信息弄乱 URI。

- #### **Content negotiation**

  ```bash
  http://www.example.com/api/products
  Accept: application/vnd.xm.device+json; version=1
  ```

  这种方法允许我们对单个资源表示进行版本控制，而不是对整个 API
  进行版本控制，这使我们能够更精细地控制版本控制。它还会在代码库中占用更少的空间，因为我们在创建新版本时不必分叉整个应用程序。

    - **优点：**允许我们对单个资源表示进行版本控制，而不是对整个 API 进行版本控制，这使我们能够更精细地控制版本控制。占用空间更小。不需要实施
      URI 路由规则。
    - **缺点：**要求 HTTP 标头带有媒体类型，使得使用浏览器测试和探索 API 变得更加困难

参考文章：

- [How to Version a REST API](https://www.freecodecamp.org/news/how-to-version-a-rest-api/)

- [How to Version a REST API?](https://restfulapi.net/versioning/)

## 标准字段

参考 [标准字段](https://cloud.google.com/apis/design/standard_fields?hl=zh-cn)

| 名称                 | 类型                                                                                                 | 说明                                                                                                                                                      |
|:-------------------|:---------------------------------------------------------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------------|
| `name`             | `string`                                                                                           | `name` 字段应包含[相对资源名称](https://cloud.google.com/apis/design/resource_names?hl=zh-cn#relative_resource_name)。                                              |
| `parent`           | `string`                                                                                           | 对于资源定义和 List/Create 请求，`parent` 字段应包含父级[相对资源名称](https://cloud.google.com/apis/design/resource_names?hl=zh-cn#relative_resource_name)。                   |
| `create_time`      | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 创建实体的时间戳。                                                                                                                                               |
| `update_time`      | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 最后更新实体的时间戳。注意：执行 create/patch/delete 操作时会更新 update_time。                                                                                                |
| `delete_time`      | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 删除实体的时间戳，仅当它支持保留时才适用。                                                                                                                                   |
| `expire_time`      | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 实体到期时的到期时间戳。                                                                                                                                            |
| `start_time`       | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 标记某个时间段开始的时间戳。                                                                                                                                          |
| `end_time`         | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 标记某个时间段或操作结束的时间戳（无论其成功与否）。                                                                                                                              |
| `read_time`        | [`Timestamp`](https://github.com/google/protobuf/blob/master/src/google/protobuf/timestamp.proto)  | 应读取（如果在请求中使用）或已读取（如果在响应中使用）特定实体的时间戳。                                                                                                                    |
| `time_zone`        | `string`                                                                                           | 时区名称。它应该是 [IANA TZ](http://www.iana.org/time-zones) 名称，例如“America/Los_Angeles”。如需了解详情，请参阅 https://en.wikipedia.org/wiki/List_of_tz_database_time_zones。 |
| `region_code`      | `string`                                                                                           | 位置的 Unicode 国家/地区代码 (CLDR)，例如“US”和“419”。如需了解详情，请访问 http://www.unicode.org/reports/tr35/#unicode_region_subtag。                                          |
| `language_code`    | `string`                                                                                           | BCP-47 语言代码，例如“en-US”或“sr-Latn”。如需了解详情，请参阅 http://www.unicode.org/reports/tr35/#Unicode_locale_identifier。                                              |
| `mime_type`        | `string`                                                                                           | IANA 发布的 MIME 类型（也称为媒体类型）。如需了解详情，请参阅 https://www.iana.org/assignments/media-types/media-types.xhtml。                                                    |
| `display_name`     | `string`                                                                                           | 实体的显示名称。                                                                                                                                                |
| `title`            | `string`                                                                                           | 实体的官方名称，例如公司名称。它应被视为 `display_name` 的正式版本。                                                                                                              |
| `description`      | `string`                                                                                           | 实体的一个或多个文本描述段落。                                                                                                                                         |
| `filter`           | `string`                                                                                           | List 方法的标准过滤器参数。请参阅 [AIP-160](https://google.aip.dev/160)。                                                                                              |
| `query`            | `string`                                                                                           | 如果应用于搜索方法（即 [`:search`](https://cloud.google.com/apis/design/custom_methods?hl=zh-cn#common_custom_methods)），则与 `filter` 相同。                            |
| `order_by`         | `string`                                                                                           | 指定 List 请求的结果排序。                                                                                                                                        |
| `progress_percent` | `int32`                                                                                            | 指定操作的进度百分比 (0-100)。值 `-1` 表示进度未知.                                                                                                                       |
| `request_id`       | `string`                                                                                           | 用于检测重复请求的唯一字符串 ID。                                                                                                                                      |
| `resume_token`     | `string`                                                                                           | 用于恢复流式传输请求的不透明令牌。                                                                                                                                       |
| `labels`           | `map<string, string>`                                                                              | 表示 Cloud 资源标签。                                                                                                                                          |
| `show_deleted`     | `bool`                                                                                             | 如果资源允许恢复删除行为，相应的 List 方法必须具有 `show_deleted` 字段，以便客户端可以发现已删除的资源。                                                                                         |
| `update_mask`      | [`FieldMask`](https://github.com/google/protobuf/blob/master/src/google/protobuf/field_mask.proto) | 它用于 `Update` 请求消息，该消息用于对资源执行部分更新。此掩码与资源相关，而不是与请求消息相关。                                                                                                   |
| `validate_only`    | `bool`                                                                                             | 如果为 true，则表示仅应验证给定请求，而不执行该请求。                                                                                                                           |

## 状态码

HTTP 定义了标准状态代码，可用于传达客户端请求的结果。状态代码分为五类。

- **1xx：信息性**——传达传输协议级信息。
- **2xx：成功**——表示客户端的请求已被成功接受。
- **3xx：重定向**——表示客户端必须采取一些额外的操作才能完成他们的请求。
- **4xx：客户端错误**——此类错误状态代码指向客户端。
- **5xx：服务器错误**——服务器对这些错误状态代码负责。

### REST 特定的 HTTP 状态代码

#### 200（正常）

它表示 REST API 成功执行了客户端请求的任何操作，并且 2xx 系列中不再适合使用更具体的代码。

与 204 状态代码不同，200 响应应包含响应主体。响应返回的信息取决于请求中使用的方法，例如：

- GET 在响应中发送与请求的资源相对应的实体；
- HEAD 与请求的资源相对应的实体头字段在响应中发送，没有任何消息正文；
- POST 描述或包含操作结果的实体；
- TRACE 包含终端服务器收到的请求消息的实体。

#### 201（已创建）

每当在集合中创建资源时，REST API 都会以 201 状态代码进行响应。有时，由于某些控制器操作而创建新资源，在这种情况下 201
也是合适的响应。

新创建的资源可以通过响应实体中返回的 URI 来引用，其中 Location 标头字段给出了该资源最具体的 URI。

源服务器必须在返回 201 状态代码之前创建资源。如果无法立即执行操作，则服务器应改为响应 202（已接受）。

#### 202（已接受）

202 响应通常用于需要很长时间才能处理的操作。它表示请求已被接受处理，但处理尚未完成。该请求可能会或可能不会最终得到处理，甚至可能在处理时被禁止。

其目的是允许服务器接受某些其他进程的请求（可能是每天仅运行一次的批处理进程），而不需要用户代理与服务器的连接持续到该进程完成。

此响应返回的实体应包括请求的当前状态指示以及指向状态监视器（作业队列位置）的指针或用户可预期请求何时被满足的估计。

#### 204（无内容）

当 REST API 拒绝在响应消息正文中发回任何状态消息或表示时，通常会响应`PUT`、`POST`或请求来发送 204 状态代码。`DELETE`

API 还可以结合 GET 请求发送 204，以表明所请求的资源存在，但没有状态表示可包含在正文中。

如果客户端是用户代理，则它不应更改其导致发送请求的文档视图。此响应主要用于允许输入操作，而不会导致用户代理的活动文档视图发生变化。但是，任何新的或更新的元信息都应应用于当前位于用户代理动态视图中的文档。

204 响应不得包含消息正文，因此始终以标头字段后的第一个空行终止。

#### 301（永久移动）

301 状态代码表示 REST API 的资源模型已进行了重大重新设计，并且已为客户端请求的资源分配了新的永久 URI。REST API 应在响应的
Location 标头中指定新的 URI，并且所有未来请求都应定向到给定的 URI。

您几乎不会在 API 中使用此响应代码，因为您始终可以使用新 API 的 API 版本控制，同时保留旧 API。

#### 302（找到）

HTTP 响应状态代码 302 Found 是执行 URL 重定向的常用方法。具有此状态代码的 HTTP 响应还将在 Location 标头字段中提供一个
URL。具有此代码的响应会邀请用户代理（例如 Web 浏览器）对 location 字段中指定的新 URL 发出第二个请求（否则相同）。

许多 Web 浏览器以违反此标准的方式实现此代码，将新请求的请求类型更改为 GET，而不管原始请求中使用的类型（例如 POST）。RFC 1945
和 RFC 2068 规定不允许客户端更改重定向请求的方法。已添加状态代码 303 和 307，用于希望明确说明需要客户端做出哪种反应的服务器。

#### 303 (查看其他)

303 响应表示控制器资源已完成其工作，但它不会发送可能不需要的响应主体，而是向客户端发送响应资源的 URI。响应可以是临时状态消息的
URI，也可以是某个已经存在的、更永久的资源的 URI。

一般来说，303 状态代码允许 REST API 发送对资源的引用，而无需强制客户端下载其状态。相反，客户端可以向 Location 标头的值发送
GET 请求。

303 响应一定不能被缓存，但第二个（重定向）请求的响应可能可以缓存。

#### 304（未修改）

此状态代码与 204（“无内容”）类似，响应主体必须为空。关键区别在于，当主体中没有任何内容可发送时使用 204，而当资源自请求标头
If-Modified-Since 或 If-None-Match 指定的版本以来未发生过修改时使用 304。

在这种情况下，无需重新传输资源，因为客户端仍然具有之前下载的副本。

使用这种方式可以节省服务器和客户端的带宽和重新处理，因为只需要发送和接收标题数据，而服务器需要重新处理整个页面，然后使用服务器和客户端的更多带宽再次发送。

#### 307（临时重定向）

307 响应表示 REST API 不会处理客户端的请求。相反，客户端应将请求重新提交到响应消息的 Location 标头指定的
URI。但是，未来的请求仍应使用原始 URI。

REST API 可以使用此状态代码为客户端请求的资源分配临时 URI。例如，307 响应可用于将客户端请求转移到另一台主机。

临时 URI 应由响应中的 Location 字段提供。除非请求方法是 HEAD，否则响应实体应包含一个简短的超文本注释，其中包含指向新 URI
的超链接。如果收到 307 状态代码以响应除 或 之外的请求，则`GET`用户`HEAD`代理不得自动重定向请求，除非用户可以确认，因为这可能会改变发出请求的条件。

#### 400（错误请求）

400 是通用客户端错误状态，当没有其他 4xx 错误代码适用时使用。错误可能包括格式错误的请求语法、无效的请求消息参数或欺骗性的请求路由等。

客户端不应该在未做修改的情况下重复该请求。

#### 401（未授权）

401 错误响应表示客户端尝试在未提供适当授权的情况下对受保护的资源进行操作。它可能提供了错误的凭据，或者根本没有提供凭据。响应必须包含
WWW-Authenticate 标头字段，其中包含适用于所请求资源的质询。

客户端可以使用合适的授权标头字段重复该请求。如果请求中已包含授权凭证，则 401 响应表示已拒绝对这些凭证进行授权。如果 401
响应包含与先前响应相同的质询，并且用户代理已尝试进行身份验证至少一次，则应向用户显示响应中给出的实体，因为该实体可能包含相关的诊断信息。

#### 403（禁止）

403 错误响应表示客户端的请求格式正确，但 REST API 拒绝执行该请求，即用户不具备获取资源所需的权限。403 响应不是客户端凭证不足的情况；那应该是
401（“未授权”）。

身份验证不会有帮助，并且不应重复请求。与 401 Unauthorized 响应不同，身份验证不会产生任何影响。

#### 404（未找到）

404 错误状态代码表示 REST API 无法将客户端的 URI 映射到资源，但将来可能会可用。客户端的后续请求是允许的。

没有给出该情况是暂时的还是永久的指示。如果服务器通过某种内部可配置机制知道旧资源永久不可用且没有转发地址，则应使用
410（消失）状态代码。当服务器不希望透露拒绝请求的确切原因或没有其他适用的响应时，通常使用此状态代码。

#### 405（方法不允许）

API 会以 405 错误响应，表示客户端尝试使用资源不允许的 HTTP 方法。例如，只读资源可能仅支持 GET 和 HEAD，而控制器资源可能允许
GET 和 POST，但不允许 PUT 或 DELETE。

405 响应必须包含 Allow 标头，其中列出了资源支持的 HTTP 方法。例如：

```
允许：GET、POST
```

#### 406（不接受）

406 错误响应表示 API 无法生成任何客户端首选的媒体类型，如 Accept 请求标头所示。例如，`application/xml`如果 API 仅愿意将数据格式化为
，则客户端对 格式的数据的请求将收到 406 响应`application/json`。

如果响应不可接受，用户代理应该暂时停止接收更多数据并询问用户关于进一步行动的决定。

#### 412（先决条件不满足）

412 错误响应表示客户端在其请求标头中指定了一个或多个先决条件，这实际上告诉 REST API 仅在满足某些条件时才执行其请求。412
响应表示这些条件不满足，因此 API 不会执行请求，而是发送此状态代码。

#### 415（不支持的媒体类型）

415 错误响应表示 API 无法处理客户端提供的媒体类型，如 Content-Type 请求标头所示。例如，`application/xml`如果 API 只愿意处理格式为
的数据，则包含 格式为 的数据的客户端请求将收到 415 响应`application/json`。

例如，客户端上传图像为 image/svg+xml，但服务器要求图像使用不同的格式。

#### 500内部服务器错误）

500 是通用 REST API 错误响应。大多数 Web 框架在执行引发异常的请求处理程序代码时都会自动使用此响应状态代码进行响应。

500 错误绝不是客户端的错误，因此，客户端重试触发此响应的相同请求并希望获得不同的响应是合理的。

API 响应是通用错误消息，当遇到意外情况并且没有更具体的消息适合时给出。

#### 501（未实现）

服务器无法识别请求方法，或者无法满足请求。通常，这意味着将来可用（例如，Web 服务 API 的新功能）。

参考 ：https://www.iana.org/assignments/http-status-codes/http-status-codes.xhtml

## 错误

请求出现错误时候，返回结果包含以下字段：

- code
- message
- status
- details
    - type
    - reason
    - metadata

常见错误码：

| HTTP |                      | 说明                                                                    |
|:-----|:---------------------|:----------------------------------------------------------------------|
| 200  | `OK`                 | 无错误                                                                   |
| 400  | `INVALID_ARGUMENT`   | 客户端指定了无效参数                                                            |
| 401  | `UNAUTHENTICATED`    | 由于 OAuth 令牌丢失、无效或过期，请求未通过身份验证。                                        |
| 403  | `PERMISSION_DENIED`  | 客户端权限不足。这可能是因为 OAuth 令牌没有正确的范围、客户端没有权限或者 API 尚未启用。                    |
| 404  | `NOT_FOUND`          | 未找到指定的资源。                                                             |
| 409  | `ABORTED`            | 并发冲突，例如读取/修改/写入冲突。                                                    |
| 429  | `RESOURCE_EXHAUSTED` | 资源配额不足或达到速率限制。                                                        |
| 499  | `CANCELLED`          | 请求被客户端取消。                                                             |
| 500  | `INTERNAL`           | 出现内部服务器错误。通常是服务器错误。                                                   |
| 501  | `NOT_IMPLEMENTED`    | API 方法未通过服务器实现。                                                       |
| 502  | 不适用                  | 到达服务器前发生网络错误。通常是网络中断或配置错误。                                            |
| 503  | `UNAVAILABLE`        | 服务不可用。通常是服务器已关闭。                                                      |
| 504  | `DEADLINE_EXCEEDED`  | 超出请求时限。仅当调用者设置的时限比方法的默认时限短（即请求的时限不足以让服务器处理请求）并且请求未在时限范围内完成时，才会发生这种情况。 |

## 命名规则

### 方法名称

方法名称**应**采用大驼峰式命名格式并遵循 `VerbNoun` 的命名惯例，其中 Noun（名词）通常是资源类型。

| 动词       | Noun   | 方法名称         | 请求消息                | 响应消息                 |
|:---------|:-------|:-------------|:--------------------|:---------------------|
| `list`   | `Book` | `listBooks`  | `ListBooksRequest`  | `ListBooksResponse`  |
| `get`    | `Book` | `getBook`    | `GetBookRequest`    | `Book`               |
| `create` | `Book` | `createBook` | `CreateBookRequest` | `Book`               |
| `update` | `Book` | `updateBook` | `UpdateBookRequest` | `Book`               |
| `rename` | `Book` | `renameBook` | `RenameBookRequest` | `RenameBookResponse` |
| `delete` | `Book` | `deleteBook` | `DeleteBookRequest` | void                 |

方法名称的动词部分**应该**使用用于要求或命令的[祈使语气](https://en.wikipedia.org/wiki/Imperative_mood#English)
，而不是用于提问的陈述语气。

对于标准方法，方法名称的名词部分对于除 `list` 之外的所有方法**必须**采用单数形式，而对于 `List` **必须**
采用复数形式。对于自定义方法，名词在适当情况下**可以**采用单数或复数形式。批处理方法**必须**采用复数名词形式。

### 枚举名称

枚举类型**必须**使用 UpperCamelCase 格式的名称。

枚举值**必须**使用 CAPITALIZED_NAMES_WITH_UNDERSCORES 格式。每个枚举值**必须**以分号（而不是逗号）结尾。第一个值**应该**命名为
ENUM_TYPE_UNSPECIFIED，因为在枚举值未明确指定时系统会返回此值。

```java
enum FooBar {
  // The first value represents the default and must be == 0.
  FOO_BAR_UNSPECIFIED;
  FIRST_VALUE;
  SECOND_VALUE;
}
```

### 名称缩写

对于软件开发者熟知的名称缩写，例如 `config` 和 `spec`，**应该**在 API 定义中使用这些缩写，而非完整名称。这将使源代码易于读写。而在正式文档中，
**应该**使用完整名称。示例：

- config (configuration)
- id (identifier)
- spec (specification)
- stats (statistics)

## 缓存

哪些方法可以缓存：

- **GET** 请求默认应可缓存 – 除非出现特殊情况。通常，浏览器将所有 GET 请求视为可缓存。
- **POST **请求默认不可缓存，但如果在响应中添加`Expires`标头或带有指令的标头以明确允许缓存，则可以使其可缓存。
  `Cache-Control`
- **PUT**对和请求的响应**DELETE**根本无法缓存。

HTTP 响应头可以控制是否缓存：

- Expires：HTTP 标头指定了缓存表示的绝对过期时间。超过该时间，缓存表示将被视为*过期*，必须通过源服务器重新验证。

  ```bash
  Expires: Fri, 20 May 2016 19:20:49 GMT
  ```

- Cache-Control。标头值包含一个或多个逗号分隔的[指令](https://tools.ietf.org/html/rfc7234#page-24)
  。这些指令确定响应是否可缓存，如果可以，则确定由谁缓存以及缓存多长时间（例如`max-age`或`s-maxage`指令）。

  ```bash
  Cache-Control: max-age=3600
  ```

  可缓存的响应（无论是 GET 还是 POST 请求）也应该包含一个验证器 — — *ETag*或*Last-Modified*标头。

- ETag。值是一个不透明的字符串标记，服务器将其与资源关联，以唯一地标识资源在其生命周期内的状态*。*

  如果给定 URL 上的资源发生变化，则 *必须*`Etag`生成一个新 值 。对它们进行比较可以确定资源的两种表示是否相同。

  在请求资源时，客户端将*If-None-Match*标头字段中的*ETag*发送给服务器。服务器将请求资源的*Etag与**If-None-Match*
  标头中发送的值进行匹配。如果两个值都匹配，服务器将返回一个 不带正文的状态，告知客户端缓存的响应版本仍可使用（*新鲜*）。`304`
  `Not Modified`

  ```bash
  ETag: "abcd1234567n34jv"
  ```

- Last-Modified。请注意，Date 标头列在[禁止的标头名称](https://fetch.spec.whatwg.org/#forbidden-header-name)中。

  ```bash
  Last-Modified: Fri, 10 May 2016 09:17:49 GMT
  ```

## 压缩

### 1. Accept-Encoding

在请求资源表述时（连同 HTTP 请求），客户端会发送一个 Accept-Encoding 标头，说明客户端可以理解哪种压缩算法。

`Accept-Encoding`两个标准值是**compress**和**gzip**。

带有的示例请求`accept-encoding`，标头如下所示：

```
GET        /employees         HTTP/1.1
Host:     www.domain.com
Accept:     text/html
Accept-Encoding:     gzip,compress
```

accept-encoding 的另一种可能用法可能是：

```
Accept-Encoding: compress, gzip
Accept-Encoding:
Accept-Encoding: *
Accept-Encoding: compress;q=0.5, gzip;q=1.0
Accept-Encoding: gzip;q=1.0, identity; q=0.5, *;q=0
```

如果`Accept-Encoding`请求中存在该字段，并且服务器无法根据`Accept-Encoding`标头发送可接受的响应，则服务器应该发送带有**
406（不可接受）**状态代码的错误响应。

### 2. Content-Encoding

如果服务器理解 Accept-Encoding 中的一种压缩算法，它可以使用该算法在提供之前压缩表示。成功压缩后，服务器会通过另一个 HTTP
标头（即）告知客户端编码方案`Content-Encoding`。

```
200 OK
Content-Type:     text/html
Content-Encoding:     gzip
```

如果请求消息中实体的内容编码不为源服务器所接受，则服务器应以状态代码**415（不支持的媒体类型）**
进行响应。如果对实体应用了多种内容编码，则必须按使用顺序列出所有编码。

请注意，无论是否请求压缩，请求和响应的原始媒体类型都不会受到影响。

压缩可以节省大量带宽，成本极低，且不会增加复杂性。此外，您可能知道，大多数 Web 浏览器都会使用上述标头自动从网站托管服务器请求压缩表示。

## 内容协商

内容协商是 REST 的一个关键方面，它使客户端和服务器能够就交换数据的格式和语言达成一致。它使不同的系统能够无缝协作。

通常，[REST](https://restfulapi.net/)资源可以有多种呈现方式，主要是因为不同的客户端可能期望不同的呈现方式。然而，为了确保高效的通信，客户端和服务器必须就这些资源的呈现和交付方式达成一致。

如果由服务器上的算法来选择最佳响应表示，则称为服务器驱动协商。如果由代理或客户端进行选择，则称为代理驱动*协商*。

**大多数 REST API 实现都依赖于代理驱动的内容协商。代理驱动的内容协商依赖于HTTP 请求标头**或**资源 URI 模式**的使用。

1. 使用 HTTP 标头进行内容协商

   在服务器端，传入的请求可能附加有实体。为了确定其类型，服务器使用 HTTP 请求标头“ `**Content-Type**`”。

   ```
   Content-Type: application/json
   ```

   类似地，为了确定客户端需要哪种类型的表示，使用 HTTP 标头“ **ACCEPT** ”。它将具有上述值之一`Content-Type`。

   ```
   Accept: application/json
   ```

   通常，如果`Accept`请求中没有标头，服务器可以发送预先配置的*默认表示类型*。


2. 使用 URL 模式进行内容协商

   向服务器传递内容类型信息的另一种方法是，客户端可以在资源 URI 中使用特定扩展。例如，客户端可以使用以下方式询问详细信息：

   ```
   http://rest.api.com/v1/employees/20423.xml
   http://rest.api.com/v1/employees/20423.json
   ```

   在上面的例子中，第一个请求 URI 将返回 XML 响应，而第二个请求 URI
   将返回[JSON](https://restfulapi.net/introduction-to-json/)响应。

3. 定义偏好

   当客户端不确定其所需的表示是否存在或是否被服务器支持时，客户端可能希望在 HTTP Accept 头部中提供多个值（使用逗号分割），并可以为每个媒体类型指定一个
   `q` 值，范围从 0 到 1。这个 `q` 值表示客户端对该媒体类型的相对优先级。如果没有指定 `q` 值，默认为 `q=1.0`。

   例如：

   ```
   Accept: application/json,application/xml;q=0.9,*/*;q=0.8
   ```

   这个 Accept 头部表示客户端希望接收以下优先级的响应格式:

    1. `application/json`: 最高优先级,客户端优先接受 JSON 格式的响应。
    2. `application/xml`: 次高优先级,如果 JSON 不可用,客户端会接受 XML 格式的响应。
    3. `*/*`: 最低优先级,如果 JSON 和 XML 都不可用,客户端会接受任意格式的响应。

## 幂等性

在 RESTful Web 服务领域，幂等性指的是多次发出相同的 API 请求应产生与仅发出一次相同的结果的概念。这意味着无论您重复幂等请求多少次，结果都保持一致。

HTTP 方法的幂等性：

- GET、PUT、DELETE、HEAD、OPTIONS 和 TRACE 是幂等的

- `POST`并且`PATCH`不是幂等的

## 安全性

### REST 安全设计原则

Jerome Saltzer 和 Michael Schroeder
撰写的论文[“计算机系统中的信息保护”](http://web.mit.edu/Saltzer/www/publications/protection/)
提出了保护计算机系统中信息安全的八项设计原则，如下所述：

- **最小特权：**实体应仅拥有执行其被授权的操作所需的权限集，不能再拥有更多权限。可以根据需要添加权限，并且应在不再使用时撤销权限。
- **故障安全默认值：**用户对系统中任何资源的默认访问级别应该被“拒绝”，除非他们被明确授予“许可”。
- **机制的经济性：**设计应该尽可能简单。所有组件接口以及它们之间的交互都应该足够简单易懂。
- **完全调解：**系统应验证所有资源的访问权限，以确保这些权限是被允许的，而不应依赖于缓存的权限矩阵。如果某个资源的访问级别被撤销，但权限矩阵中却没有反映这一点，则将违反安全性。
- **开放式设计：**该原则强调了以开放的方式构建系统的重要性——没有秘密、机密的算法。
- **权限分离：**授予实体的权限不应纯粹基于单一条件，基于资源类型的条件组合是一个更好的想法。
- **最不常用机制：**它涉及不同组件之间共享状态的风险。如果一个组件可以破坏共享状态，那么它就可以破坏所有依赖于它的其他组件。
- **心理可接受性：**它指出安全机制不应使资源比没有安全机制时更难访问。简而言之，安全性不应使用户体验变差。

### 保护 REST API 的最佳实践

- 保持简单
- 始终使用 HTTPS
- 使用密码哈希
- 切勿泄露 URL 信息。用户名、密码、会话令牌和 API 密钥不应出现在 URL 中，因为这些内容可能会被捕获在 Web 服务器日志中，从而很容易被利用。
- 考虑 OAuth
- 考虑在请求中添加时间戳。除了其他请求参数外，您还可以在 API 请求中添加请求时间戳作为 HTTP
  自定义标头。服务器会将当前时间戳与请求时间戳进行比较，并且仅在合理的时间范围（例如 30
  秒）之后才接受请求。这将防止那些试图在不更改此时间戳的情况下[暴力破解](https://en.wikipedia.org/wiki/Brute-force_attack)
  您的系统的人进行非常基本的[重放攻击。](https://en.wikipedia.org/wiki/Replay_attack)
- 对输入参数进行验证

## 无状态

无状态是 REST 的 6 个关键架构约束之一。它建议使所有客户端与服务器交互都无状态。这意味着服务器不会存储有关客户端发出的最新
HTTP 请求的任何信息，它会将每个请求都视为新请求。

无状态 REST API 不会建立或维护客户端会话。客户端负责在每个请求中提供所有必要的信息，例如身份验证令牌、凭据或上下文数据。服务器不存储特定于客户端的会话数据。

因此，应用程序的会话状态完全保存在客户端。**客户端负责在自己** **这边**存储和处理与会话相关的信息。

这也意味着客户端负责在需要时向服务器发送任何状态信息。客户端和服务器之间不应该存在任何***会话亲和性***或***粘性会话。***

## SOAP 和 REST

两者区别：

- 底层协议
    - SOAP 本身是一种用于开发基于 SOAP 的 API 的协议（通过 HTTP）。
    - REST 几乎与 HTTP 同义，尽管 REST 规范并未强制要求这样做。
- 数据格式
    - SOAP 使用 XML
    - RESTful 可以使用 CSV、JSON、RSS、XML
- 状态性
    - 通常，SOAP Web 服务是无状态的 -
      但您可以通过更改服务器上的代码轻松地[使 SOAP API 具有状态。](https://docs.oracle.com/cd/E14571_01/web.1111/e13734/stateful.htm#WSADV235)
    - RESTful Web 服务是完全无状态的。管理对话状态完全由客户端自己负责。服务器不会帮你做这件事。
- 缓存
    - SOAP 通过 HTTP POST 请求发送。由于 HTTP POST 是非幂等的，因此无法在 HTTP
      级别缓存。因此，应使用[响应缓存优化模块](https://lists.w3.org/Archives/Public/www-ws/2001Aug/att-0000/ResponseCache.html)
      中提供的信息来缓存 SOAP 响应
    - RESTful ，对应 `GET `方法，可以将响应数据标记为可缓存或不可缓存。
- 使用的 HTTP 方法
    - REST 主要通过 HTTP 使用，它利用[HTTP GET、POST、PUT、DELETE 和 PATCH 方法](https://restfulapi.net/http-methods/)执行不同的
      CRUD 操作。
    - SOAP 还定义了与 HTTP 协议的绑定。绑定到 HTTP 时，所有 SOAP 请求都通过 HTTP POST 发送。
- 安全
    - REST 基于 HTTP，而 HTTP 本身就是一种非常不安全的协议。它通过 TLS 支持基本身份验证和通信加密。任何进一步的安全性都应在服务器上额外实施。
    - SOAP 安全性通过[WS-SECURITY](https://www.oasis-open.org/committees/tc_home.php?wg_abbrev=wss)
      得到了很好的标准化，它功能丰富，并且易于在应用程序代码中实现。
- 异步处理
    - 当创建/更新资源耗时时，可能需要异步请求处理。在这种情况下，REST 建议返回 HTTP 响应代码 202
      并发送队列的位置，任务完成状态将在该队列中频繁更新。REST
      本身对[JAX-RS 中的异步 API](https://docs.oracle.com/javaee/7/api/javax/ws/rs/container/AsyncResponse.html)有相当好的支持。
    - 如果您的应用程序需要保证一定程度的可靠性和安全性，那么 SOAP 1.2 提供了额外的标准来确保此类操作。例如
      WSRM – [WS-Reliable Messaging](https://en.wikipedia.org/wiki/WS-ReliableMessaging)。

## 最佳实践

- 使用名词来表示资源
- 一致性是关键
    - 使用斜杠表示层级关系
    - 不要在 URI 中使用尾部斜杠
    - 使用连字符（-）提高 URI 的可读性
    - 不要使用下划线
    - 在 URI 中使用小写字母

- 不要使用文件扩展名。推荐使用 **Content-Type**

- 切勿在 URI 中使用 CRUD 函数名称
- 使用查询组件过滤URI集合

- 不要在 URI
  中使用动词。如果需要使用动词，推荐参考 [Google Cloud Docs 的自定义方法](https://cloud.google.com/apis/design/custom_methods)

    - 以下是常用或有用的自定义方法名称的精选列表。API 设计者在引入自己的名称之前**应该**考虑使用这些名称，以提高 API
      之间的一致性。

  | 方法名称   | 自定义动词  | HTTP 动词 | 备注                                                         |
    | :--------- | :---------- | :-------- | :----------------------------------------------------------- |
  | `Cancel`   | `:cancel`   | `POST`    | 取消一个未完成的操作，例如 [`operations.cancel`](https://github.com/googleapis/googleapis/blob/master/google/longrunning/operations.proto#L100)。 |
  | `BatchGet` | `:batchGet` | `GET`     | 批量获取多个资源。如需了解详情，请参阅[列表描述](https://cloud.google.com/apis/design/standard_methods?hl=zh-cn#list)。 |
  | `Move`     | `:move`     | `POST`    | 将资源从一个父级移动到另一个父级，例如 [`folders.move`](https://cloud.google.com/resource-manager/reference/rest/v2/folders/move?hl=zh-cn)。 |
  | `Search`   | `:search`   | `GET`     | List 的替代方法，用于获取不符合 List 语义的数据，例如 [`services.search`](https://cloud.google.com/service-infrastructure/docs/service-consumer-management/reference/rest/v1/services/search?hl=zh-cn)。 |
  | `Undelete` | `:undelete` | `POST`    | 恢复之前删除的资源，例如 [`services.undelete`](https://cloud.google.com/service-infrastructure/docs/service-management/reference/rest/v1/services/undelete?hl=zh-cn)。建议的保留期限为 30 天。 |

参考文章：

- [Google Cloud API 设计指南](https://cloud.google.com/apis/design?hl=zh-cn)