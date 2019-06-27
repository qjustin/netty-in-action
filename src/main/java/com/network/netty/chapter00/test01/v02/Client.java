package com.network.netty.chapter00.test01.v02;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Client {
    private static final String SERVER_HOST = "192.168.6.201";

    public static void main(String[] args)   {
        final int beginPort = 8000;
        final int nPort = 100;

        System.out.println("client starting....");
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
        final Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) {
            }
        });
        int index = 0;
        int port;
        while (!Thread.interrupted()) {
            port = beginPort + index;
            try {
                ChannelFuture channelFuture = bootstrap.connect(SERVER_HOST, port);
                channelFuture.addListener((ChannelFutureListener) future -> {
                    if (!future.isSuccess()) {
                        System.out.println("连接失败, 退出!");
                        System.exit(0);
                    }
                });
                channelFuture.get();
            } catch (Exception e) {
            }
            if (++index == nPort) {
                index = 0;
            }
        }
    }
}
