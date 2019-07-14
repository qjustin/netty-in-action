read过程
上一篇博文整理出来，当channel收到消息后的处理流程

//类名或对象名::调用方法
NioEventLoop::processSelectedKey                           //1. 开始处理消息
unsafe::read                                               //2. 读取消息
pipeline::fireChannelRead                                  //3. 交给channel对应的pipeline处理消息
AbstractChannelHandlerContext::invokeChannelRead(head, msg)//4. 从pipeline的head开始调用channelRead
ctx::findContextInbound                                    //5. 从head开始寻找第一个inbound的handler
ctx::fireChannelRead                                       //6. 将read事件传递给handler
handler::channelRead                                       //7. 最终的业务逻辑所在

可以知道read过程，主要是在pipeline中从head开始寻找inboundhandler进行处理，如果需要传递下去给第二个handler处理，则需要ctx.fireChannelRead，则会继续在pipeline中寻找下一个inboundhandler。

以自己写的HttpFileServer为例，该server支持http协议，用来下载文件。

ServerBootstrap sb = new ServerBootstrap();
sb.group(master, worker)
  .channel(NioServerSocketChannel.class)
  .childHandler(new ChannelInitializer<Channel>() {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65536));
        ch.pipeline().addLast(new ChunkedWriteHandler());
        ch.pipeline().addLast(new HttpFileServerHandler());
    }
});

先分析pipeline是怎样的
在客户端连接接入后创建的NioSocketChannel的初始pipeline是这样的：
HeadContext(inbound/outbound)
    <-> TailContext(inbound)

在ServerBootstrapAcceptor的channelRead方法中为NioSocketChannel添加了用户设置的handler：
HeadContext(inbound/outbound)
    <-> ChannelInitializer(inbound)
        <-> TailContext(inbound)

ChannelInitializer实际并没有业务逻辑处理，当ChannelInitializer被加入到pipeline后，会调用器initChannel方法，将运行重写的initChannel方法并且移除自身，此时pipeline是这样的：
HeadContext(inbound/outbound)
    <-> HttpServerCode(inbound/outbound)
        <-> HttpObjectAggregator(inbound)       聚合成一个完整的请求
            <-> ChunkedWriteHandler(outbound)       大文件读写，读取文件到堆
                <-> HttpFileServerHandler(inbound)
                    <-> TailContext(inbound)

现在可以开始看msg在pipeline中的处理过程

HeadContext中的channelRead方法只做了一个事情，那就是传递给下一个handler
ctx.fireChannelRead(msg);

HttpServerCodec是一个特殊的handler，既实现了ChannleInboundHandler，又实现了ChannelOutboundHandler，其里面包含了两个handler变量，分别是HttpServerRequestDecoder类型和HttpServerResponseEncoder类型，负责Http解码和Http编码的工作。所以当有消息到了HttpServerCodec时，实际是HttpServerRequestDecoder在处理。

HttpServerRequestDecoder继承ByteToMessageDecoder。ByteToMessageDecoder是用来将Byte转化为message对象，其channelRead方法主要做了三个事情：
（1）收集数据
（2）调用重写的decode方法
（3）将数据传递给下一个handler
回到HttpServerRequestDecoder的encode方法，其实际是调用了HttpObjectDecoder的encode，在这里，会逐个部分的读取Http消息，包括请求行/请求头/body，并且对chunked类型的消息做了处理，最后会输出HttpMessage和HttpContent类型的对象到out里。

HttpObjectAggregator继承MessageToMessageDecoder。MessageToMessageDecoder是用来将message对象转化为另一个message对象，其主要做这几个事情：
（1）acceptInboundMessage，判断是否为可处理的类型，像此时，只接收HttpObject对象
（2）将传进来的对象强转型为HttpObject对象
（3）调用重写的decode方法
（4）释放掉旧message
（5）将新message传递给下一个handler
于是HttpObjectAggregator把HttpMessage和HttpContent组装成FullHttpMessage。
这里要注意的是，如果自己继承MessageToMessageDecoder，旧message在后面还会使用到，需要调用retain方法，否则会被释放掉。

ChunkedWriteHandler不为inboundhandler，直接跳过

HttpFileServerHandler用于实现业务逻辑，继承SimpleChannelInboundHandler。一般的业务逻辑处理器可以直接继承SimpleChannelInboundHandler，其handlerRead方法会做这些操作：
（1）acceptInboundMessage，判断是否为可处理的类型
（2）强转型
（3）调用重写的channelRead0
（4）释放消息
所以，HttpFileServerHandler类中只要重写channelRead0方法即可，需要注意的是，如果仍需要传递给下一个handler，需要手动fireChannelRead，如果msg在后面仍要用到，也需要调用msg.retain方法。
这里没有写fireChannelRead，所以读的流程到此为止，消息经历过了以下历程。


write过程
在HttpFileServerHandler中处理完业务逻辑之后，就需要把数据返回到客户端中，此时调用的是ctx.write方法。
write与read类似，当调用write方法时，会在当前ctx，往前找下一个outboundhandler，然后调用下一个的write方法。

ChunkedWriteHandler为写chunked类型的消息提供了支持。

HttpServerCodec内含的HttpServerResponseEncoder，继承MessageToMessageEncoder，负责把返回的消息根据Http协议转换成ByteBuf，如为请求头和请求行之间增加换行等。

channelInactive
另外，inactive事件与read类似，也是从head开始寻找inboundHandler。
https://blog.csdn.net/lblblblblzdx/article/details/81587503