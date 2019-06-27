package com.network.netty.chapter00.test03.v02;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * java -Xmx3100m -Xms3100m -jar iotest.one-jar.jar
 */
public class Client {
    public static void main(String[] args) {
        // 服务器监听起始端口
        int beginPort = 8000;
        // 服务器监听端口数量
        int nPort = 200;

        EventLoopGroup g = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(g)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ClientInitializer());

            for (int i = 0; i < 50000; i++) {
                int port = beginPort + (i % nPort);
                try {
                    ChannelFuture future = b.connect("192.168.6.201", port).addListener(new ChannelFutureListener() {
                        @Override
                        public void operationComplete(ChannelFuture channelFuture) throws Exception {
                            if (!channelFuture.isSuccess()) {
                                System.out.println("连接失败, Channel.id:" + channelFuture.channel().id());
                            }
                        }
                    });
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
//            try {
//                g.shutdownGracefully().sync();
//            } catch (Exception ex) {
//                System.out.println(ex.getMessage());
//            }
        }
    }


}
