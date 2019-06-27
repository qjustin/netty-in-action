package com.network.netty.chapter00.test02.v01;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
// https://www.jianshu.com/p/490e2981545c
public class Client {
    public static void main(String[] args) {
        final ByteBuf byteBuf = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Client connected to server", CharsetUtil.UTF_8));

        Bootstrap boot = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            boot.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_REUSEADDR, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                        }
                    });

           for(int i = 0; i < 10000; i++) {

                try {
                    ChannelFuture channelFuture = boot.connect("localhost", 1127);
                    channelFuture.addListener((ChannelFutureListener) future -> {
                        if (!future.isSuccess()) {
                            System.out.println("连接失败, 退出!");
                            System.exit(0);
                        }
                    });
                    channelFuture.get();
                } catch (Exception e) {

                }
            }
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
