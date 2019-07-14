package com.network.netty.book01.chapter08.demo03;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.util.concurrent.TimeUnit;

public class Client {
    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        EventLoopGroup g = new NioEventLoopGroup();

        try {
            b.group(g).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    channel.eventLoop().scheduleAtFixedRate(() -> {
                                        ctx.writeAndFlush(Unpooled.copiedBuffer("heartbeat", CharsetUtil.UTF_8));
                                    }, 5, 5, TimeUnit.SECONDS);
                                }

                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(((ByteBuf) msg).toString(CharsetUtil.UTF_8));
                                    ReferenceCountUtil.release(msg);
                                }
                            });
                        }
                    });
            ChannelFuture f = b.connect("localhost", 1127).sync();
            f.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        System.out.println("Client connected to ProxyServer success");
                    } else {
                        System.out.println("Client connected to ProxyServer failed");
                        channelFuture.cause().printStackTrace();
                    }
                }
            });
            f.channel().closeFuture().sync();
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
