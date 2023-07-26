# FileBridge

文件同步传输桥程序
为了解决不同文件系统之间文件批量定制传输的场景实现的一个小工具。

基本设计图
![Base Struct](./docs/base.png)

## 核心接口

本项目有两个核心的接口，分别用于处理与文件服务对接及对文件进行过滤筛选判定

### IFileService

该接口是实现文件服务的核心接口，将对文件操作抽象实现，由框架层完成同步逻辑

### IFilter

该接口是实现对文件进行过滤的接口，该接口对`FileMeta`进行判断，返回判断结果是否进行同步

## ROADMAP

* [x] Filter
  * [x] 前缀过滤
  * [x] 后缀过滤
  * [x] 正则过滤
  * [x] 修改时间过滤
  * [x] 文件大小的过滤
* [x] FileService Plugins
  * [x] 本地文件 plugins
  * [x] Ftp Plugins
  * [x] Minio Plugins
  * [x] S3协议支持（minio base)
* [ ] 运行方式的优化
  * [ ] 增设本地缓存机制
  * [x] 增加 多入单出 的运行模式
  * [ ] 增加 单入多出 的运行模式
  * [ ] GraalVM加持构建二进制分发
  * [ ] 可视化管理平台
  * [ ] 加密的配置文件
  * [ ] 分片传输
* [ ] 增加更多的日志跟踪日志

## 同步命令

```
   java -Dfile.encoding=utf8 -jar FileBridge.jar /path/to/conf.json
```

## 配置文件说明

配置文件为数组，允许设置多个同步通道，同步时将会依次运行。 下面以单个文件通道为例说明：

```
  {
    // 源插件配置
    "input": [{}],
    // 目标插件配置
    "output": {},
    // 文件过滤器配置
    "file_filter":[],
    // 镜像模式参数 不设置则为移动模式
    "mirror_param":{
        //  1： 追加模式、2：全量模式
        "mirror_mode":1,
        // 镜像标记文件地址 不设置则为输出目录
        "clone_result_path":""
    }
  }
```

## 过滤器配置说明

### 前缀过滤器

```
{
    // 固定值，插件类型
    "type": "prefix",
    // 后缀
    "prefix": "sync"
}
```

### 后缀过滤器

```
{
    // 固定值，插件类型
    "type": "ext",
    // 后缀
    "ext": "zip"
}
```

### 正则过滤器

```
{
    // 固定值，插件类型
    "type": "pattern",
    // 后缀
    "pattern": ".*\\.zip"
}
```

### 修改时间过滤器

```
{
    // 固定值，插件类型
    "type": "modifyTime",
    // 扫描文件配置，只同步最后编辑时间n秒前的文件，设置为-1时为全部文件，默认值10
    "modifyWatcher": 10
}
```

### 文件大小过滤器

```
{
    // 固定值，插件类型
    "type": "fileSize",
    // 文件大小范围，不输入则为不约束，单位为byte
    "maxSize": "10000",
    "minSize": "10"
}
```

## 数据插件配置说明

### FTP插件

### 本地文件插件

```
{
    // 固定值，插件类型
    "type": "local",
    // 本地同步根路径
    "root": "/path/to/dir"
}
```

```
{
    // 固定值，插件类型
    "type": "ftp",
    // FTP服务器同步的根目录
    "root": "/path/to/dir",
    // FTP服务器的IP
    "ip": "1.2.3.4",
    // FTP服务器的端口
    "port": 21,
    // FTP服务器的用户名
    "username": "root",
    // FTP服务器的密码
    "password": "123456"
}
```

### minio插件

```
{
    // 固定值，插件类型
      "type": "minio",
    // 上传的桶
      "bucket": "fstest",
    // minio服务器url
      "url": "http://1.2.3.4:5678",
    // accessKey
      "accessKey": "ak",
    // secretKey
      "secretKey": "sk",
    // 是否打印minio的trace日志
      "trace": true
    }
```
