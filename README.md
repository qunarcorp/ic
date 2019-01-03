# IC

IC是去哪儿公司内部CI、CD以及devops体系建设过程中使用的消息系统和数据中心。由于其基于HTTP协议的特性，具有跨平台、跨语言的优点。而devops体系搭建中，会引入各种开源工具，这些工具的语言差异也很大。基于IC，我们不仅快速实现了流程自动化，而且系统解耦，自动化进程大大提高。

## IC主要提供以下功能：
* 消息接收
* 消息监听（轮询方式实时返回，长轮询方式可能延时2s，可自行修改）
* 历史消息按时间段查询
* 历史消息按id查询

## 依赖
* MongoDB 3.2.8
* JDK 8
* Tomcat 8





## 文档
* [快速开始](docs/cn/quickstart.md)
* [安装](docs/cn/install.md)
* [设计背景](docs/cn/design.md)
* [架构概览](docs/cn/arch.md)
* [表结构介绍](docs/cn/table.md)
* [配置消息](docs/cn/message.md)
* [发送消息](docs/cn/producer.md)
* [消费消息](docs/cn/consumer.md)
* [查询消息](docs/cn/query.md)
* [进阶说明](docs/cn/advance.md)


## 技术支持

### QQ群
![QQ](docs/images/qq.png)

