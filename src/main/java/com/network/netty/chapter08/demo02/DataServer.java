package com.network.netty.chapter08.demo02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

public class DataServer {
    public static void main(String[] args) {
        ServerBootstrap boot = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            boot.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new SimpleChannelInboundHandler<ByteBuf>() {
                                @Override
                                protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                    System.out.println("DataServer Receive data:" + byteBuf.toString(CharsetUtil.UTF_8));
                                    channelHandlerContext.writeAndFlush(Unpooled.copiedBuffer("Data:{'age':'18', 'name':'justin'}\r\n", CharsetUtil.UTF_8))
                                            .addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });

            ChannelFuture future = boot.bind(1128).sync();

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess())
                        System.out.println("DataServer[localhost:1128] started");
                    else
                        System.out.println("DataServer[localhost:1128] start failed");
                }
            });

            future.channel().closeFuture().sync();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();
                workGroup.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
