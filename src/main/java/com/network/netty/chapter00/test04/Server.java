package com.network.netty.chapter00.test04;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;

public class Server {

    public static void main(String[] args) {
        DefaultChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

        ServerBootstrap bootstrap = new ServerBootstrap();
        EventLoopGroup group = new NioEventLoopGroup();

        int beginPort = 8000;
        int nPort = 1000;

        try {
            bootstrap.group(group).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            channel.pipeline().addLast(new ChannelOutboundHandlerAdapter(){

                            });
                        }
                    });
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
