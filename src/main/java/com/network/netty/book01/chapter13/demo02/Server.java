package com.network.netty.book01.chapter13.demo02;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

public class Server {
    public static void main(String[] args) {
        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        EventLoopGroup bossgroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        Channel channel = null;

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();

            bootstrap.group(bossgroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChatServerChildHandlerInitializer(channelGroup));

            ChannelFuture future = bootstrap.bind(1127).sync();
            future.syncUninterruptibly();
            channel = future.channel();
            future.channel().closeFuture().syncUninterruptibly();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                if (channel != null) {
                    channel.close();
                }
                channelGroup.close();
                bossgroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
