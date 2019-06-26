package com.network.netty.example.demo02;

import com.network.netty.chapter00.test03.ConnectionCountHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * java -Xmx3100m -Xms3100m -jar iotest.one-jar.jar
 */
public class Server {
    public static void main(String[] args) {

        int beginPort = 8000;
        int nPort = 100;
        System.out.println("server starting....");
        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.childOption(ChannelOption.SO_REUSEADDR, true);

        bootstrap.childHandler(new ChildChannelHandlerInitializer(channelGroup));

        for (int i = 0; i < nPort; i++) {
            int port = beginPort + i;
            bootstrap.bind(port).addListener((ChannelFutureListener) future -> {
                System.out.println("bind success in port: " + port);
            });
        }
        System.out.println("server started!");

//        // 服务器监听起始端口
//        int beginPort = 8000;
//        // 服务器监听端口数量
//        int nPort = 100;
//
//        ChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
//        EventLoopGroup bossGroup = new NioEventLoopGroup();
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//
//        try {
//            ServerBootstrap boot = new ServerBootstrap();
//            boot.group(bossGroup, workerGroup)
//                    .channel(NioServerSocketChannel.class)
//                    .childOption(ChannelOption.SO_REUSEADDR, true)
//                    .childHandler(new ChildChannelHandlerInitializer(channelGroup));
//
//            for (int i = 0; i < nPort; i++) {
//                int port = beginPort + i;
//                try {
//                    boot.bind(port).addListener(new ChannelFutureListener() {
//                        @Override
//                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
//                            System.out.println("bind success in port: " + port);
//                        }
//                    });
//                } catch (Exception ex) {
//                    System.out.println(ex.getMessage());
//                }
//            }
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        } finally {
//            try {
//                bossGroup.shutdownGracefully().sync();
//                workerGroup.shutdownGracefully().sync();
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }
//        }
    }
}
