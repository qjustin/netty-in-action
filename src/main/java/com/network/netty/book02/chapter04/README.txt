主要讲述TCP 粘包/拆包问题
Client 发送两个数据包D1、D2：

1. 服务端分两次读取到两个独立的数据包，D1和D2，没有粘包/拆包
2. 服务端分一次接收到了两个数据包，D1和D2粘合在一起，称为TCP粘包
3. 服务端分两次读取到两个数据包，第一次读取到完整D1和D2的部分内容，第二次读取到D2的剩余内容，称为TCP拆包

TCP粘包/拆包发生的原因：
1. 应用write写入的字节数大于Socket发送缓冲区的大小。
2. 进行MSS大小的TCP分段；
3. 以太网帧payload大于MTU进行IP分片

解决办法
定长：每个报文定长200，不够补空格
分隔符：在结尾增加回车换行符进行分割
消息头/体：消息头中包含表示消息长度的字段，通常是int32