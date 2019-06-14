package com.network.netty.chapter04.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * 知识点
 * <p>
 * 1. 传输API的核心接口 Channel接口
 * 2. 新建的Channel会与那些组件绑定呢？
 * 3. Channel提供了那些方法？
 */

/**
 * 技巧：
 * 1. Channel的用法，Channel是线程安全的，多线程使用同一个Channel写消息 参考demo03
 *      扩散写是否可以使用这种方式呢?
 */
public class NettyNioServer {

    public void server(int port) throws Exception {
        final ByteBuf buf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hi!\r\n", Charset.forName("UTF-8")));
        // 事件循环组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 引导服务器配置
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                    /**
                     * 2. 新建的Channel会与那些组件绑定呢？
                     *      EventLoop
                     *      ChannelPipeline
                     *      ChannelConfig
                     */
                    .channel(NioServerSocketChannel.class)
                    .localAddress(new InetSocketAddress(port))
                    // 初始化handlers
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            /**
                             * Channel提供了那些方法？
                             * channel.eventLoop(), 返回分配给Channel的EventLoop
                             * channel.localAddress(), 已绑定的本地SocketAddress
                             * channel.write(), 写数据到远程客户端
                             * channel.writeAndFlush() 等同于调用wirte后立即调用flush方法
                             * channel.flush() 将缓冲区写入channel
                             * channel.pipeline() 分配给channel的channelPipeline
                             */
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                                    // 连接后，写消息到客户端，写完后关闭连接
                                    channelHandlerContext.write(buf.duplicate())
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });
            // 绑定服务器
            ChannelFuture f = b.bind().sync();
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }
}
