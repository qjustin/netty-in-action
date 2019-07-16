package com.network.netty.book02.chapter04.demo01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class Server {
    public static void main(String[] args) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup,workerGroup);
            boot.channel(NioServerSocketChannel.class);
            boot.childHandler(new ChannelInitializer<Channel>() {
                @Override
                protected void initChannel(Channel channel) throws Exception {
                    channel.pipeline().addLast(new TimeServerHandler());
                }
            });
            ChannelFuture future = boot.bind(1127).sync();
            future.channel().closeFuture().sync();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                bossGroup.shutdownGracefully().sync();
                workerGroup.shutdownGracefully().sync();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
