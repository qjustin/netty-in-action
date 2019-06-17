package com.network.netty.chapter04.demo03;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class Server {
    public static void main(String[] args) {
        ServerBootstrap b = new ServerBootstrap();
        EventLoopGroup g = new NioEventLoopGroup();

        try {
            b.group(g).channel(NioServerSocketChannel.class).localAddress(1127)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(((ByteBuf)msg).toString(CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });
            ChannelFuture future = b.bind().sync();
            Channel channel = future.channel();
            Runnable writer = new Runnable() {
                @Override
                public void run() {
                    channel.pipeline().writeAndFlush(Unpooled.copiedBuffer("hello\r\n", CharsetUtil.UTF_8));
                }
            };
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                g.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
