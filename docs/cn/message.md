[上一页](table.md)
[回目录](../../README.md)
[下一页](producer.md)

# 配置消息

### 新增消息类型
在typeinfos表中添加一条记录，表示新增一种消息类型叫"student-chooseClasses"，表示学生选择了某些课程
```
> db.typeinfos.insert({
     "name" : "student-chooseClasses",
     "detail" : "学生选课",
     "properties" : {
         "userName" : {
             "type" : "string"
         },
         "classes" : {
             "type" : "array"
         },
         "age" : {
             "type" : "integer"
         },
         "sex" : {
             "enum" : [
                 "male",
                 "female"
             ],
             "type" : "string"
         },
         "hobby" : {
             "type" : "string"
         },
     },
     "required" : [
         "userName",
         "classes",
         "age",
         "sex"
     ]
 })

```

### 配置消息来源 ip白名单
在typeinfos表中添加一条记录，表示ip 属于courseSelectionSystem，如果配置的是".*"，表示任何ip都可能是courseSelectionSystem的机器（用于测试环境）
ps：当IC接收到一条消息时，会根据消息中的source值进行ip校验，可以避免producer乱发消息
```
> db.producerinfos.insert({
     "name" : "courseSelectionSystem",
     "ips" : [
         ".*"
     ],
     "detail" : "选课系统"
 })

```

### 更新消息类型配置版本,使消息类型的修改在IC服务中生效
将propertyinfos表中key="cache.schema"的记录的version+1
```
﻿db.getCollection('propertyinfos').update({'key': "cache.schema"}, {'$inc': {'version': 1}})
```


### 更新producer版本，使producer的修改在IC服务中生效
将propertyinfos表中key="cache.producer"的记录的version+1
```
﻿db.getCollection('propertyinfos').update({'key': "cache.producer"}, {'$inc': {'version': 1}})
```


配置完producer和consumer就可以发送和监听student-chooseClasses消息了

[上一页](table.md)
[回目录](../../README.md)
[下一页](producer.md)
