[回目录](../../README.md)
[下一页](install.md)

# 快速入门

### 配置可接收的消息

参见 [配置消息](docs/cn/message.md)

### 发送消息

```
curl -i -X POST \
   -H "Content-Type:application/json" \
   -d \
'{
    "userName": "san.zhang",
      "classes": [
        "math",
        "english",
        "chinese"
      ],
      "age": 14,
      "sex": "male",
      "hobby": "basketball",
      "source": "courseSelectionSystem",
      "type": "student-chooseClasses",
      "operator": "san.zhang",
      "timestamp": 1512555333139
  }' \
 'http://ip:port/api/v2/event'
```

## 消费消息

```
curl -i -X GET \
 'http://ip:port/api/v2/event/listener/demo2?type=student-chooseClasses'
```

[回目录](../../README.md)
[下一页](install.md)