[上一页](arch.md)
[回目录](../../README.md)
[下一页](message.md)


# 表结构介绍

### eventinfos表

用于保存所有的消息

### identitycounters表

用于保存消息总数

### listenerinfos表

用于保存consumer信息

### producerinfos表

用于保存producer信息

### typeinfos表

用于保存消息类型及格式信息

### propertyinfos表

用于保存配置版本信息,包括更新或添加producer（即修改producerinfos表），更新或添加消息类型（即修改typeinfos表）
当改动了producerinfos表时，要将key=cache.producer的version加1
当改动了typeinfos表时，要将key=cache.schema的version加1


[上一页](arch.md)
[回目录](../../README.md)
[下一页](message.md)