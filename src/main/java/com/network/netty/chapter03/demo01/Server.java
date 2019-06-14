package com.network.netty.chapter03.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

/**
 * 知识点
 * 1. 三大顶层接口
 * 2. EventLoopGroup EventLoop Thread Channel 的关系
 * 3. ChannelHandler ChannelHandlerContext 与 ChannelPipeline的关系
 * 4. 发送消息的两种方式
 * 5. 入站（读，需要解码）出站（写，需要编码）
 * 6. ChannelxxxBoundHandlerAdapter类的意义
 * 7. 编码和解密以及默认实现Handler, SimpleChannleInboutnHandler<T>
 * 8. Bootstrap & ServerBootstrap  引导类为网络成配置提供了容器 以及区别。
 * 9. 为什么ServerBootstrap需要两个EventLoopGroup
 */

public class Server {
    /**
     * 三大顶层接口
     *
     * Channel（连接）：Channel相当于Socket，它降低了创建Socket的复杂性；
     * EventLoop（执行）：控制流，并发，执行业务逻辑线程
     * ChannelFuture（结果）：异步通知
     */
    public static void main(String[] args) {
        /**
         * Bootstrap 用于客户端，连接到远程主机和端口，EventLoopGroup 的数目为 1
         * ServerBootstrap 用于服务端，进程绑定到本地端口用于监听, EventLoopGroup 的数目为 2(可以是同一个实例，也可以是不同实例)
         *
         * 为什么ServerBootstrap需要两个EventLoopGroup？
         *
         * 因为服务器需要两组不同的Channel：
         * 第一组只包含一个ServerChannel，代表服务器自己，这个ServerChannel与一个Channel（线程）绑定专门监听进入的连接并创建SubChannel
         * 第二组包含多个SubChannel（客户端连接），并为每个SubChannel分配一个线程，然后SubChannel上的事件
         */

        ServerBootstrap serverBootstrap = new ServerBootstrap();

        /**
         * EventLoopGroup EventLoop Thread Channel 的关系
         *
         *  EventLoopGroup 包含一个或多个 EventLoop
         *  一个EventLoop会被分配给多个Channel
         *  一个EventLoop与一个线程绑定，EventLoop上所有的IO事件都由与它绑定的线程来处理
         *  Channel只会分配一个EventLoop给它
         *  换句话说
         *  线程池(EventLoopGroup) 线程(EventLoop)
         *  线程池包含y一个或多个线程，每个线程会被绑定到一个或多个Channel上，一个Channel仅绑定一个线程Channel上所有的事件由该线程执行
         *
         */

        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        try {
            serverBootstrap.group(eventLoopGroup)

                    /**
                     * Channel:相当于Socket，它降低了创建Socket的复杂性；
                     * 内置传输：
                     *      NioServerSocketChannel;
                     *      NioSocketChannel;
                     *      LocalServerChannel;
                     *      LocalChannel;
                     *      NioSctpServerChannel;
                     *      NioSctpChannel;
                     *      EpollEventLoopGroup
                     *      EmbeddedChannel;
                     *  ChannelHandler:业务逻辑容器
                     *  ChannelPipeline：ChannelHandler的容器，并定义了入站和出站事件流API，
                     *  而ChannelHandler就用于处理这些事件
                     *
                     *  ChannelHandler ， ChannelHandlerContext 与 ChannelPipeline的关系？
                     *  ChannelPipeline将一个或多个ChannelHandler串联起来,添加ChannelHandler到
                     *  ChannelPipeline时会为ChannelHandler分配一个ChannelHandlerContext，
                     *  因此Context代表了Pipeline与Handler的关系。ChannelHandlerContext主要用于写出站数据。
                     *
                     *  从Client角度看，事件运动方向从Client到Server称为出站事件，反之为入站事件
                     *
                     *  入站从Pipeline头部开始执行，出站从尾部开始执行
                     *
                     *  发送消息的两种方式：
                     *  通过Channel发送（直接出站）：导致消息从ChannelPipeline的尾部开始流动。
                     *  通过ChannelHandlerContext发送:从Piplineline中的下一个ChannelHandler开始流动
                     */
                    .channel(NioServerSocketChannel.class)
                    .localAddress(1127)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi\r\n", CharsetUtil.UTF_8)))
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });

            /**
             * ChannelFuture接口：ChannelFuture接口提供的方法用于对异步返回的结果进行处理
             * 可以通过addListener()方法注册一个ChannelFutureListener回调,在某个操作结时执行
             */
            ChannelFuture future = serverBootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                eventLoopGroup.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }

    }
}
