package com.network.netty.chapter08.demo03;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class ProxyServer {
    public static void main(String[] args) {
        ServerBootstrap boot = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            boot.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(
                                    new ChannelInboundHandlerAdapter() {
                                        ChannelFuture future;

                                        @Override
                                        public void channelActive(ChannelHandlerContext context) throws Exception {
                                            Bootstrap b = new Bootstrap();
                                            b.channel(NioSocketChannel.class)
                                                    .handler(new ChannelInboundHandlerAdapter() {
                                                        @Override
                                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                                            context.writeAndFlush(msg);
                                                        }
                                                    });
                                            b.group(context.channel().eventLoop());
                                            future = b.connect("localhost", 1128);
                                            future.addListener(new ChannelFutureListener() {
                                                @Override
                                                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                                                    if (channelFuture.isSuccess()) {
                                                        System.out.println("ProxyServer connected to DataServer success");
                                                    } else {
                                                        System.out.println("ProxyServer connected to DataServer failed");
                                                        channelFuture.cause().printStackTrace();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
                                            if (future.isDone()) {
                                                // 发送数据给DataServer
                                                future.channel().writeAndFlush(msg);
                                            }
                                        }
                                    });
                        }
                    });

            ChannelFuture future = boot.bind(1127).sync();

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("ProxyServer[localhost:1127] started");
                    } else {
                        System.out.println("ProxyServer[localhost:1127] start failed");
                        channelFuture.cause().printStackTrace();
                    }
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
