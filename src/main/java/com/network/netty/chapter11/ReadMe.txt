1. 编码器解码器都是一个ChannelHandler
2. 解码器
ByteToMessageDecoder:将字节转换为消息。由于底层传输的时字节流，因此可以将它作为最底层的一个解码器。
MessageToMessageDecoder:将一种消息解码为另一种消息

ReplayingDecoder:ByteToMessageDecoder读取数据时(调用readInt之前)不得不验证byteBuff.readableBytes()是否有足够的数据.
而ReplayingDecoder则不需要调用readableBytes做检查，decode方法传入的参数byteBuff内部已经调用过了readableBytes。

建议: 编码器尽量使用ByteToMessageDecoder。

LineBasedFrameDecoder它使用了行尾控制字符（\n 或者\r\n）来解析消息数据

TooLongFrameException

3. 编码器