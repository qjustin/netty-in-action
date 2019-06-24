 通过SSL/TLS 保护Netty 应用程序
 构建基于Netty 的HTTP/HTTPS 应用程序
 处理空闲的连接和超时
 解码基于分隔符的协议和基于长度的协议
 写大型数据


1. SslHandler 将是ChannelPipeline 中的第一个ChannelHandler。
    这确保了只有在所有其他的ChannelHandler 将它们的逻辑应用到数据之后，才会进行加密。

2. HTTP 解码器、编码器和编解码器
将以下四个Handler加入到Pipeline中，
HttpRequestEncoder 将HttpRequest、HttpContent 和LastHttpContent 消息编码为字节
HttpResponseEncoder 将HttpResponse、HttpContent 和LastHttpContent 消息编码为字节
HttpRequestDecoder 将字节解码为HttpRequest、HttpContent 和LastHttpContent 消息
HttpResponseDecoder 将字节解码为HttpResponse、HttpContent 和LastHttpContent 消息
Client端：HttpRequestEncoder HttpResponseDecoder
Server端：HttpRequestDecoder HttpResponseEncoder

3. 聚合HTTP 消息
使用下面的Handler实现Http消息聚合
new HttpClientCodec() / new HttpObjectAggregator()
new HttpServerCodec() / new HttpObjectAggregator()

4. HTTP 压缩
Netty提供的Handler同时支持gzip和deflate编码。客户端使用以下头部信息来只是服务器支持的压缩格式
GET /encrypted-area HTTP/1.1
Host: www.example.com
Accept -Encoding: gzip, deflate
服务端压缩/客户端解压缩
new HttpClientCodec() / new HttpContentDecompressor();
new HttpServerCodec() / new HttpContentCompressor();

5. 使用HTTPS
启用HTTPS 只需要将SslHandler 添加到ChannelPipeline 的ChannelHandler 组合中。
new HttpClientCodec() / new SslHandler(engine);
new HttpServerCodec() / new SslHandler(engine);

6. WebSocket
BinaryWebSocketFrame：数据帧：二进制数据
TextWebSocketFrame：数据帧：文本数据
ContinuationWebSocketFrame：数据帧：属于上一个BinaryWebSocketFrame 或者TextWeb-
SocketFrame：的文本的或者二进制数据
CloseWebSocketFrame：控制帧：一个CLOSE 请求、关闭的状态码以及关闭的原因
PingWebSocketFrame：控制帧：请求一个PongWebSocketFrame
PongWebSocketFrame：控制帧：对PingWebSocketFrame 请求的响应

7. 空闲的连接和超时
IdleStateHandler: 当连接空闲时间太长时，将会触发一个IdleStateEvent 事件。然后，你可以通过在你的ChannelInboundHandler 中重写userEventTriggered()方法来处理该IdleStateEvent 事件
ReadTimeoutHandler: 如果在指定的时间间隔内没有收到任何的入站数据，则抛出一个ReadTimeoutException 并关闭对应的Channel。可以通过重写你的ChannelHandler 中的exceptionCaught()方法来检测该ReadTimeoutException
WriteTimeoutHandler: 如果在指定的时间间隔内没有任何出站数据写入，则抛出一个WriteTimeoutException 并关闭对应的Channel 。可以通过重写你的ChannelHandler 的exceptionCaught()方法检测该WriteTimeoutException

心跳实例(demo06):了当使用通常的发送心跳消息到远程节点的方法时，如果在60 秒之内没有接收或者发送任何的数据，
我们将如何得到通知；如果没有响应，则连接会被关闭。

8. 解码基于分隔符的协议和基于长度的协议

9. 基于分隔符的协议
DelimiterBasedFrameDecoder: 使用任何由用户提供的分隔符来提取帧的通用解码器
LineBasedFrameDecoder: 提取由行尾符（\n 或者\r\n）分隔的帧的解码器(每个分隔符为一帧)。这个解码器比DelimiterBasedFrameDecoder 更快
使用DelimiterBasedFrameDecoder，只需要将特定的分隔符序列指定到其构造函数即可。

10. 基于长度的协议
FixedLengthFrameDecoder: 提取在调用构造函数时指定的定长帧
LengthFieldBasedFrameDecoder: 根据编码进帧头部中的长度值提取帧；该字段的偏移量以及长度在构造函数中指定.

FixedLengthFrameDecoder  其在构造时已经指定了帧长度为8字节。 32字节将被解码为8帧
LengthFieldBasedFrameDecoder 用于处理变长帧，例如：消息头部的帧大小不是固定值。
    LengthFieldBasedFrameDecoder用前两个字节表示内容的长度。解码前14字节，解码后12字节，因为头两个字节表示内容的实际长度。

10. 写大型数据
写大型数据时，需要准备好处理到远程节点的连接是慢速连接的情况，这种情况会导致内存释放的延迟。
FileRegion 接口的实现 来实现零拷贝
    DefaultFileRegion: 以该文件的完整长度创建一个新的DefaultFileRegion
ChunkedWriteHandler: 使用ChunkedWriteHandler将数据从文件系统复制到用户内存中，它支持异步写大型数据流，而又不会导致大量的内存消耗。
    ChunkedInput<B>：其中类型参数B 是readChunk()方法返回的类型
    ChunkedStream 的用法，它是实践中最常用的实现.
注意:逐块输入 要使用你自己的ChunkedInput 实现，请在ChannelPipeline 中安装一个 ChunkedWriteHandler。

ChunkedFile 从文件中逐块获取数据，当你的平台不支持零拷贝或者你需要转换数据时使用
ChunkedNioFile 和ChunkedFile 类似，只是它使用了FileChannel
ChunkedStream 从InputStream 中逐块传输内容
ChunkedNioStream 从ReadableByteChannel 中逐块传输内容

11. JDK 序列化 / JBoss Marshalling 序列化 / Protocol Buffers 序列化
ProtobufDecoder： 使用protobuf 对消息进行解码
ProtobufEncoder： 使用protobuf 对消息进行编码
ProtobufVarint32FrameDecoder： 根据消息中的Google Protocol Buffers 的“Base 128 Varints” 整型长度字段值动态地分割所接收到的ByteBuf
ProtobufVarint32LengthFieldPrepender： 向ByteBuf 前追加一个Google Protocal Buffers 的“Base 128 Varints”整型的长度字段值