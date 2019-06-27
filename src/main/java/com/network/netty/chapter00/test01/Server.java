package com.network.netty.chapter00.test01;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * https://blog.csdn.net/usagoole/article/details/88025354
 *
 * java -Xmx3100m -Xms3100m -jar iotest.one-jar.jar
 */
public class Server {
    public static void main(String[] args) {
        int beginPort = 10000;
        int nPort = 200;

        System.out.println("server starting....");
        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ChannelFuture[] channelFutures = new ChannelFuture[nPort];

        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);
        bootstrap.childHandler(new ConnectionCountHandler());

        try {
            for (int i = 0; i < nPort; i++) {
                int port = beginPort + i;
                // 要同步执行 .sync()
                ChannelFuture channelFuture = bootstrap.bind(port).sync();
                channelFutures[i] = channelFuture;
                channelFutures[i].addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        if (future.isSuccess()) {
                            System.out.println("server bind to port: " + port);
                        } else {
                            System.out.println("connect failed port : " + port);
                        }
                    }
                });
            }

            for (int i = 0; i < nPort; i++) {
                int index = i;
                channelFutures[index].channel().closeFuture().addListener(new GenericFutureListener<Future<? super Void>>() {
                    @Override
                    public void operationComplete(Future<? super Void> future) throws Exception {
                        System.out.println("channel close");
                        channelFutures[index] = null;
                    }
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            // 如果不注释掉 shutdownGracefully（）将被执行 上面的代码没有阻塞
//            try {
//                bossGroup.shutdownGracefully().sync();
//                workerGroup.shutdownGracefully().sync();
//            } catch (InterruptedException ex) {
//                ex.printStackTrace();
//            }
        }
    }
}
