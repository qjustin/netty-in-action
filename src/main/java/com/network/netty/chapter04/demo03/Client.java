package com.network.netty.chapter04.demo03;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            b.group(group).channel(NioSocketChannel.class).remoteAddress("localhost",1127)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                @Override
                                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                    System.out.println(msg.toString());
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("received!", CharsetUtil.UTF_8));
                                }
                            });
                        }
                    });

            ChannelFuture future = b.connect().sync();
            future.channel().closeFuture().sync();

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {

            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
