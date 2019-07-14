package com.network.netty.book01.chapter00.test03.v01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * java -Xmx3100m -Xms3100m -jar iotest.one-jar.jar
 */
public class Server {
    public static void main(String[] args) {
        // 服务器监听起始端口
        int beginPort = 8000;
        // 服务器监听端口数量
        int nPort = 200;
        System.out.println("server starting....");
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
        bootstrap.childHandler(new ServerInitializer());

        for (int i = 0; i < nPort; i++) {
            int port = beginPort + i;
            bootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                System.out.println("bind success in port: " + port);
            });
        }
    }
}
