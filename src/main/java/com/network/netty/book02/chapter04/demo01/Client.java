package com.network.netty.book02.chapter04.demo01;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    public static void main(String args[]) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap boot = new Bootstrap();
            boot.group(group);
            boot.channel(NioSocketChannel.class);
            boot.handler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(new TimeClientHandler());
                }
            });
            ChannelFuture future = boot.connect("localhost", 1127).syncUninterruptibly();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                group.shutdownGracefully().sync();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
