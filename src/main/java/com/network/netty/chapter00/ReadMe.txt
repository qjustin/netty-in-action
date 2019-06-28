// 查看局部文件句柄限制
# ulimit -n
    1024

// 查看全局文件句柄限制
# cat /proc/sys/fs/file-max
    399917

// 修改局部文件句柄限制
# vi /etc/security/limits.conf
    *hard nofile 1000000
    *soft nofile 1000000
    hard nofile 1000000
    soft nofile 1000000

// 修改全局文件句柄限制
# vi /etc/sysctl.conf
    fs.file-max=1000000

// 让配置生效
# sysctl -p

// 重启系统让所有配置生效
# reboot

test01: 服务端多端口监听，客户端多端口连接, 测试并发连接数
test02：echo 客户端发的内容，服务端直接将收到的原文返回客户端，例如：客户端发ABC，服务端收到ABC后返回给客户端
test03: 50万客户端，器中50个客户端按照每秒发送一条1k的消息到服务端，服务端将消息分发给50万客户端。
            v1：网上照抄，客户端连接成功时发送一条信息，之后不再发送，多端口监听与连接
            v2: 移除了handler事件，客户端连接成功时发送一条信息，之后不再发送，多端口监听与连接
            v3: 5万订阅者不发送消息，多端口监听与连接, 另起一个10个客户端每秒发送一条消息，TCP_NODELAY=true 有OOM
            v4：
test04:


