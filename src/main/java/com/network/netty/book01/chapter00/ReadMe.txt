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
test04: 客户端发一条消息给服务端，等待回复，服务端收到消息后回复客户端，客户端收到消息后再发送一条消息给服务器。这样循环
test05：客户端发一条消息给服务端，服务端收到消息后直接丢弃，然后回复客户端，客户端收到消息后也丢弃。这样循环
test06: 计算1000的阶乘 https://www.cnblogs.com/yaoyuan2/p/9656777.html
    BtyeBuf读操作主要提供以下功能：

        readByte：取1字节的内容；
        skipBytes： 跳过内容
        readUnsignedByte：取1字节的内容，返回（(short) (readByte() & 0xFF)）；（能把负数转换为无符号吗？）
        readShort：取2字节的内容，返回转换后的short类型；
        readUnsignedShort：取2字节的内容，返回readShort() & 0xFFFF；
        readMedium：取3字节的内容，返回转换后的int类型；
        readUnsignedMedium：取3字节的内容，返回转换后的int类型；
        readInt：取4字节的内容；
        readUnsignedInt：取4字节的内容，返回readInt() & 0xFFFFFFFFL；
        readLong：取8字节的内容；
        readChar：取1字节的内容；
        readFloat：取4字节的int内容，转换为float类型；
        readDouble：取8字节的long内容，转换为double类型；
        readBytes：取指定长度的内容，返回ByteBuf类型；
        readSlice：取指定长度的内容，返回ByteBuf类型；
        readBytes：取指定长度的内容到目标容器。
    写操作
    写操作提供的功能主要是往ByteBuf中写入byte内容，不再一一赘述。主要区别在于写入前根据类型转换为相对应长度的byte数组。
    主要函数是：writeBoolean、writeByte、writeShort、writeMedium、writeInt、writeLong、writeChar、writeFloat、writeDouble、writeBytes、writeZero。
    边界值安全
    不论读或写，肯定会存在ByteBuf数据为空或满的情形，作为数据容器，要存在边界值检查，确保读写安全。


