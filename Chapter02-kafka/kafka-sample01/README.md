```bash
$ curl -X POST http://localhost:8080/send/foo/bar

$ curl -X POST http://localhost:8080/send/foo/fail
```

出现异常，重试 2 次，然后进入死信队列，总共执行 3 次。

## 参考

- https://github.com/spring-projects/spring-kafka/tree/main/samples
