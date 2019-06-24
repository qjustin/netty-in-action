1. 编码器和解码器都是一个ChannelHandler
2. 解码器
ByteToMessageDecoder:将字节转换为消息。由于底层传输的时字节流，因此可以将它作为最底层的一个解码器。
MessageToMessageDecoder:将一种消息解码为另一种消息

ReplayingDecoder:ByteToMessageDecoder读取数据时(调用readInt之前)不得不验证byteBuff.readableBytes()是否有足够的数据.
而ReplayingDecoder则不需要调用readableBytes做检查，decode方法传入的参数byteBuff内部已经调用过了readableBytes。

建议: 编码器尽量使用ByteToMessageDecoder。

LineBasedFrameDecoder它使用了行尾控制字符（\n 或者\r\n）来解析消息数据

TooLongFrameException:由于解码器不能缓冲大量的数据。帧超出指定的大小限制时抛出改异常。需要注意的是，
                        如果你正在使用一个可变帧大小的协议，那么这种保护措施将是尤为重要的。

3. 编码器
为什么解码器有两个实现方法?因为，关闭之后仍然产生一个消息是毫无意义的。

MessageToByteEncoder
MessageToMessageEncoder

4. 抽象(编解)码器类
2和3将编码与解码独立讨论(在不同的类中实现)，有时在同一个类中管理入站和出站数据和消息的转换是很有用，例如：
任何的请求/响应协议都可以作为使用ByteToMessageCodec的理想选择。

Netty同样也提供两个基础类：

// 只要有字节可以被消费，这个方法就将会被调用。它将入站ByteBuf 转换为指定的消息格式， 并将其转发给ChannelPipeline 中的下一个ChannelInboundHandler
ByteToMessageCodecdecode(ChannelHandlerContext ctx,ByteBuf in,List<Object>)

// 这个方法的默认实现委托给了decode()方法。它只会在Channel的状态变为非活动时被调用一次。它可以被重写以实现特殊的处理
decodeLast(ChannelHandlerContext ctx,ByteBuf in,List<Object> out)

// 对于每个将被编码并写入出站ByteBuf 的（类型为I 的）消息来说，这个方法都将会被调用
encode(ChannelHandlerContext ctx,I msg,ByteBuf out)

MessageToMessageCodec

// 这个方法被调用时会被传入INBOUND_IN 类型的消息。它将把它们解码为OUTBOUND_IN 类型的消息，这些消息将被转发给ChannelPipeline 中的下一个ChannelInboundHandler
protected abstract decode(ChannelHandlerContext ctx,INBOUND_IN msg,List<Object> out)

// 对于每个OUTBOUND_IN 类型的消息，这个方法都将会被调用。这些消息将会被编码为INBOUND_IN 类型的消息，然后被转发给ChannelPipeline 中的下一个ChannelOutboundHandler
protected abstract encode(ChannelHandlerContext ctx,OUTBOUND_IN msg,List<Object> out)


5. CombinedChannelDuplexHandler 类
结合一个解码器和编码器可能会对可重用性造成影响，使用CombinedChannelDuplexHandler能够避免这种惩罚，又不会牺牲将一个解码器和一个编码器作为一个单独的单元部署所带来的便利性。