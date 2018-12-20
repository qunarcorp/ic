[上一页](quickstart.md)
[回目录](../../README.md)
[下一页](design.md)

# 安装

### 初始化数据库
创建mongo数据库
```
> use dc
switched to db dc
```

创建mongo数据库用户
```
> db.createUser({user:'username',pwd:'pwd',roles:['readWrite']})
Successfully added user: { "user" : "username", "roles" : [ "readWrite" ] }
```
创建collection
```
> db.createCollection("eventinfos")
{ "ok" : 1 }
> db.createCollection("identitycounters")
{ "ok" : 1 }
> db.createCollection("listenerinfos")
{ "ok" : 1 }
> db.createCollection("producerinfos")
{ "ok" : 1 }
> db.createCollection("typeinfos")
{ "ok" : 1 }
```
### 初始化数据
```
db.identitycounters.insert({
     "model" : "EventInfo",
     "field" : "id",
     "count" : 0.0,
     "__v" : 0
 })

db.propertyinfos.insert({
     "key" : "cache.schema",
     "version" : 0
 })
 
db.propertyinfos.insert({
     "key" : "cache.producer",
     "version" : 0
 })
 
```

### 配置文件
*mongodb.properties*
```
# 必填，mongo数据库连接地址
mongo.hosts=<address>:<port>
# 必填，mongo数据库用户名 
mongo.username=userName
# 必填，mongo数据库密码
mongo.password=pwd
# 必填，mongo数据库
mongo.database=dc
```

## 部署启动
进入ic_task目录，执行打包命令
>mvn package -Dmaven.test.skip=true

进入ic_task/target，复制 ic_task.war 到%TOMCAT_HONE%/webapps下

进入%TOMCAT_HOME%/conf目录下，编辑server.xml,在host中添加
> \<Context path="/" docBase="%TOMCAT_HOME%/webapps/ic_task" reloadable="true" crossContext="true" />

进入%TOMCAT_HOME%/bin 目录下，运行startup.sh(startup.bat) 启动tomcat服务

进入%TOMCAT_HOME%/logs 目录下,查看catalina.out文件即可查看日志


[上一页](quickstart.md)
[回目录](../../README.md)
[下一页](design.md)
