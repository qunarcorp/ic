[上一页](message.md)
[回目录](../../README.md)
[下一页](consumer.md)

# 发送消息(producer)

### 发送POST请求
其中source、type、operator、timestamp是所有消息的公共字段，也是producer的必填字段，其他字段会根据消息类型配置表中的配置进行校验（包括必填字段和字段值类型的校验），消息发送成功会收到该消息在数据库中存储的id。
* source表示消息来源
* type表示消息类型，值必须已在消息配置表（typeinfos）中添加的
* operator表示操作人
* timstamp表示时间戳

POST /api/v2/event

*HEADER*
```
Content-Type: application/json
```

*请求参数*
```json
{
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
}
```
*发送成功返回值*
```json
{
  "message": "success",
  "event": {
    "id": 7576300
  }
}

```

*发送失败返回值*

当producer没有传userName字段时表示userName是必填字段
```json
{
  "message": "com.github.fge.jsonschema.core.report.ListProcessingReport: failure\n--- BEGIN MESSAGES ---\nerror: object has missing required properties ([\"userName\"])\n    level: \"error\"\n    schema: {\"loadingURI\":\"#\",\"pointer\":\"\"}\n    instance: {\"pointer\":\"\"}\n    domain: \"validation\"\n    keyword: \"required\"\n    required: [\"userName\",\"operator\",\"repo\",\"source\",\"type\"]\n    missing: [\"userName\"]\n---  END MESSAGES  ---\n"
}
```

[上一页](message.md)
[回目录](../../README.md)
[下一页](consumer.md)
