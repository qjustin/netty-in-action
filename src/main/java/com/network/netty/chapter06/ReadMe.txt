
知识点
ChannelHandler：
1. ChannelHandler ChannelInboundHandler ChannelOutboundHander 的关系以及他们的生命周期
2. ChannelHandlerAdapter ChannelInboundHandlerAdapter ChannelOutboundHanderAdapter 作为 ChannelHandler的默认实现
3. 调用 ChannelInboundHandler.channelRead() 或 ChannelOutboundHandler.write() 需要确保没有任何资源泄漏。
    Netty使用引用计数器来池化ByteBuf，因此使用完ByteBuf后，调整它的引用计数很重要。
       channelRead/write 方法内调用 ReferenceCountUtil.release(msg); 释放资源
    泄露检测: 启动java程序时加上参数：java -Dio.netty.leakDetectionLevel=ADVANCED

4. SimpleChannelInboundHandler 时一个特殊实现，这个实现会在消息被channelRead0() 方法消费之后自动释放消息。
5. 如果一个消息被丢弃或被消费，并且没有传递给ChannelPipeline中的下一个Handler，那么就必须调用ReferenceCountUtil.release(msg); 释放资源

ChannelPipeline

6. Netty 总是 从左边入站（头）从右边出站（尾）
   （头） inbound1 -> inbound2 -> outbound3 -> inbound4 -> outbund5（尾）
    入站第一个被执行的是inbound1，出站第一个被执行的是outbound5

7. 修改ChannelPipeline

8. 绝对不能阻塞ChannelHandler的执行（阻塞EventLoop线程的执行）。
      使用 ChannelPipeline的add(接受一个EventExecutorGroup 作为参数)方法避免阻塞。参考DefaultEventExecutorGroup

9. ChannelPipeline有那些API用于响应出站和入站事件？

ChannelHandlerContext

10. ChannelHandlerContext的很多方法在Channel和ChannelPipeline也有。区别在于：
如果：Channel和ChannelPipeline调用这些方法，他们将沿着整个ChannelPipeline进行转播，而ChannelHandlerContext上调用，从从当前的Handler开始
传递给下一个能处理该事件的Handler。
        channel.write() & ctx.pipeline().write() & ctx.write() 的区别

11. 牢记：
    ChannelHandlerContext与ChannelHandler 之间的绑定永远无法改变，所以缓存对它的引用是安全的。
    ChannelHandlerContext产生的事件流更短，充分利用这个特性来获得最大性能。

12. 从ChannelHandlerContext获取的channel上调用write方法会导致事件从尾端流经整个pipeline
        channel.write()
    从ChannelHandlerContext获取pipeline上调用write方法也会导致事件从尾端流经整个pipeline
        ctx.pipeline().write()
    channel.write() 和 ctx.pipeline().write() 在ChannelHandler事件级别上相同，事件从一个ChannelHandler
    到下一个ChannelHandler的移动是由ChannelHandlerContext上的调用完成

13. 通过ChannelHandlerContex上的pipeline()方法获得ChannelPipeline的引用，然后获得pipeline包含的handler，来进行复杂的设计。（实现动态协议切换）

14. @Sharable 用法：一个ChannelHandler 可以绑定到多个Pipeline和多个ChannelHandlerContext实例。这种ChannelHandler,必须使用@Sharable注解
    这种ChannelHandler必须是线程安全的。
    // 记录方法调用，并转发给下一个ChannelHandler
    channelRead() { ... ctx.fireChannelRead(msg); }

15. 为什么要共享一个ChannelHandler？跨Channel收集信息。

异常处理

16. 入站重写ChannelInboundHandler 的exceptionCaught 方法处理异常。异常事件也会流经整个Pipeline，因此这个异常处理Handler在Pipeline的最后，这样就能处理所有异常

17. 出站异常
    每个出站操作都会返回ChannelFuture，注册到ChannelFuture的ChannelFutureListener将在操作完成时被通知该操作是成功还是失败。
        方法1.在返回的ChannelFuture.addListener();
    所有ChannelOutboundHandler方法都会传入一个ChannelPromise的实例，
        方法2. 在ChannelPromise实例上监听ChannelPromise.addListener()

    setSuccess() / setFailure() 立即触发监听。

技巧：
1. 在ChannelOutboundHandler.write()方法中调用  ReferenceCountUtil.release(msg);释放消息，还要通知ChannelPromise（promiss.setSucccess()）
2. 不要阻塞EventLoop线程： 使用 ChannelPipeline的add(接受一个EventExecutorGroup 作为参数)方法避免阻塞。参考DefaultEventExecutorGroup
3. ChannelHandlerContext产生的事件流更短，充分利用这个特性来获得最大性能。
4. 注意 channel.write() &  ctx.pipeline().write() 的区别
5. 缓存ChannelHandlerContext的引用供稍后使用，
6. 在多个ChannelPipeline中安装同一个ChannelHandler的常见原因是收集跨多个Channel的统计信息。

