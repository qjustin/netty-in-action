package com.network.netty.book01.chapter08.demo01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

public class Client {
    public static void main(String[] args) {
        Bootstrap boot = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            boot.group(group).channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelInboundHandlerAdapter(){
                                @Override
                                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                    ctx.writeAndFlush(Unpooled.copiedBuffer("Hello\r\n", CharsetUtil.UTF_8)).addListener(ChannelFutureListener.CLOSE);
                                }
                            });
                        }
                    });

            ChannelFuture future = boot.connect("localhost", 1127).sync();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
